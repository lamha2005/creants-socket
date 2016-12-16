package com.avengers.netty.gamelib.key;

/**
 * CLASS Định nghĩa trao đổi message giữa Client - Server
 * 
 * @author LamHa
 *
 */

// TODO nhập chung với class SystemEventType
public class NetworkConstant {

	/*
	 * Client gửi lệnh Tạo Bàn C2S : { Message: MESSAGE_ID = CREATE_ROOM DATA =
	 * SERVICE_ID, BETCOIN, TIMEOUT, RULES_IN_GAME } S2C : { Message: MESSAGE_ID
	 * = CREATE_ROOM DATA = ErrorCode } - Tạo bàn thành công: Server sẽ cho
	 * Client join vào bàn vừa tạo ; Client bắt sự kiện JOIN_ROOM của smartfox
	 * và xử lý tiếp. - Tạo bàn thất bại: Server trả về ErrorCode mã lỗi
	 */
	public static final short COMMAND_CREATE_ROOM = 0x33;

	/*
	 * Client chơi nhanh C2S : { Message: MESSAGE_ID = AUTO_JOIN_ROOM, DATA =
	 * SERVICE_ID } S2C : { Message: MESSAGE_ID = AUTO_JOIN_ROOM, DATA =
	 * ERROR_CODE }
	 * 
	 * - Server sẽ tìm 1 bàn trống cho Client join vào - Nếu hết bàn Server trả
	 * về ERROR_CODE mã lỗi
	 */
	public static final short COMMAND_AUTO_JOIN_ROOM = 0x35;

	/************* MESSAGE IN GAME **************/
	public static final short COMMAND_REQUEST_IN_GAME = 0x36;
	/*
	 * Client gửi request trong game C2S : { Message: MESSAGE_ID =
	 * REQUEST_IN_GAME, DATA = SERVICE_ID } S2C : { Message: MESSAGE_ID =
	 * REQUEST_IN_GAME, DATA = ERROR_CODE, ROOM_LIST, USER_PROFILE}
	 */

	public static final short COMMAND_GET_USERS_IN_LOBBY = 0x37;
	/*
	 * Server trả thông tin của tất cả user đang ở phòng chờ C2S : { Message:
	 * MESSAGE_ID = GET_USERS_IN_LOBBY, DATA = SERVICE_ID, PAGE_ORDER } S2C : {
	 * Message: MESSAGE_ID = GET_USERS_IN_LOBBY, DATA = PAGE_SIZE, PLAYER_LIST,
	 * ERROR_CODE }
	 */

	public static final short COMMAND_INVITE_TO_PLAY = 0x38;
	/*
	 * Server response nội dung popup cho user gửi lời mời và user nhận lời mời.
	 * C2S : { Message: MESSAGE_ID = INVITE_FRIEND, DATA = INVITING_ID,
	 * INVITED_ID } S2C : { Message: MESSAGE_ID = INVITE_FRIEND, DATA =
	 * IS_INVITING, ERROR_CODE }
	 */

	public static final short COMMAND_INVITE_TO_PLAY_CONFIRM = 0x39;
	/*
	 * Client gửi request xác nhận lời mời chơi. Server sẽ cho 2 user đó join
	 * vào phòng chơi C2S : { Message: MESSAGE_ID = INVITE_FRIEND_CONFIRM, DATA
	 * = SERVICE_ID, INVITING_ID, INVITED_ID } S2C : { Message: MESSAGE_ID =
	 * INVITE_FRIEND_CONFIRM, DATA = ERROR_CODE }
	 */

	/**
	 * Gửi tin nhắn đến người chơi trong bàn <br>
	 * C2S : <br>
	 * <code>{ Message: MESSAGE_ID = SEND_PUBLIC_CHAT,<br>
	 * DATA = SENDER_ID, BYTE_MSG_TYPE, STRING_PARAM_1,
	 * STRING_ARRAY_PARAM_1(*opt) }<code> <br>
	 * S2C: <br>
	 * <code>{ Message: MESSAGE_ID = SEND_PUBLIC_CHAT,<br>
	 * DATA = SENDER_ID, STRING_PARAMETER_1 }<code>
	 */
	public static final short COMMAND_CHAT_ROOM = 0x64;

	public static final short COMMAND_GET_ALL_ROOM = 0x65;
	/*
	 * Lấy danh sách room của 1 service C2S : { Message: MESSAGE_ID =
	 * GET_ALL_ROOM, DATA = SERVICE_ID, PAGE_ORDER } S2C : { Message: MESSAGE_ID
	 * = GET_ALL_ROOM, DATA = PAGE_SIZE, ROOM_LIST, ERROR_CODE }
	 * 
	 * Nếu client join room theo id hoặc name thì dùng cơ chế của smartfox
	 * 
	 * Lỗi: Không còn phòng trống
	 */

	public static final short COMMAND_GET_ROOM_LIST = 0x69;
	/*
	 * Client gửi lệnh lấy danh sách bàn chơi phù hợp với level, bet C2S : {
	 * Message: MESSAGE_ID = GET_ROOM_LIST, DATA = SERVICE_ID, MONEY, PAGE_ORDER
	 * } S2C : { Message: MESSAGE_ID = GET_ROOM_LIST, DATA = PAGE_SIZE,
	 * ROOM_LIST, ERROR_CODE }
	 * 
	 * Client không gửi lên MONEY thì tìm theo tiền của user Server trả
	 * ROOM_LIST rỗng khi không có room nào đang chờ
	 */

	public static final short COMMAND_ON_RECEIVE_DATA_AFTER_RECONNECT = 0x6A;
	/*
	 * Server trả về thông tin bàn chơi hiện tại cho Client khi Client reconnect
	 * C2S : { } S2C : { Message: MESSAGE_ID = BOARD_INFO, DATA = ROOM_INFO,
	 * PLAYER_LIST, ERROR_CODE }
	 */

	public static final short COMMAND_JOIN_BOARD = 0x6B;
	/*
	 * Server trả thông tin bàn chơi cho user vừa join (bao gồm thông tin tất cả
	 * player, bàn chơi) C2S : { } S2C : { Message: MESSAGE_ID =
	 * UPDATE_BOARD_GAME, DATA = PLAYER_LIST, GAME_DATA, ERROR_CODE }
	 */

	public static final short COMMAND_TEST = 0xFF;

	public static final short COMMAND_UPDATE_ROOM_LIST = 0x6C;
	/*
	 * Server trả thông tin user vừa join cho tất car user đang ở trong bàn C2S
	 * : { } S2C : { Message: MESSAGE_ID = UPDATE_ROOM_LIST, DATA = ROOM_INFO,
	 * PLAYER_LIST, ERROR_CODE }
	 */

	public static final short COMMAND_CHANGE_LANGUAGE = 0x70;
	/*
	 * Client gửi request xác nhận lời mời chơi. Server sẽ cho 2 user đó join
	 * vào phòng chơi C2S : { Message: MESSAGE_ID = CHANGE_LANGUAGE, DATA =
	 * LANGUAGE_TYPE } S2C : { Message: MESSAGE_ID = CHANGE_LANGUAGE, DATA =
	 * ERROR_CODE }
	 */

	// TODO đụng key với command COMMAND_UPDATE_ROOM_INFO
	public static final short COMMAND_SWITCH_SPECTATOR_TO_PLAYER = 0x71;
	/*
	 * Server trả thông tin user vừa join cho tất cả user đang ở trong bàn C2S :
	 * { } S2C : { Message: MESSAGE_ID = COMMAND_SWITCH_SPECTATOR_TO_PLAYER,
	 * DATA = PLAYER_LIST, GAME_DATA, ERROR_CODE }
	 */

	/**
	 * Cập nhật thông tin bàn chơi. Chỉ được cập nhật khi game chưa bắt đầu, và
	 * thỏa số tiền bet. <br>
	 * C2S : <br>
	 * <code>{ Message: MESSAGE_ID = UPDATE_ROOM_INFO,<br>
	 * DATA = SERVICE_ID, ROOM_ID, BETCOIN, TIMEOUT,RULES_IN_GAME } <code> <br>
	 * S2C: <br>
	 * <code>{ Message: MESSAGE_ID = UPDATE_ROOM_INFO,<br>
	 * DATA = SERVICE_ID, ROOM_ID, BETCOIN, TIMEOUT,RULES_IN_GAME }<code>
	 */
	public static final short COMMAND_UPDATE_ROOM_INFO = 0x71;

	public static final short COMMAND_START_GAME = 0x73;
	/*
	 * Client gửi lệnh START_GAME C2S : { Message: MESSAGE_ID = START_GAME DATA
	 * = BETCOIN, TIMEOUT, RULES_IN_GAME } S2C : { Message: MESSAGE_ID =
	 * START_GAME DATA = } - Start Game thành công: Server sẽ cho Client ..; -
	 * Start Game thất bại: Server trả về ErrorCode mã lỗi
	 */
	public static final short COMMAND_PLAY_MOVE = 0x74;
	public static final short COMMAND_END_GAME = 0x75;
	public static final short COMMAND_DOWN_CHESS = 0x76;

	public static final short COMMAND_READY = 0x77;
	/*
	 * Client gửi lệnh READY để chơi có thể bắt đầu chơi C2S : { Message:
	 * MESSAGE_ID = READY DATA = BETCOIN, TIMEOUT, RULES_IN_GAME } S2C : {
	 * Message: MESSAGE_ID = START_GAME DATA = }
	 */

	public static final short COMMAND_BOARD_INFO = 0x78;

	public static final short ROOM_INFO = 5;

	// KEY
	public static final short KEYI_TIMEOUT = 0x23;
	public static final short KEYI_BETCOIN = 0x24;
	public static final short KEYB_IS_OWNER = 0x25; // 1 : có || 0 : không
	public static final short KEYI_PLAYER_ID = 0x26;
	public static final short KEYR_REMAIN_TIME = 0x27; // thời gian còn lại
	public static final short KEYI_NEXT_PAGE = 0x28;
	public static final short KEYI_MAX_USER = 0x29;
	public static final short KEYBLOB_CARD_LIST = 0x2A;
	public static final short KEYR_REQUEST_NEXT_CMD = 0x2F;

}
