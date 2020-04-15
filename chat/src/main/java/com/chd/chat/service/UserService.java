package com.chd.chat.service;

import java.util.List;

import com.chd.chat.netty.ChatMessage;
import com.chd.chat.pojo.ChatMsg;
import com.chd.chat.pojo.User;
import com.chd.chat.pojo.bo.MyFriendBO;
import com.chd.chat.pojo.vo.FriendRequestVO;
import com.chd.chat.pojo.vo.MyFriendsVO;

public interface UserService {

	/*
	 * 查询手机号是否注册
	 */
	public boolean queryPhonenumberIsExist(String phonenumber);
	
	
	/*
	 * 用户注册
	 */
	public void register(String phonenumber, String password);
	
	/*
	 * 用户注销
	 */
	public void closeAccount(String userId);
	
	/*
	 *  修改密码
	 */
	public void updatePassword(String userId, String password);
	
	/*
	 * 根据id查询用户
	 */
	public User queryUserById(String userId);
	
	/*
	 * 验证用户登录
	 */
	public User queryUserForLogin(String phonenumber, String password);
	
	/*
	 * 修改用户信息
	 */
	public String  updateUserInfo(User user);


	/*
	 * 搜索好友
	 */
	public Integer searchFriend(String myUserId, String friendAccount);
	
	/*
	 * 根据趣语号查询用户信息
	 */
	public User queryUserInfoByAccount(String account);

	/*
	 * 添加好友发送请求
	 */
	public void sendFriendRequest(String myUserId, String friendUsername);
	
	/**
	 *  查询好友请求
	 */
	public List<FriendRequestVO> queryFriendRequestList(String acceptUserId);
	
	/**
	 * 删除好友请求记录
	 */
	public void deleteFriendRequest(String sendUserId, String acceptUserId);
	
	/*
	 * 通过好友请求记录
	 */
	public void passFriendRequest(String sendUserId, String acceptUserId);
	
	/*
	 * 根据用户id查询好友列表
	 */
	public List<MyFriendsVO> queryFriends(String userId);
	
	/*
	 * 设置好友备注
	 */
	public List<MyFriendsVO> setFriendRemark(MyFriendBO myFriendBO);
	
	/*
	 * 保存聊天消息到数据库
	 */
	public String saveMsg(ChatMessage chatMessage);
	
	/*
	 * 批量签收消息
	 */
	public void updateMsgSigned(List<String> msgIdList);
	
	/*
	 * 获取未签收消息列表
	 */
	public List<ChatMsg> getUnReadMsgList(String acceptUserId);


	
}
