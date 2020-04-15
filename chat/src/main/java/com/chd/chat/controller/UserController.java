package com.chd.chat.controller;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.chd.chat.enums.OperatorFriendRequestTypeEnum;
import com.chd.chat.enums.SearchFriendsStatusEnum;
import com.chd.chat.pojo.ChatMsg;
import com.chd.chat.pojo.User;
import com.chd.chat.pojo.bo.MyFriendBO;
import com.chd.chat.pojo.bo.UserBO;
import com.chd.chat.pojo.vo.FriendRequestVO;
import com.chd.chat.pojo.vo.MyFriendsVO;
import com.chd.chat.pojo.vo.UserVO;
import com.chd.chat.service.UserService;
import com.chd.chat.utils.FastDFSClient;
import com.chd.chat.utils.FileUtils;
import com.chd.chat.utils.JSONResult;
import com.chd.chat.utils.MD5Utils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(value = "UserController", tags = { "用户相关操作接口" })
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private FastDFSClient fastDFSClient;

	@Value("${imgStorageUrl.faceImgUrl}")
	private String faceImgUrl;

	/*
	 * 用户注册
	 */
	@ApiOperation(value = "register", notes = "用户注册")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "成功"), 
			@ApiResponse(code = 500, message = "失败"),
			@ApiResponse(code = 502, message = "token失效或错误"), 
			@ApiResponse(code = 503, message = "请求参数不全"),
			@ApiResponse(code = 555, message = "异常抛出错误") })
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "phonenumber", value = "手机号码", required = true, dataType = "String",paramType="query"),
			@ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "String",paramType="query")
	})
	@PostMapping("/register")
	public JSONResult register(String phonenumber, String password) throws Exception {
		
		// 判断参数是否为空
		if (StringUtils.isBlank(phonenumber) || StringUtils.isBlank(password)) {
			return JSONResult.errorParameter("参数不能为空...");
		}
		
		// 判断手机号是否已经注册过了
		if (userService.queryPhonenumberIsExist(phonenumber)) {
			// 手机号已注册
			return JSONResult.errorMsg("手机号已经注册过了，请去登录...");
		} else {
			userService.register(phonenumber, MD5Utils.getMD5Str(password));
		}
		
		return JSONResult.ok();

	}

	/*
	 * 用户登录
	 */
	@ApiOperation(value = "login", notes = "用户登录")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "成功"), 
			@ApiResponse(code = 500, message = "失败"),
			@ApiResponse(code = 502, message = "token失效或错误"),
			@ApiResponse(code = 503, message = "请求参数不全"),
			@ApiResponse(code = 555, message = "异常抛出错误") })
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "phonenumber", value = "手机号码", required = true, dataType = "String",paramType="query"),
			@ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "String",paramType="query")
	})
	@PostMapping("/login")
	public JSONResult login(String phonenumber, String password) throws Exception {
		
		// 判断参数是否为空
		if (StringUtils.isBlank(phonenumber) || StringUtils.isBlank(password)) {
			return JSONResult.errorParameter("参数不能为空...");
		}
					
		// 判断当前登录手机号是否已注册
		if (!userService.queryPhonenumberIsExist(phonenumber)) {
			// 手机号未注册
			return JSONResult.errorMsg("手机号未注册，请先去注册...");
		} else {
			User user = userService.queryUserForLogin(phonenumber, MD5Utils.getMD5Str(password));
			
			if (user == null) {
				return JSONResult.errorMsg("账号密码不正确，请重新输入...");
			} else {
				UserVO userInfo = new UserVO();
				BeanUtils.copyProperties(user, userInfo);
				return JSONResult.ok(userInfo);
			}
			
		}	
}

	
	/*
	 * 用户注销
	 */
	@ApiOperation(value = "closeAccount", notes = "用户注销")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "成功"), 
			@ApiResponse(code = 500, message = "失败"),
			@ApiResponse(code = 502, message = "token失效或错误"),
			@ApiResponse(code = 503, message = "请求参数不全"),
			@ApiResponse(code = 555, message = "异常抛出错误") })

	@ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String",paramType="query")

	@PostMapping("/closeAccount")
	public JSONResult closeAccount(String userId) throws Exception {
		
		// 判断参数是否为空
		if (StringUtils.isBlank(userId)) {
			return JSONResult.errorParameter("参数不能为空...");
		}
					
		// 注销用户账户
		userService.closeAccount(userId);
		return JSONResult.ok();
	}
	
	/*
	 * 修改密码
	 */
	@ApiOperation(value = "updatePassword", notes = "修改密码")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "成功"), 
			@ApiResponse(code = 500, message = "失败"),
			@ApiResponse(code = 502, message = "token失效或错误"),
			@ApiResponse(code = 503, message = "请求参数不全"),
			@ApiResponse(code = 555, message = "异常抛出错误") })

	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String",paramType="query"),
			@ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "String",paramType="query")
	})

	@PostMapping("/updatePassword")
	public JSONResult updatePassword(String userId, String password) throws Exception {
		
		// 判断参数是否为空
		if (StringUtils.isBlank(userId) || StringUtils.isBlank(password)) {
			return JSONResult.errorParameter("参数不能为空...");
		}
					
		userService.updatePassword(userId, MD5Utils.getMD5Str(password));
		return JSONResult.ok();
	}

	/*
	 * 修改用户昵称
	 */
	@ApiOperation(value = "updateNickName", notes = "修改昵称")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "成功"), 
			@ApiResponse(code = 500, message = "失败"),
			@ApiResponse(code = 502, message = "token失效或错误"),
			@ApiResponse(code = 503, message = "请求参数不全"),
			@ApiResponse(code = 555, message = "异常抛出错误") })
	@PostMapping("/updateNickName")
	public  JSONResult updateNickName(
			@ApiParam(required = true, name = "userBO", value = "用户修改个人信息业务对象") @RequestBody UserBO userBO) {
		User user = new User();
		user.setUserId(userBO.getUserId());
		user.setNickname(userBO.getNickname());

		String result = userService.updateUserInfo(user);
		if ("修改成功".equals(result)) {
			return JSONResult.ok(userService.queryUserById(userBO.getUserId()));
		}else {
			return JSONResult.errorMsg(result);
		}
	}
	
	/*
	 * 修改趣语号
	 */
	@ApiOperation(value = "updateAccount", notes = "修改趣语号")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "成功"), 
			@ApiResponse(code = 500, message = "失败"),
			@ApiResponse(code = 502, message = "token失效或错误"),
			@ApiResponse(code = 503, message = "请求参数不全"),
			@ApiResponse(code = 555, message = "异常抛出错误") })
	@PostMapping("/updateAccount")
	public  JSONResult updateAccount(
			@ApiParam(required = true, name = "userBO", value = "用户修改个人信息业务对象") @RequestBody UserBO userBO) {
		
	    // 判断用户修改的趣语号是否存在，保证用户趣语号的唯一性
		if (userService.queryUserInfoByAccount(userBO.getAccount()) != null) {
			return JSONResult.errorMsg("该趣语号已存在，请填写别的趣语号...");
		}
		User user = new User();
		user.setUserId(userBO.getUserId());
		user.setAccount(userBO.getAccount());
		
		

		String result = userService.updateUserInfo(user);
		if ("修改成功".equals(result)) {
			return JSONResult.ok(userService.queryUserById(userBO.getUserId()));
		}else {
			return JSONResult.errorMsg(result);
		}
		
	}
	
	/**
	 * 上传用户头像
	 */
	@ApiOperation(value = "uploadFaceBase64", notes = "上传用户头像")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "成功"), 
			@ApiResponse(code = 500, message = "失败"),
			@ApiResponse(code = 502, message = "token失效或错误"),
			@ApiResponse(code = 503, message = "请求参数不全"),
			@ApiResponse(code = 555, message = "异常抛出错误") })
	@PostMapping("uploadFaceBase64")
	public JSONResult uploadFaceBase64(
			@ApiParam(required = true, name = "usersBO", value = "用户上传头像信息")@RequestBody UserBO userBO) {
		
		// 获取前端传过来的base64字符串，然后转换为文件对象再上传
		String base64Data = userBO.getFaceData();
		// 图片文件暂存地址
		String userFacePath = faceImgUrl + userBO.getUserId() + "userface64.png";
		String faceUrl = null;
		
		try {
			// base64转换为文件对象
			FileUtils.base64ToFile(userFacePath, base64Data);
		} catch (Exception e) {
			return JSONResult.errorException(e.getMessage());
		}
		
		try {
			// 上传文件到fastfds
			MultipartFile faceFile = FileUtils.fileToMultipart(userFacePath);
			faceUrl = fastDFSClient.uploadBase64(faceFile);
		} catch (IOException e) {
			return JSONResult.errorException(e.getMessage());
		}
		
		// 获取缩略图的url
		// 我们自己所配置的缩略图大小
		String thump = "_80x80.";
		String arr[] = faceUrl.split("\\.");
		String thumpImgUrl = arr[0] + thump + arr[1];
		
		// 更新用户信息
		User user = new User();
		user.setUserId(userBO.getUserId());
		user.setFaceImage(thumpImgUrl);
		user.setFaceImageBig(faceUrl);
		

		String result = userService.updateUserInfo(user);
		if ("修改成功".equals(result)) {
			return JSONResult.ok(userService.queryUserById(userBO.getUserId()));
		}else {
			return JSONResult.errorMsg("修改用户头像失败");
		}

	}
	
	/*
	 * 搜索好友
	 */
	@ApiOperation(value = "searchUser", notes = "搜索好友")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "成功"), 
			@ApiResponse(code = 500, message = "失败"),
			@ApiResponse(code = 502, message = "token失效或错误"),
			@ApiResponse(code = 503, message = "请求参数不全"),
			@ApiResponse(code = 555, message = "异常抛出错误") })
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "myUserId", value = "用户id", required = true, dataType = "String",paramType="query"),
			@ApiImplicitParam(name = "friendAccount", value = "搜索的好友趣语号", required = true, dataType = "String",paramType="query")
	})
	@PostMapping("/searchUser")
	public JSONResult searchUser(String myUserId,  String friendAccount) {
		
		// 判断所需参数是否为空
		if (StringUtils.isBlank(myUserId) || StringUtils.isBlank(friendAccount)) {
			return JSONResult.errorParameter("请求参数不能为空");
		}
		
		// 根据搜索结果在枚举中获取对应的信息返回结果
		// 1. 搜索的用户如果不存在，返回[无此用户]
		// 2. 搜索账号是你自己，返回[不能添加自己]
		// 3. 搜索的朋友已经是你的好友，返回[该用户已经是你的好友]
		
		Integer status = userService.searchFriend(myUserId, friendAccount);
		
		// 搜索成功,返回对应好友的用户信息
		// 反之，返回搜索的对应错误信息
		if (status ==SearchFriendsStatusEnum.SUCCESS.status) {
			User user = userService.queryUserInfoByAccount(friendAccount);
			
			UserVO userVO = new UserVO();
			BeanUtils.copyProperties(user, userVO);
			return JSONResult.ok(userVO);
		}else {
			String errorMessage = SearchFriendsStatusEnum.getMsgByKey(status);
			return JSONResult.errorMsg(errorMessage);
		}
			
	}
	
	/*
	 * 发送添加好友请求
	 */
	@ApiOperation(value = "addFriendRequest", notes = "发送添加好友请求")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "成功"), 
			@ApiResponse(code = 500, message = "失败"),
			@ApiResponse(code = 502, message = "token失效或错误"),
			@ApiResponse(code = 503, message = "请求参数不全"),
			@ApiResponse(code = 555, message = "异常抛出错误") })
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "myUserId", value = "发送好友请求id", required = true, dataType = "String",paramType="query"),
			@ApiImplicitParam(name = "friendAccount", value = "接收添加好友请求的好友趣语号", required = true, dataType = "String",paramType="query")
	})
	@PostMapping("/addFriendRequest")
	public JSONResult addFriendRequest(String myUserId, String friendAccount) {
		
		// 判断所需参数是否为空
		if (StringUtils.isBlank(myUserId) || StringUtils.isBlank(friendAccount)) {
			return JSONResult.errorParameter("请求参数不能为空");
		}
		
		// 根据搜索结果在枚举中获取对应的信息返回结果
		// 1. 搜索的用户如果不存在，返回[无此用户]
		// 2. 搜索账号是你自己，返回[不能添加自己]
		// 3. 搜索的朋友已经是你的好友，返回[该用户已经是你的好友]
		
		Integer status = userService.searchFriend(myUserId, friendAccount);
		
		if (status == SearchFriendsStatusEnum.SUCCESS.status) {
			userService.sendFriendRequest(myUserId, friendAccount);
			}else {
			String errorMsg = SearchFriendsStatusEnum.getMsgByKey(status);
			return JSONResult.errorMsg(errorMsg);
		}
		return JSONResult.ok();
	}
	
	/*
	 * 根据用户id搜索添加好友请求列表
	 */
	@ApiOperation(value = "searchFriendRequest", notes = "查询好友请求列表")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "成功"), 
			@ApiResponse(code = 500, message = "失败"),
			@ApiResponse(code = 502, message = "token失效或错误"),
			@ApiResponse(code = 503, message = "请求参数不全"),
			@ApiResponse(code = 555, message = "异常抛出错误") })
	@ApiImplicitParam(name = "acceptUserId", value = "接收添加好友请求id", required = true, dataType = "String",paramType="query")
	@PostMapping("/searchFriendRequest")
	public JSONResult searchFriendRequest(String acceptUserId) {
		
		// 参数非空判断
		if (StringUtils.isBlank(acceptUserId)) {
			return JSONResult.errorParameter("参数不能为空...");
		}
		List<FriendRequestVO> friendRequestVOs = userService.queryFriendRequestList(acceptUserId);
		return JSONResult.ok(friendRequestVOs);
	}
		
	
	/*
	 * 根据用户id查找好友列表
	 */
	@ApiOperation(value = "queryFriendsList", notes = "获取好友列表")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "成功"), 
			@ApiResponse(code = 500, message = "失败"),
			@ApiResponse(code = 502, message = "token失效或错误"),
			@ApiResponse(code = 503, message = "请求参数不全"),
			@ApiResponse(code = 555, message = "异常抛出错误") })
	@ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String",paramType="query")
	@PostMapping("/queryFriendsList")
	public JSONResult queryFriendsList(String userId) {
		
		// 参数非空判断
		if (StringUtils.isBlank(userId)) {
			return JSONResult.errorParameter("参数不能为空...");
		}
	
		// 返回好友列表
		List<MyFriendsVO> myFriends = userService.queryFriends(userId);
		return JSONResult.ok(myFriends);
	}
	
	
	/*
	 * 设置指定的好友备注
	 */
	@ApiOperation(value = "setFriendRemark", notes = "设置好友备注")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "成功"), 
			@ApiResponse(code = 500, message = "失败"),
			@ApiResponse(code = 502, message = "token失效或错误"),
			@ApiResponse(code = 503, message = "请求参数不全"),
			@ApiResponse(code = 555, message = "异常抛出错误") })
	@PostMapping("/setFriendRemark")
	public JSONResult setFriendRemark(@RequestBody MyFriendBO myFriend) {
		
		List<MyFriendsVO> myFriends = userService.setFriendRemark(myFriend);
		return JSONResult.ok(myFriends);
	}
	
	@ApiOperation(value = "operFriendRequest", notes = "操作好友请求")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "成功"), 
			@ApiResponse(code = 500, message = "失败"),
			@ApiResponse(code = 502, message = "token失效或错误"),
			@ApiResponse(code = 503, message = "请求参数不全"),
			@ApiResponse(code = 555, message = "异常抛出错误") })
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "sendUserId", value = "发送好友请求id", required = true, dataType = "String",paramType="query"),
			@ApiImplicitParam(name = "acceptUserId", value = "接收添加好友请求的好友名称", required = true, dataType = "String",paramType="query"),
			@ApiImplicitParam(name = "operType", value = "操作类别", required = true, dataType = "Integer",paramType="query")
			
	})
	@PostMapping("/operFriendRequest")
	public JSONResult operFriendRequest(String acceptUserId, String sendUserId, Integer operType) {
		
		// 参数是否为空判断
		if (StringUtils.isBlank(acceptUserId) 
				|| StringUtils.isBlank(sendUserId) 
				|| operType == null) {
			return JSONResult.errorParameter("参数不能为空");
		}
		
		if (operType == OperatorFriendRequestTypeEnum.IGNORE.type) {
			// 判断如果忽略好友请求，则直接删除好友请求的数据库表记录
			userService.deleteFriendRequest(sendUserId, acceptUserId);
		} else if (operType == OperatorFriendRequestTypeEnum.PASS.type) {
			// 判断如果是通过好友请求，则互相增加好友记录到数据库对应的表
			//	然后删除好友请求的数据库表记录
			userService.passFriendRequest(sendUserId, acceptUserId);
		}
		
		// 4. 数据库查询好友列表
		List<MyFriendsVO> myFirends = userService.queryFriends(acceptUserId);
		
		return JSONResult.ok(myFirends);
		
		
	}
	
	@ApiOperation(value = "getUnReadMsgList", notes = "获取未签收的聊天消息")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "成功"), 
			@ApiResponse(code = 500, message = "失败"),
			@ApiResponse(code = 502, message = "token失效或错误"),
			@ApiResponse(code = 503, message = "请求参数不全"),
			@ApiResponse(code = 555, message = "异常抛出错误") })
	
	@ApiImplicitParam(name = "acceptUserId", value = "接收信息好友方", required = true, dataType = "String",paramType="query")

	/*
	 * 用户手机端获取未签收的消息列表
	 */
	@PostMapping("getUnReadMsgList")
	public JSONResult getUnReadMsgList(String acceptUserId ) {
		
		// 判断参数是否为空
		if(StringUtils.isBlank(acceptUserId)) {
			return JSONResult.errorParameter("参数不能为空");
		}
		
		// 查询未签收消息列表
		List<ChatMsg> unreadMsgList = userService.getUnReadMsgList(acceptUserId);
		
		return JSONResult.ok(unreadMsgList);
		
	}

}
