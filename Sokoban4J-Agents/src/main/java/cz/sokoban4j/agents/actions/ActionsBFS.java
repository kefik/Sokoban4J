package cz.sokoban4j.agents.actions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cz.sokoban4j.simulation.actions.EDirection;
import cz.sokoban4j.simulation.actions.compact.CPush;
import cz.sokoban4j.simulation.actions.compact.CWalk;
import cz.sokoban4j.simulation.actions.compact.CWalkPush;
import cz.sokoban4j.simulation.board.compact.BoardCompact;
import cz.sokoban4j.simulation.board.compact.CTile;

/**
 * BFS for finding all possible actions from the current state of the {@link BoardCompact}.
 * 
 * It produces a list of {@link CWalkPush} via {@link #findActions(BoardCompact)} that are possible to perform.
 * 
 * @author Jimmy
 */
public class ActionsBFS {
	
	private static class Node {
		
		public final int x;
		public final int y;
		
		public final int level;
		
		public final int hashCode;
		
		public final EDirection move;
		
		public Node parent;
		
		public Node(int x, int y, int level, EDirection move) {
			this.x = x;
			this.y = y;		
			this.level = level;
			this.move = move;
			hashCode = 290317 * x + 97 * y;
		}
		
		public Node(int x, int y, Node parent, EDirection move) {
			this.x = x;
			this.y = y;		
			this.parent = parent;
			this.level = parent.level + 1;
			this.move = move;
			hashCode = 290317 * x + 97 * y;
		}
		
		@Override
		public int hashCode() {
			return hashCode;
		}
		
		public boolean equals(Object obj) {
			return ((Node)obj).x == x && ((Node)obj).y == y;
		}
		
		@Override
		public String toString() {
			return "Node[" + x + "," + y + "|level = " + level + "]";
		}
		
	}
	
	private CircularArrayQueue<Node> queue;
	
	private Set<Node> touched;
	
	public boolean depthLimitHit = false;
	
	public boolean obeyDepthLimit = true;
	
	/**
	 * Performs BFS to find all possible "walk-to-the-box-and-push" actions ({@link CWalkPush} actions}.
	 * @param board
	 * @return
	 */
	public List<CWalkPush> findActions(BoardCompact board) {
		depthLimitHit = false;
		
		List<CWalkPush> result = new ArrayList<CWalkPush>();
		
		if (queue == null) {
			queue = new CircularArrayQueue<ActionsBFS.Node>(board.width() * board.height());
		} else {
			queue.ensureCapacity(board.width() * board.height());
		}
		
		if (touched == null) {
			touched = new HashSet<Node>();
		} else {
			touched.clear();
		}
				
		Node first = new Node(board.playerX, board.playerY, 0, null);
		
		// Variant A) include the first node into the result => generate "pushes" from the current player location as well
		queue.enqueue(first);
		touched.add(first);
		
		// Variant B) do not include the first node into the result
		//touched.add(first);
		//expand(board, first);
		
		while (!queue.isEmpty()) {
			Node node = queue.dequeue();
			
			if (isTarget(board, node)) {
				addActions(board, node, result);				
			}
									
			expand(board, node);
		}

		return result;
	}
	
	private void expand(BoardCompact board, Node node) {
		for (EDirection dir : EDirection.arrows()) {
			if (CTile.isWalkable(board.tile(node.x+dir.dX, node.y+dir.dY))) {
//				if (CTile.isWall(board.tile(node.x+dir.dX, node.y+dir.dY))) {
//					System.out.println("???");
//					CTile.isWalkable(board.tile(node.x+dir.dX, node.y+dir.dY));
//				}
//				if (CTile.isSomeBox(board.tile(node.x+dir.dX, node.y+dir.dY))) {
//					System.out.println("???");
//					CTile.isWalkable(board.tile(node.x+dir.dX, node.y+dir.dY));
//				}
				Node next = new Node(node.x+dir.dX, node.y+dir.dY, node, dir);
				if (touched.contains(next)) {
					// already probed
					continue;
				}
				
				// ADD NODE TO QUEUE
				touched.add(next);
				queue.enqueue(next);
			}
		}
	}
	
	private void addActions(BoardCompact board, Node node, List<CWalkPush> result) {
		// CREATE WALK ACTION
		
		int targetX = node.x;
		int targetY = node.y;
		
		EDirection[] moves = new EDirection[node.level];
		int index = node.level-1;
		
		while (node != null && node.move != null) {
			moves[index] = node.move;
			node = node.parent;
			--index;
		}
		
		CWalk walkAction = new CWalk(targetX, targetY, moves);
		
		// NOW QUERY POSSIBLE PUSH ACTIONS FROM [targetX, targetY] AND CREATE RESULT ACTIONS
		
		for (EDirection dir : EDirection.arrows()) {
			if (CPush.isPushPossibleIgnorePlayer(board, targetX, targetY, dir)) {
				CWalkPush resultAction = new CWalkPush(walkAction, CPush.getAction(dir));
				result.add(resultAction);
			}
		}
	}

	private static boolean isTarget(BoardCompact board, Node node) {
		// moveable box around
		return    isMovableBoxAt(board, node.x-1, node.y, EDirection.LEFT) 
			   || isMovableBoxAt(board, node.x+1, node.y, EDirection.RIGHT) 
			   || isMovableBoxAt(board, node.x, node.y-1, EDirection.UP) 
			   || isMovableBoxAt(board, node.x, node.y+1, EDirection.DOWN);
	}
	
	private static boolean isMovableBoxAt(BoardCompact board, int x, int y, EDirection moveDir) {
		return    CTile.isSomeBox(board.tile(x, y))
			   && CTile.isWalkable(board.tile(x+moveDir.dX, y+moveDir.dY))
			   ;
	}

}
