package com.chd.chat.pojo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlterAccountRecord {
	
	@Id
	@Column(name = "user_id")
	private String userId;
	
	@Column(name = "alter_time")
	private Date alterTime;

}
