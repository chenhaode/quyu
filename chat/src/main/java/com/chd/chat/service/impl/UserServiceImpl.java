package com.chd.chat.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.chd.chat.enums.MsgSignFlagEnum;
import com.chd.chat.enums.SearchFriendsStatusEnum;
import com.chd.chat.mapper.AlterAccountRecordMapper;
import com.chd.chat.mapper.ChatMsgMapper;
import com.chd.chat.mapper.FriendsRequestMapper;
import com.chd.chat.mapper.LoginMapper;
import com.chd.chat.mapper.MyFriendsMapper;
import com.chd.chat.mapper.UserMapper;
import com.chd.chat.mapper.UserMapperCustom;
import com.chd.chat.netty.ChatMessage;
import com.chd.chat.pojo.AlterAccountRecord;
import com.chd.chat.pojo.ChatMsg;
import com.chd.chat.pojo.FriendsRequest;
import com.chd.chat.pojo.Login;
import com.chd.chat.pojo.MyFriends;
import com.chd.chat.pojo.User;
import com.chd.chat.pojo.bo.MyFriendBO;
import com.chd.chat.pojo.vo.FriendRequestVO;
import com.chd.chat.pojo.vo.MyFriendsVO;
import com.chd.chat.service.UserService;
import com.chd.chat.utils.DateUtils;
import com.chd.chat.utils.FastDFSClient;
import com.chd.chat.utils.FileUtils;
import com.chd.chat.utils.QRCodeUtils;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private MyFriendsMapper myFriendsMapper;

	@Autowired
	private FriendsRequestMapper friendsRequestMapper;

	@Autowired
	private ChatMsgMapper chatMsgMapper;

	@Autowired
	private LoginMapper loginMapper;

	@Autowired
	private Sid sid;

	@Autowired
	private QRCodeUtils qrCodeUtils;

	@Autowired
	private FastDFSClient fastDFSClient;

	@Value("${imgStorageUrl.qrcodeUrl}")
	private String qrcodeUrl;

	@Autowired
	private UserMapperCustom usersMapperCustom;

	@Autowired
	private AlterAccountRecordMapper alterAccountRecordMapper;

	@Override
	public boolean queryPhonenumberIsExist(String phonenumber) {

		Login login = new Login();
		login.setPhonenumber(phonenumber);

		Login result = loginMapper.selectOne(login);

		return result != null ? true : false;
	}

	@Override
	public User queryUserForLogin(String phonenumber, String password) {

		Example example = new Example(Login.class);
		Criteria criteria = example.createCriteria();

		criteria.andEqualTo("phonenumber", phonenumber);
		criteria.andEqualTo("password", password);

		Login login = loginMapper.selectOneByExample(example);
		if (login != null) {
			return queryUserById(login.getId());
		}
		return null;
	

	}

	@Transactional
	@Override
	public void register(String phonenumber, String password) {
		// 为用户生成一个唯一的id
		String id = sid.nextShort();

		Login login = new Login();
		login.setId(id);
		login.setPassword(password);
		login.setPhonenumber(phonenumber);

		// 用户注册成功的时候，同时向用户表和登录表添加数据
		saveUser(id);
		loginMapper.insert(login);

	}
	
	@Transactional
	@Override
	public void closeAccount(String userId) {
	    // 用户注销，删除用户的注册信息以及登录表中的信息
		loginMapper.deleteByPrimaryKey(userId);
		userMapper.deleteByPrimaryKey(userId);
	}

	
	@Override
	public void updatePassword(String userId, String password) {
		Example example = new Example(Login.class);
		Criteria criteria = example.createCriteria();
		
		Login login = new Login();
		login.setId(userId);
		login.setPassword(password);
		loginMapper.updateByPrimaryKeySelective(login);
		
	}




	private void saveUser(String userId) {

		User user = new User();

		// 为用户生成默认趣语号和默认昵称
		String account = "quyu_" + userId;
		String nickname = account;

		// 为每个用户生成一个唯一的二维码
		// 暂存的文件地址
		String qrCodePath = qrcodeUrl + userId + "qrcode.png";
		qrCodeUtils.createQRCode(qrCodePath, "qrcode:" + account);
		MultipartFile qrCodeFile = FileUtils.fileToMultipart(qrCodePath);

		String qrCodeUrl = "";
		try {
			qrCodeUrl = fastDFSClient.uploadQRCode(qrCodeFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

		user.setUserId(userId);
		user.setAccount(account);
		user.setNickname(nickname);
		user.setQrcode(qrCodeUrl);

		userMapper.insert(user);
	}

	@Transactional
	@Override
	public String updateUserInfo(User user) {
		/*
		 * 修改用户个人信息 1、修改昵称 2、修改趣语号 3、上传头像
		 */

		// 修改趣语号的情况
		if (StringUtils.isNotBlank(user.getAccount())) {
			// 判断是否存在修改趣语号的记录
			AlterAccountRecord alterAccountRecord = alterAccountRecordMapper.selectByPrimaryKey(user.getUserId());

			if (alterAccountRecord != null) {
				// 判断用户15天之内是否修改过趣语号
				Date alterTime = alterAccountRecord.getAlterTime();
				Date currentTime = new Date();

				if (currentTime.getTime() > alterTime.getTime()
						&& currentTime.getTime() < DateUtils.getPastDate(alterTime, 30).getTime()) {
					// 用户一个月内修改过趣语号
					return "你30天内修改过趣语号，无法修改";
				} else {
					// 更新用户修改趣语号的修改时间
					AlterAccountRecord accountRecord = new AlterAccountRecord();
					accountRecord.setUserId(user.getUserId());
					accountRecord.setAlterTime(new Date());
					alterAccountRecordMapper.updateByPrimaryKeySelective(accountRecord);
				}
			} else {
				// 插入修改记录
				AlterAccountRecord accountRecord = new AlterAccountRecord();
				accountRecord.setUserId(user.getUserId());
				accountRecord.setAlterTime(new Date());
				alterAccountRecordMapper.insert(accountRecord);
			}
		}

		// 同时更新二维码内的内容
		// 暂存的文件地址
		String qrCodePath = qrcodeUrl + user.getUserId() + "qrcode.png";
		qrCodeUtils.createQRCode(qrCodePath, "qrcode:" + user.getAccount());
		MultipartFile qrCodeFile = FileUtils.fileToMultipart(qrCodePath);

		String qrCodeUrl = "";
		try {
			qrCodeUrl = fastDFSClient.uploadQRCode(qrCodeFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

		user.setQrcode(qrCodeUrl);
		userMapper.updateByPrimaryKeySelective(user);

		return "修改成功";

	}

	@Override
	public User queryUserById(String userId) {
		return userMapper.selectByPrimaryKey(userId);
	}

	@Transactional
	@Override
	public Integer searchFriend(String myUserId, String friendAccount) {

		// 根据我们所定义的搜索好友枚举，作对应的结果判断
		/*
		 * SUCCESS(0, "OK"), USER_NOT_EXIST(1, "无此用户..."), NOT_YOURSELF(2,
		 * "不能添加你自己..."), ALREADY_FRIENDS(3, "该用户已经是你的好友...");
		 */
		User user = queryUserInfoByAccount(friendAccount);

		// 搜索好友不存在
		if (user == null) {
			return SearchFriendsStatusEnum.USER_NOT_EXIST.status;
		}

		// 搜索的好友是本人
		if (user.getUserId().equals(myUserId)) {
			return SearchFriendsStatusEnum.NOT_YOURSELF.status;
		}

		// 搜索的好友已经是你的好友
		Example mfe = new Example(MyFriends.class);
		Criteria mfc = mfe.createCriteria();
		mfc.andEqualTo("myUserId", myUserId);
		mfc.andEqualTo("myFriendUserId", user.getUserId());
		MyFriends myFriendsRel = myFriendsMapper.selectOneByExample(mfe);
		if (myFriendsRel != null) {
			return SearchFriendsStatusEnum.ALREADY_FRIENDS.status;
		}

		return SearchFriendsStatusEnum.SUCCESS.status;
	}

	@Override
	public User queryUserInfoByAccount(String account) {

		Example userExample = new Example(User.class);
		Criteria criteria = userExample.createCriteria();
		criteria.andEqualTo("account", account);
		User result = userMapper.selectOneByExample(userExample);

		return result;
	}

	@Transactional
	@Override
	public void sendFriendRequest(String myUserId, String friendUsername) {

		// 根据趣语号查询好友个人信息
		User friendInfo = queryUserInfoByAccount(friendUsername);

		// 查询发送好友请求记录表
		Example fre = new Example(FriendsRequest.class);
		Criteria frc = fre.createCriteria();
		frc.andEqualTo("sendUserId", myUserId);
		frc.andEqualTo("acceptUserId", friendInfo.getUserId());
		FriendsRequest friendsRequest = friendsRequestMapper.selectOneByExample(fre);

		if (friendsRequest == null) {
			// 如果不是你的好友，并且好友记录没有添加，则新增好友请求记录
			String requestId = sid.nextShort();

			FriendsRequest request = new FriendsRequest();
			request.setId(requestId);
			request.setSendUserId(myUserId);
			request.setAcceptUserId(friendInfo.getUserId());
			request.setRequestDateTime(new Date());

			friendsRequestMapper.insert(request);
		}
	}

	@Override
	public List<FriendRequestVO> queryFriendRequestList(String acceptUserId) {
		List<FriendRequestVO> friendRequestVOs = usersMapperCustom.queryFriendRequestList(acceptUserId);
		return friendRequestVOs;
	}

	@Transactional
	@Override
	public void deleteFriendRequest(String sendUserId, String acceptUserId) {
		Example fre = new Example(FriendsRequest.class);
		Criteria frc = fre.createCriteria();
		frc.andEqualTo("sendUserId", sendUserId);
		frc.andEqualTo("acceptUserId", acceptUserId);
		friendsRequestMapper.deleteByExample(fre);
	}

	@Transactional
	@Override
	public void passFriendRequest(String sendUserId, String acceptUserId) {

		// 注意要添加两条记录到数据库，用户双方互为好友
		saveFriend(sendUserId, acceptUserId);
		saveFriend(acceptUserId, sendUserId);
		deleteFriendRequest(sendUserId, acceptUserId);

	}

	private void saveFriend(String sendUserId, String acceptUserId) {
		MyFriends myFriend = new MyFriends();
		String id = sid.nextShort();
		// 好友备注默认为用户昵称
		String friendRemark = userMapper.selectByPrimaryKey(acceptUserId).getNickname();
		myFriend.setId(id);
		myFriend.setMyFriendUserId(acceptUserId);
		myFriend.setMyUserId(sendUserId);
		myFriend.setRemark(friendRemark);
		myFriendsMapper.insert(myFriend);
	}

	@Override
	public List<MyFriendsVO> queryFriends(String userId) {
		List<MyFriendsVO> myFriendsVO = usersMapperCustom.queryFriends(userId);
		return myFriendsVO;
	}

	@Transactional
	@Override
	public List<MyFriendsVO> setFriendRemark(MyFriendBO myFriend) {

		MyFriends myFriends = new MyFriends();
		BeanUtils.copyProperties(myFriend, myFriends);
		Example example = new Example(MyFriends.class);
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo("myUserId", myFriend.getMyUserId());
		criteria.andEqualTo("myFriendUserId", myFriend.getMyFriendUserId());
		myFriendsMapper.updateByExample(myFriends, example);

		// 返回更新后的好友列表
		List<MyFriendsVO> myFriendsVO = usersMapperCustom.queryFriends(myFriend.getMyUserId());
		return myFriendsVO;
	}

	@Transactional
	@Override
	public String saveMsg(ChatMessage chatMessage) {
		// 默认保存的聊天信息都是未签收的
		ChatMsg msg = new ChatMsg();
		String id = sid.nextShort();
		msg.setId(id);
		msg.setSendUserId(chatMessage.getSenderId());
		msg.setAcceptUserId(chatMessage.getReceiverId());
		msg.setCreateTime(new Date());
		msg.setMsg(chatMessage.getMsg());
		msg.setSignFlag(MsgSignFlagEnum.UNSIGN.getType());

		chatMsgMapper.insert(msg);
		return id;

	}

	@Transactional
	@Override
	public void updateMsgSigned(List<String> msgIdList) {
		usersMapperCustom.batchUpdateMsgSigned(msgIdList);

	}

	@Override
	public List<ChatMsg> getUnReadMsgList(String acceptUserId) {

		Example example = new Example(ChatMsg.class);
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo("acceptUserId", acceptUserId);
		criteria.andEqualTo("signFlag", 0);
		List<ChatMsg> result = chatMsgMapper.selectByExample(example);

		return result;
	}

}
