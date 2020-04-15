package com.chd.chat.pojo;

import java.util.Date;
import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Table(name = "friends_request")
public class FriendsRequest {
    @Id
    private String id;

    @Column(name = "send_user_id")
    private String sendUserId;

    @Column(name = "accept_user_id")
    private String acceptUserId;

    /**
     * 发送请求的事件
     */
    @Column(name = "request_date_time")
    private Date requestDateTime;

}