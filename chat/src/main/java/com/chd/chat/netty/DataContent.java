package com.chd.chat.netty;

import java.io.Serializable;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataContent implements Serializable{

	private static final long serialVersionUID = 8293528899235946492L;
	
	private Integer action;         // 动作类型
	private ChatMessage chatMessage;  //用户的聊天内容
	private String extand;         //扩展字段
}
