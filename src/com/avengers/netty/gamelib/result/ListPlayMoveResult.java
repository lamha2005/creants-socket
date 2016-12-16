package com.avengers.netty.gamelib.result;

import java.util.LinkedList;
import java.util.List;

import com.avengers.netty.gamelib.GameController;

/**
 * 
 * @author LamHa
 *
 */
public class ListPlayMoveResult implements IPlayMoveResult {

	private List<IPlayMoveResult> listChildren = new LinkedList<IPlayMoveResult>();

	@Override
	public void handleResult(GameController gameController) {
		for (IPlayMoveResult r : getChildrenResult()) {
			if (r != null) {
				r.handleResult(gameController);
			}
		}
	}

	@Override
	public List<IPlayMoveResult> getChildrenResult() {
		return this.listChildren;
	}

	@Override
	public void addChildResult(IPlayMoveResult result) {
		this.listChildren.add(result);
	}
}