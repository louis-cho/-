package com.ssafy.server.user.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;


import com.ssafy.server.auth.model.dto.JwtCode;
import com.ssafy.server.auth.model.dto.Role;
import com.ssafy.server.auth.model.dto.Token;
import com.ssafy.server.auth.model.dto.TokenKey;
import com.ssafy.server.auth.util.JwtUtil;
import com.ssafy.server.user.document.UserDocument;
import com.ssafy.server.user.model.User;
import com.ssafy.server.user.model.UserAuth;
import com.ssafy.server.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<String> login(HttpServletRequest servletRequest,  @RequestBody JsonNode request) throws Exception{

        String type = request.get("type").asText();

        switch(type) {
            case "getPublicKey": {

                String ip = servletRequest.getRemoteAddr();
                String[] publicKey = userService.getPublicKey(ip);
                System.out.println(publicKey[0] + " " + publicKey[1]);
                ObjectNode jsonResponse = JsonNodeFactory.instance.objectNode();
                jsonResponse.put("modulus", publicKey[0]);
                jsonResponse.put("exponent", publicKey[1]);

                return ResponseEntity.ok(jsonResponse.toString());
            }

            case "login": {

                JsonNode idNode = request.get("id");
                JsonNode pwNode = request.get("pw");

                if(idNode == null || pwNode == null) {
                    // throw new InvalidParameterException("there is no id or pw");
                }

                String id = idNode.asText();
                String pw = pwNode.asText();
                String ip = servletRequest.getRemoteAddr();

                // JsonNode로 응답 생성
                ObjectNode jsonResponse = JsonNodeFactory.instance.objectNode();

                // DB 저장 데이터 비교하기
                UUID uuid = userService.validatePassword(id, pw, ip);

                if(uuid != null) {
                    // DB 저장 데이터와 일치하는 경우 토큰 발급
                    System.out.println("사용자 인증 성공");
                    int userPk = userService.getUserPk(uuid);
                    System.out.println("UserContoller.userPk: " + " " + userPk);
                    String refreshToken = jwtUtil.getSavedRefresh(userPk);
                    if(refreshToken != null && jwtUtil.validateToken(refreshToken) == JwtCode.ACCESS) {
                        // db에 저장된 refreshToken이 있고, 유효한 경우
                        String newAccessToken = jwtUtil.generateAccess(userPk, Role.USER.getKey());
                        System.out.println("db에 저장된 refreshToken이 있고, 유효한 경우, AccessToken 재발급: " + " " + newAccessToken);

                        jsonResponse.put(TokenKey.ACCESS.getKey(), newAccessToken);
                        jsonResponse.put(TokenKey.REFRESH.getKey(), refreshToken);
                    } else {
                        // db에 저장된  refreshToken이 없거나, 있어도 유효하지 않은 경우
                        Token newToken = jwtUtil.generateToken(userPk, Role.USER.getKey());
                        System.out.println("accessToken & refreshToken 둘다 발급.");
                        System.out.println(newToken.getAccessToken());
                        System.out.println(newToken.getRefreshToken());

                        jsonResponse.put(TokenKey.ACCESS.getKey(), newToken.getAccessToken());
                        jsonResponse.put(TokenKey.REFRESH.getKey(), newToken.getRefreshToken());
                    }

                    jsonResponse.put("uuid", uuid.toString());

                    return ResponseEntity.ok(jsonResponse.toString());
                } else {
                    // throw new InvalidCredentialException("fail to login");
                }
            }

            default: {
                // throw new InvalidPasswordException("Invalid request type");
            }
        }
        return null;
    }
    // register
    @PostMapping("/register")
    public ResponseEntity<String> register(HttpServletRequest servletRequest,  @RequestBody JsonNode request) throws Exception{

        String type = request.get("type").asText();

        switch(type) {
                case "register": {

                String ip = servletRequest.getRemoteAddr();

                // JsonNode로 응답 생성
                ObjectNode jsonResponse = JsonNodeFactory.instance.objectNode();

                String id = request.get("id").asText();
                String pw = request.get("pw").asText();
                String nickname = request.get("nickname").asText();


                try {
                    UserAuth userAuth = userService.createUserAuth(id, pw, ip);
                    User user = userService.createUser(userAuth, nickname);

                    if(userAuth != null && user != null) {
                        // JsonNode를 String으로 변환하여 반환
                        // 추후 자체 발행 토큰을 넣어줄 것
                        return ResponseEntity.ok(jsonResponse.toString());
                    } else {
                        log.error("유저가 생성되지 않았습니다");
                        return ResponseEntity.status(400).body(jsonResponse.toString());
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                    System.out.println("유저 생성 중 에러 발생");

                    return ResponseEntity.status(400).body(jsonResponse.toString());
                }
            }

            default: {
                throw new IllegalArgumentException("Invalid request type");
            }
        }
    }


    @GetMapping("/search/{nickname}")
    public ResponseEntity<List<UserDocument>> searchUsersByNickname(@PathVariable String nickname) {
        List<UserDocument> users = userService.searchUsersByNickname(nickname);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}
