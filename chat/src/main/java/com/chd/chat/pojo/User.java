package com.chd.chat.pojo;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
	
    @Id
    @Column(name = "user_id")
    private String userId;
    private String account;

    @Column(name = "face_image")
    private String faceImage;

    @Column(name = "face_image_big")
    private String faceImageBig;
    private String nickname;
    private String qrcode;

}