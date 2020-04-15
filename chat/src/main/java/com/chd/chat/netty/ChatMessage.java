package com.chd.chat.netty;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChatMessage  implements Serializable{

	private static final long serialVersionUID = 6373860356252473358L;
	
	private String senderId;    // 发送者的用户id
	private String receiverId;  //接受者的用户id
	private String msg;          // 聊天内容
	private String msgId;       // 用于消息的签收
	

}
