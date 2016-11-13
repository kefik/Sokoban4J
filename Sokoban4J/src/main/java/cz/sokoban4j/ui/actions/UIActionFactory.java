package cz.sokoban4j.ui.actions;

import cz.sokoban4j.simulation.actions.oop.IAction;
import cz.sokoban4j.simulation.board.oop.Board;
import cz.sokoban4j.ui.UIBoard;
import cz.sokoban4j.ui.atlas.SpriteAtlas;

public class UIActionFactory {

	public static IUIAction createUIAction(Board board, SpriteAtlas sprites, UIBoard uiBoard, IAction agentAction) {
		switch (agentAction.getType(board)) {
		case MOVE: return new UIMove(board, uiBoard, sprites, agentAction.getDirection(), 231, 8);
		case PUSH: return new UIPush(board, uiBoard, sprites, agentAction.getDirection(), 231, 8);
		default: return null;
		}
	}
	
}
