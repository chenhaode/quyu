package com.chd.chat.mapper;

import java.util.List;

import com.chd.chat.pojo.User;
import com.chd.chat.pojo.vo.FriendRequestVO;
import com.chd.chat.pojo.vo.MyFriendsVO;
import com.chd.chat.utils.MyMapper;



public interface UserMapperCustom extends MyMapper<User> {
	
	public List<FriendRequestVO> queryFriendRequestList(String acceptUserId);
	
	public List<MyFriendsVO> queryFriends(String userId);

	public void batchUpdateMsgSigned(List<String> msgIdList);
}