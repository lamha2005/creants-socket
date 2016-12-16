package com.avengers.netty.core.event.system;

import com.avengers.netty.core.event.SystemNetworkConstant;
import com.avengers.netty.core.event.handler.AbstractRequestHandler;
import com.avengers.netty.core.util.CoreTracer;
import com.avengers.netty.gamelib.key.ErrorCode;
import com.avengers.netty.gamelib.service.WebService;
import com.avengers.netty.socket.gate.IMessage;
import com.avengers.netty.socket.gate.wood.User;
import com.couchbase.client.java.document.json.JsonObject;

/**
 * @author LamHa
 *
 */
public class LoginRequestHandler extends AbstractRequestHandler {

	@Override
	public void initialize() {
	}

	@Override
	public void perform(User user, IMessage message) {
		try {
			// TODO move to custom
			String token = message.getString(SystemNetworkConstant.KEYS_TOKEN);
			String verifyInfo = WebService.getInstance().verify(token, "12345");
			JsonObject jo = JsonObject.fromJson(verifyInfo);
			Integer code = jo.getInt("code");
			if (code != 1) {
				CoreTracer.debug(this.getClass(), "[DEBUG] session is expired!");
				writeErrorMessage(user, SystemNetworkConstant.COMMAND_USER_LOGIN, ErrorCode.SESSION_EXPIRED,
						"Session đã hết hạn");
				return;
			}

			CoreTracer.debug(this.getClass(), "[DEBUG] " + verifyInfo);
			// String encryptMD5 = Security.encryptMD5(token);
			// String userInfo = cacheServce.get(encryptMD5);
			// if (userInfo == null) {
			// LOG.debug("[DEBUG] session is expired!");
			// writeErrorMessage(user, SystemNetworkConstant.COMMAND_USER_LOGIN,
			// ErrorCode.SESSION_EXPIRED,
			// "Session đã hết hạn");
			// return;
			// }

			JsonObject userObj = jo.getObject("data");
			String uid = userObj.getString("uid");

			// kiểm tra đã đăng nhập trước đó chưa nếu có thì đá ra
			User pUser = coreApi.getUserByName(uid);
			if (pUser != null) {
				writeErrorMessage(pUser, SystemNetworkConstant.COMMAND_USER_LOGIN, ErrorCode.LOGIN_OTHER_DEIVCE,
						"Tài khoản đã được đăng nhập bởi nơi khác");

				coreApi.logout(pUser);
			}

			String fullName = userObj.getString("full_name");
			user.setUserName(fullName != null ? fullName : userObj.getString("username"));

			user.setCreantUserId(userObj.getInt("user_id"));
			user.setMoney(userObj.getLong("money"));
			user.setAvatar(userObj.getString("avatar"));
			user.setUid(uid);
			user.setName(uid);
			coreApi.login(user);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
