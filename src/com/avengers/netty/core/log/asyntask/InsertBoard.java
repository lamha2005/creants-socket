package com.avengers.netty.core.log.asyntask;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;

import com.avengers.netty.core.dao.BaseDatabase;
import com.avengers.netty.core.dao.ConnPool;
import com.avengers.netty.core.log.BoardLog;
import com.avengers.netty.core.queue.RabitQueueService;
import com.avengers.netty.core.queue.constant.QueueLogKey;
import com.avengers.netty.core.util.CoreTracer;

/**
 * @author LamHa
 *
 */
public class InsertBoard extends BaseDatabase implements Runnable {

	private BoardLog board;

	public InsertBoard(BoardLog board) {
		this.board = board;
	}

	@Override
	public void run() {

		Connection conn = null;
		CallableStatement statement = null;

		try {
			conn = ConnPool.getInstance().getConnectionLog();
			statement = conn.prepareCall("{ call sp_board_log_insert(?, ?, ?, ?, ?) }");
			statement.setInt(1, board.getRoomId());
			statement.setInt(2, board.getTotalUserInBoard());
			statement.setLong(3, board.getMoney());
			statement.setInt(4, board.getServiceId());
			statement.registerOutParameter(5, Types.INTEGER);

			statement.executeUpdate();

			board.setId(statement.getInt(5));

			if (board.getId() != -1 && board.getPlayersInBoard().size() > 0) {
				StringBuilder data = new StringBuilder().append(board.getId()).append(QueueLogKey.SEPERATE_CHARS);
				int size = board.getPlayersInBoard().size();
				for (int i = 0; i < size; i++) {
					data.append(board.getPlayersInBoard().get(i));
					if (i < size - 1) {
						data.append(QueueLogKey.SEPERATE_CHARS);
					}
				}
				RabitQueueService.getInstance().sendLog(QueueLogKey.BOARD_EXCHANGE, QueueLogKey.UserBoard, true,
						data.toString());
			} else {
				CoreTracer.warn(InsertBoard.class, "Board info is not valid: " + board.toString());
			}

		} catch (Exception e) {
			CoreTracer.error(InsertBoard.class, "run fail!", e);
		} finally {
			closePreparedStatement(statement);
			free(conn);
		}
	}
}