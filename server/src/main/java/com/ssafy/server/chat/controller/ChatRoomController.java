package com.ssafy.server.chat.controller;

import com.ssafy.server.chat.model.ChatRoom;
import com.ssafy.server.chat.model.UsersChats;
import com.ssafy.server.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/chatroom")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    // 채팅 리스트 화면
    @GetMapping("/list/{userId}")
    public Page<UsersChats> chatRoomList(@PathVariable long userId,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        System.out.println(chatRoomService.findAllRoomByUserId(userId, pageable));
        return chatRoomService.findAllRoomByUserId(userId, pageable);
    }

    @GetMapping("/info/{roomId}")
    public Optional<ChatRoom> getRoomInfo(@PathVariable long roomId) {return chatRoomService.getRoomInfo(roomId);}

    // 채팅방 생성
    @PostMapping("/create")
    public ChatRoom createRoom(@RequestParam String name, @RequestParam long host, @RequestParam List<String> guests) {
        return chatRoomService.createChatRoom(name, host, guests);
    }

    // 채팅방 참여 유저 리스트 반환
    @GetMapping("/list/users/{roomId}")
    public List<UsersChats> userList(@PathVariable long roomId) {
        return chatRoomService.getUserList(roomId);
    }

    //유저 초대
    @PostMapping("/invite/{roomId}")
    public void inviteUser(@PathVariable long roomId, @RequestParam List<String> guests){
        chatRoomService.inviteUser(guests, roomId);
    }

    //채팅방 입, 퇴장
    @PostMapping("/eoInfo/{roomId}")
    public void enterOutInfo(@PathVariable long roomId, @RequestParam long userId){
        chatRoomService.enterOutInfo(roomId, userId);
    }

    //방 나가기
    @PostMapping("/exit/{roomId}")
    public void exitRoom(@PathVariable long roomId, @RequestParam long userId){
        chatRoomService.exitRoom(userId, roomId);
    }
}