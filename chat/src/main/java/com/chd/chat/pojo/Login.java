package com.chd.chat.pojo;

import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Login {
	
	@Id
	private String id;
	private String phonenumber;
	private String password;

}
