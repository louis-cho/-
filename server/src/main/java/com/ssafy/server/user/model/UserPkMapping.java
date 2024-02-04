package com.ssafy.server.user.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "user_pk_mapping")
@Getter
@Setter
public class UserPkMapping {

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_pk")
    private int userPk;
}