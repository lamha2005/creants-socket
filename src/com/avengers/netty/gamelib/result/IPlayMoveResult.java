package com.avengers.netty.gamelib.result;

import java.util.List;

import com.avengers.netty.gamelib.GameController;

/**
 * @author LamHa
 *
 */
public interface IPlayMoveResult {

	public void handleResult(GameController gameController);

	public void addChildResult(IPlayMoveResult result);

	public List<IPlayMoveResult> getChildrenResult();

}