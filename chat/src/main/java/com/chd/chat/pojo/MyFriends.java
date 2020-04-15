package com.chd.chat.pojo;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "my_friends")
public class MyFriends {
	
    @Id
    private String id;

    @Column(name = "my_user_id")
    private String myUserId;

    @Column(name = "my_friend_user_id")
    private String myFriendUserId;
    
    private String remark;
}