package com.avengers.netty;

import java.net.SocketAddress;

import org.apache.log4j.PropertyConfigurator;

import com.avengers.netty.core.api.APIManager;
import com.avengers.netty.core.dao.DataManager;
import com.avengers.netty.core.event.SystemNetworkConstant;
import com.avengers.netty.core.event.handler.SystemHandlerManager;
import com.avengers.netty.core.event.service.SystemMessageExecutor;
import com.avengers.netty.core.service.ExtensionManager;
import com.avengers.netty.core.service.GameManager;
import com.avengers.netty.core.service.IClusterService;
import com.avengers.netty.core.service.RoomManager;
import com.avengers.netty.core.service.UserManager;
import com.avengers.netty.core.util.AppConfig;
import com.avengers.netty.core.util.CoreTracer;
import com.avengers.netty.gamelib.key.NetworkConstant;
import com.avengers.netty.socket.codec.MessageDecoder;
import com.avengers.netty.socket.codec.MessageEncoder;
import com.avengers.netty.socket.gate.MessageHandler;
import com.avengers.netty.socket.gate.wood.ChannelService;
import com.avengers.netty.socket.gate.wood.ConstantMessageContentInterpreter;
import com.avengers.netty.socket.gate.wood.Message;
import com.avengers.netty.socket.gate.wood.MessageWriter;
import com.avengers.netty.websocket.codec.WebsocketDecoder;
import com.avengers.netty.websocket.codec.WebsocketEncoder;
import com.avengers.netty.websocket.gate.HttpRequestHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * @author LamHa
 *
 */
public class SocketServer {
	private static SocketServer instance;
	private MessageHandler messageHandler = new MessageHandler();
	private SystemHandlerManager systemHandlerManager;
	private SystemMessageExecutor messageExecutor;

	private UserManager userManager;
	private RoomManager roomService;
	private APIManager apiManager;
	private ExtensionManager extensionManager;

	public static SocketServer getInstance() {
		if (instance == null) {
			instance = new SocketServer();
		}

		return instance;
	}

	private SocketServer() {
		initConfig();
		userManager = new UserManager();
		roomService = new RoomManager(this);
		apiManager = new APIManager(this);
		extensionManager = new ExtensionManager();
		ChannelService.getInstance();
	}

	private void start() throws InterruptedException {
		System.setProperty("log4j.configurationFile", "configs/log4j2.xml");
		CoreTracer.info(this.getClass(), "================================================ ");
		CoreTracer.info(this.getClass(), "|              PREPARE XYZ SERVER               |");
		CoreTracer.info(this.getClass(), "================================================ ");
		initServerService();
		// connection incoming (chứa danh sách đang kết nối)
		EventLoopGroup bossGroup = new NioEventLoopGroup();

		// xử ly các event sau khi đã kết nối (xử lý IO cho một channel)
		// EventLoop handle những event cho channel
		// EventLoopGroup có thể chứ nhiều hơn 1 EventLoop
		// có thể hiểu EventLoops như những thread xử lý task cho một channel

		// Khi một channel được đăng ký, Netty sẽ bind nó với một single
		// EventLoop(single thread) cho lifetime của Channel. Do đó ứng dụng
		// không cần phải Sync vì tất cả IO của channel sẽ luôn luôn thực thi
		// trên cùng một thread
		EventLoopGroup workerGroup = new NioEventLoopGroup();// chứa danh sách
		// đã kết nối
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			// Channel có thể hiểu như một socket connection
			bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new MessageDecoder(), new MessageEncoder(), messageHandler);
						}

					})
					// Unpooled buffer sẽ có rủi ro cấp phát/giải phóng buffer
					// chậm
					.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
					// max incoming trong queue, Option apply cho serverchannel
					.option(ChannelOption.SO_BACKLOG, 100).option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
					// chấp nhận incoming connection. Child Option sẽ apply trên
					// các channel đã được accept.
					.childOption(ChannelOption.SO_KEEPALIVE, true);

			// ChannelFuture tất cả các hành động IO của Netty là bất đồng bộ
			// do đó không biết được khi nào một hành động kết thúc
			// ChannelFuture sẽ giúp listen sau một hành động kết thúc
			// bind và start chấp nhận incoming connection
			ChannelFuture future = bootstrap.bind(AppConfig.getSocketIp(), AppConfig.getSocketPort()).sync();
			future.addListener(new GenericFutureListener<ChannelFuture>() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if (future.isSuccess()) {
						SocketAddress localAddress = future.channel().localAddress();
						CoreTracer.info(SocketServer.class, "SERVER INFO:" + localAddress.toString());
						CoreTracer.info(SocketServer.class,
								"================= SOCKET SERVER STARTED =================");
					} else {
						CoreTracer.error(SocketServer.class, "Bound attempt failed! ", future.cause().toString());
					}
				}
			});

			ServerBootstrap websocketBoostrap = new ServerBootstrap();
			websocketBoostrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new HttpServerCodec(), new HttpObjectAggregator(64 * 1024),
									new WebsocketDecoder(), new WebsocketEncoder());
							ch.pipeline().addLast(new HttpRequestHandler("/ws"),
									new WebSocketServerProtocolHandler("/ws"), messageHandler);

							// ch.pipeline().addLast("handler", new
							// HexDumpProxyInboundHandler(cf, remoteHost,
							// remotePort));
						}

					}).childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
					.option(ChannelOption.SO_BACKLOG, 100).option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
					.childOption(ChannelOption.SO_KEEPALIVE, true);

			ChannelFuture websocketChannelFuture = websocketBoostrap
					.bind(AppConfig.getWebsocketIp(), AppConfig.getWebsocketPort()).sync();

			websocketChannelFuture.addListener(new GenericFutureListener<ChannelFuture>() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if (future.isSuccess()) {
						SocketAddress localAddress = future.channel().localAddress();
						CoreTracer.info(SocketServer.class, "SERVER INFO:" + localAddress.toString());
						CoreTracer.info(SocketServer.class,
								"================= WEBSOCKET SERVER STARTED =================");
					} else {
						CoreTracer.error(SocketServer.class, "Bound attempt failed! ", future.cause().toString());
					}
				}
			});

			// chờ cho đới khi server socket đóng
			future.channel().closeFuture().sync();
			websocketChannelFuture.channel().closeFuture().sync();

		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}

	}

	public void initConfig() {
		// load server config
		AppConfig.init("configs/config.properties");
	}

	public void initServerService() {
		DataManager.getInstance();
		systemHandlerManager = new SystemHandlerManager();
		messageHandler.setSystemHandlerManager(systemHandlerManager);
		systemHandlerManager.init(userManager, new MessageWriter(messageHandler), roomService);

		GameManager.getInstance();

		// TODO nên đưa ra config
		// trace message
		Message.setIntepreter(
				new ConstantMessageContentInterpreter(SystemNetworkConstant.class, NetworkConstant.class));

		CoreTracer.info(this.getClass(), "==================== Everything Is Completed! =====================");
	}

	public SystemHandlerManager getSystemHandlerManager() {
		return systemHandlerManager;
	}

	public MessageHandler getMessageHandler() {
		return messageHandler;
	}

	public APIManager getAPIManager() {
		return apiManager;
	}

	public SystemMessageExecutor getSystemMessageExecutor() {
		return messageExecutor;
	}

	public IClusterService getClusterService() {
		return null;
	}

	public RoomManager getRoomService() {
		return roomService;
	}

	public ExtensionManager getExtensionManager() {
		return extensionManager;
	}

	public UserManager getUserManager() {
		return userManager;
	}

	public static void main(String[] args) throws Exception {
		PropertyConfigurator.configure("configs/log4j.properties");
		SocketServer.getInstance().start();
	}
}
