package com.avengers.netty.gamelib.key;
/**
 * @author LamHa
 *
 */
public class ErrorCode {
	public static final short SUCCESS = 0;
	
//	Mã hóa package
	public static final short INVALID_DATA_LENGTH = 10;						// Độ dài dữ liệu không đúng
	public static final short NOT_EXIST_GAME = 11;							// Không tồn tại game này
	
	// Login
	public static final short LOGIN_NEW_USER = 100;							// không tồn tại user, tạo tk mới
	public static final short LOGIN_USER_NAME_LENGTH = 101;					// tên tài khoản phải từ 5 đến 20 ký tự.
	public static final short LOGIN_USER_NAME_CONTAIN = 102;				// tên tài khoản phải là các ký tự a-z A-Z và 0-9.
	public static final short LOGIN_USER_NAME_EXIST = 103;					// username đã tồn tại
	public static final short LOGIN_USER_WRONG_PASS = 104;					// sai mật khẩu
	public static final short LOGIN_USER_EMPTY_USERNAME_OR_PASS = 105;		// tên đăng nhập hoặc mật khẩu rỗng
	public static final short SESSION_EXPIRED = 106;						// session đã hết hạn
	public static final short LOGIN_OTHER_DEIVCE = 107;						// đăng nhập ở thiết bị khác
	public static final short LOGIN_IS_REQUIRED = 108;						// đăng nhập ở thiết bị khác
	
	public static final short JOIN_ROOM_FAILED = 110; 						// JOIN_ROOM thất bại
	public static final short CREATE_ROOM_FAILED = 111; 					// CREATE_ROOM thất bại
	
	// Game
	public static final short GAME_EXT_STOPPED = 200;					// Game service đã dừng
	public static final short GAME_EXT_LOBBY_NOT_FOUND = 201;			// Không tìm thấy lobby của game service

	// User
	public static final short USER_LIST_EMPTY = 300;						// danh sách user rỗng
	public static final short NOT_ENOUGH_MONEY = 301;					// user không đủ tiền
	public static final short USER_NOT_READY = 302;							// user chưa sẳn sàng
	public static final short USER_NOT_JOIN_GAME = 303;						// user chưa join một game cụ thể
	
	// Room
	public static final short ROOM_LIST_EMPTY = 400;						// danh sách room rỗng
	public static final short ROOM_NOT_FOUND = 401;							// Không tìm thấy room này trên hệ thống
	public static final short OUT_OF_PLAYER = 402;							// Phòng đã đủ người chơi
		
	// IN GAME
	public static final short GAME_IS_PLAYING_CANNOT_START = 500;			// Ván chơi đang diễn ra, không thể Start ván mới
	public static final short GAME_START_NEWMATCH_FAILED = 501;				// Start ván mới thất bại
	public static final short GAME_STOPPED = 502;							// Ván đấu đã kết thúc
	public static final short GAME_PLAYING = 503;							// Ván đấu đang diễn ra
	public static final short CHANGE_SERVER = 504;							// Yêu cầu chuyển server
	
	// Player
	public static final short PLAYER_NOT_TO_TURN = 600;						// Player chưa tới lượt (bet, raise, call, check, fold, allin)
	public static final short PLAYER_ACTION_IS_ALLIN_OR_FOLD = 601;			// Hành động của player đang là all hoặc fold
	public static final short PLAYER_ACTION_INVALID = 602;					// Hành động của player không hợp lệ
	public static final short PLAYER_MONEY_INVALID = 603;					// Tiền không hợp lệ

}
