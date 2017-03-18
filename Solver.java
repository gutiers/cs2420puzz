import java.util.ArrayList;
import java.util.Comparator;

import edu.princeton.cs.algs4.MinPQ;


public class Solver {
	private int moves = 0;
	private SearchNode solution;
	public Solver(Board initial){
		SearchNode sn = new SearchNode(initial,moves,null);
		SearchNode husk;
		MinPQ<SearchNode> pq = new MinPQ(sn.byHamming());
		pq.insert(sn);
		while(!pq.isEmpty()){
			sn = pq.delMin();
			//is it goal?
			if(sn.board.isGoal()){
				solution = sn;
				solution();
				break;
			}
			//is it same?
			else if(sn.previous == null || !sn.board.equals(sn.previous.board)){
				//expand
				for(Board el : sn.board.neighbors()){
					husk = new SearchNode(el,moves,sn);
					pq.insert(husk);
				}
				moves++;
			}
		}
		
	}
	public int moves(){
		return moves;
	}
	public Iterable<Board> solution(){
		ArrayList<Board> myList = new ArrayList<>();
		while(solution.previous != null){
			myList.add(solution.board);
			solution = solution.previous;
		}
		//for(Board el:myList) System.out.println(el);
		return myList;
	}
	
	private class SearchNode implements Comparable<SearchNode>{
		public int moves;
		public Board board;
		public SearchNode previous;
		public int manhattan;
		public int hamming;
		public SearchNode(Board board, int level,SearchNode previous){
			this.moves = level;
			this.manhattan = board.manhattan();
			this.hamming = board.hamming();
			this.previous = previous;
			this.board = board;
		}
		public Comparator<SearchNode> byManhattan() {
	        return new Comparator<SearchNode>(){
	            public int compare(SearchNode sn1, SearchNode sn2) {
	            	if(sn1.manhattan + moves == sn2.manhattan+moves) return 0;
	    			else if(sn1.manhattan + moves > sn2.manhattan+moves) return 1;
	    			else return -1; 
	            } 
	        };
	        
	    }
		public Comparator<SearchNode> byHamming(){
			return new Comparator<SearchNode>(){
				public int compare(SearchNode sn1, SearchNode sn2){
					if(sn1.hamming + moves == sn2.hamming + moves) return 0;
					else if(sn1.hamming + moves > sn2.hamming) return 1;
					else return -1;
				}
			};
		}
		@Override
		public int compareTo(SearchNode arg0) {
			// TODO Auto-generated method stub
			return 0;
		}
	}

	public static void main(String[] args) {

	    // create initial board from file
	    //In in = new In(args[0]);
		In in = new In("src/test");
	    int N = in.readInt();
	    int[][] blocks = new int[N][N];
	    for (int i = 0; i < N; i++)
	        for (int j = 0; j < N; j++)
	            blocks[i][j] = in.readInt();
	    Board initial = new Board(blocks);

	    // check if puzzle is solvable; if so, solve it and output solution
	    if (initial.isSolvable()) {
	        Solver solver = new Solver(initial);
	        StdOut.println("Minimum number of moves = " + solver.moves());
	        System.out.println(solver.solution());
	        for (Board board : solver.solution())
	            StdOut.println(board);
	    }

	    // if not, report unsolvable
	    else {
	        StdOut.println("Unsolvable puzzle");
	    }
	}
	
}
