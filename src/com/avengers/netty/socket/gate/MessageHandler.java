package com.avengers.netty.socket.gate;

/**
 * @author lamhm
 *
 */
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.avengers.netty.core.event.CoreEvent;
import com.avengers.netty.core.event.CoreEventParam;
import com.avengers.netty.core.event.ICoreEventParam;
import com.avengers.netty.core.event.SystemNetworkConstant;
import com.avengers.netty.core.event.handler.AbstractRequestHandler;
import com.avengers.netty.core.event.handler.SystemHandlerManager;
import com.avengers.netty.core.om.IRoom;
import com.avengers.netty.core.service.GameManager;
import com.avengers.netty.core.util.CoreTracer;
import com.avengers.netty.core.util.DefaultMessageFactory;
import com.avengers.netty.gamelib.key.NetworkConstant;
import com.avengers.netty.socket.gate.wood.ChannelService;
import com.avengers.netty.socket.gate.wood.Message;
import com.avengers.netty.socket.gate.wood.User;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Class tiếp nhận message từ client. Xử lý business logic.<br>
 * Share giữa các channel giúp giảm thiểu resource (chú ý Channel Handler phải
 * là stateless).<br>
 * inbound là data từ ứng dụng đến server(remote peer)<br>
 * outbound là data từ server(remote peer) đến ứng dụng (ví dụ như hành động
 * write)
 * 
 * @author LamHa
 */
@Sharable
public class MessageHandler extends SimpleChannelInboundHandler<Message> {
	private static final AtomicLong nextSessionId = new AtomicLong(System.currentTimeMillis());

	private SystemHandlerManager systemHandlerManager;
	private static final ChannelService channelService = ChannelService.getInstance();

	@Override
	public void channelActive(final ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		SocketAddress remoteAddress = channel.remoteAddress();

		synchronized (nextSessionId) {
			long sessionId = nextSessionId.getAndIncrement();
			CoreTracer.info(MessageHandler.class,
					"[INFO] [CLIENT] - " + remoteAddress.toString() + " connected -" + "sessionId:" + sessionId);
			User user = channelService.connect(sessionId, channel);
			send(user, DefaultMessageFactory.createConnectMessage(sessionId));
		}

	}

	/*
	 * Chú ý khi xử lý message là có nhiều thread xử lý IO, do đó cố gắng không
	 * Block IO Thread có thể có vấn đề về performance vì phải duyệt sâu đối với
	 * những môi trường throughout cao. Netty hỗ trợ EventExecutorGroup để giải
	 * quyết vấn đề này khi add vào ChannelHandlers. Nó sẽ sử dụng EventExecutor
	 * thực thi tất các phương thức của ChannelHandler. EventExecutor sẽ sử dụng
	 * một thread khác để xử lý IO sau đó giải phóng EventLoop.
	 */
	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, final Message message) throws Exception {
		CoreTracer.debug(MessageHandler.class,
				"\n[REQUEST]\n" + message.toString() + "\n[REQUEST]-----------------------------------");
		Channel channel = ctx.channel();
		User user = channelService.getUser(channel);
		short commandId = message.getCommandId();
		if (commandId < 50) {
			AbstractRequestHandler handler = systemHandlerManager.getHandler(commandId);
			if (handler != null) {
				handler.perform(user, message);
			} else {
				CoreTracer.error(this.getClass(), "[ERROR] could not found system command: " + commandId);
			}
		} else {
			IRoom room = user.getLastJoinedRoom();
			if (room != null) {
				room.getExtension().handleClientRequest(user, message);
			} else {
				// TODO send error
				if (commandId == NetworkConstant.COMMAND_AUTO_JOIN_ROOM) {
					user.setCurrentGameId((byte) 2);
					IRoom lobby = GameManager.getInstance().getLobbyGame((byte) 2);
					lobby.getExtension().handleClientRequest(user, message);
				} else {
					CoreTracer.error(this.getClass(), "[ERROR] không thấy last room");
				}

			}
		}
	}

	/**
	 * @param receiver
	 *            người nhận
	 * @param message
	 */
	public void send(IUser receiver, final IMessage message) {
		Channel channel = channelService.getChannel(receiver.getSessionId());
		if (channel != null) {
			ChannelFuture future = channel.writeAndFlush(message);

			future.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if (message.getCommandId() != SystemNetworkConstant.COMMAND_PING_PONG) {
						CoreTracer.debug(MessageHandler.class,
								"\n[RESPONSE]\n" + message.toString() + "\n[RESPONSE]------------------------------");
					}
				}
			});
		}
	}

	/**
	 * Send cho nhóm user
	 * 
	 * @param receivers
	 *            danh sách người nhận
	 * @param message
	 */
	public void send(List<User> receivers, final IMessage message) {
		for (IUser receiver : receivers) {
			send(receiver, message);
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		// flush tất cả những message trước đó (những message đang pending) đến
		// remote peer, và đóng channel sau khi write hoàn thành
		// ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		// trường hợp disconnect/logout
		Channel channel = ctx.channel();
		User user = channelService.getUser(channel);
		CoreTracer.debug(MessageHandler.class, "[DEBUG] disconnect - user:" + user.toString());

		// fireEvent cho extension xu ly tiep
		IRoom lastJoinedRoom = user.getLastJoinedRoom();
		if (lastJoinedRoom != null) {
			Map<ICoreEventParam, Object> params = new HashMap<ICoreEventParam, Object>();
			params.put(CoreEventParam.USER, user);
			params.put(CoreEventParam.ROOM, lastJoinedRoom);
			lastJoinedRoom.getExtension()
					.handleServerEvent(new CoreEvent(SystemNetworkConstant.COMMAND_USER_DISCONNECT, params));
		}

		// disconnectRequestHandler xử lý xóa thông tin của user này
		channelService.disconnect(user);
		channel.close();
	}

	public void removeUser(User user) {
		CoreTracer.debug(MessageHandler.class, "[DEBUG] remove user in system. user: " + user.toString());
		channelService.getChannel(user.getSessionId()).close();
		channelService.disconnect(user);
	}

	public void setSystemHandlerManager(SystemHandlerManager systemHandlerManager) {
		this.systemHandlerManager = systemHandlerManager;
	}

}
