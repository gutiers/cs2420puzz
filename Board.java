import java.util.ArrayList;
import java.util.Iterator;


public class Board {
	final private int[] board;
	private int hamming;
	private int manhattan;
	private int rowSize;
	private int colSize;
	private int zedRow;
	private int zedIndex;
	private int inversions;
	private int N;
	private int[] temp;
	//private int[][] toTwoArr;
	public Board(int[][] blocks){
		this.rowSize = blocks.length; //notice blocks.length not blocks[0] for row
		this.colSize = blocks[1].length;
		this.N = rowSize * colSize;
		this.board = new int[N];
		//1D CONVERSION***************************************************************************************
		for(int i = 0; i < rowSize; i++){
			for(int j = 0; j < colSize; j++){
				int val = blocks[i][j];
				//System.out.println("i: " + i + " j: " + j + " Val = " + val);
				int index = (colSize * i) + j;
				this.board[index] = val;
				//HEURISTICS**********************************************************************************
				if(val != (index + 1)){
					this.hamming += 1;
					if(val == 0){
						this.zedRow = i;
						this.zedIndex = index;
					}
					else{
						int oneDDest = (int)val -1;
						int row = oneDDest/colSize;
						int col = oneDDest%colSize;
						this.manhattan += (Math.abs(row - i) + Math.abs(col-j)) ;
					}
				}
			}	
		}
	}
	
	public int boardSize(){
		return board.length;
	}
	
	public int hamming(){
		return hamming - 1; //take away the zed's position
	}
	public int manhattan(){
		return manhattan;
	}
	public boolean isGoal(){
		return manhattan  == 0;
	}
	public boolean isSolvable(){
		//int thisParity = (rowSize - 1) % 2;	//parity is inversions + row of zed all % 2
		 int lo = 0;
		int hi = N-1;	//our high is with respect to the new array we create without zero, thus n-1
		temp = new int[hi];
		
		for(int i = 0; i < temp.length; i++){  //get rid of zed for inversions
			if(board[i] != 0) temp[i] = board[i];
		}
		inversions= mergeSort(temp,lo,hi);
		System.out.println("inversions = "+inversions + "n=" +colSize);
		if(colSize%2 != 0){
			if(inversions%2 == 0) return true;
			if(inversions%2 != 0) return false;
		}
		if(colSize%2 == 0){
			if((zedRow + inversions)%2 == 0) return false;
			if((zedRow + inversions)%2 != 0) return true;
		}
		return false;
	}
	
	public boolean equals(Object board){
		//check for self comparison
		//null case?
		if(board == null) throw new java.lang.NullPointerException();
		if(this == board) return true;
		if(!(board instanceof Board)) return false;
		Board that = (Board) board;
		if(this.zedIndex == that.zedIndex && this.inversions == that.inversions && this.boardSize() == that.boardSize() && 
				this.manhattan == that.manhattan){
			for(int i = 0; i < N; i++){
				if(this.board[i] != that.board[i]) return false;
			} return true;
			//instead of checking every square i'll do diagonals and a ratio of random sqrs POSSIBLE PERFORMANCE IMPROVEMENTS
		}
		return false;
	}
	public Iterable<Board> neighbors(){
		ArrayList<Board> boardList = new ArrayList<Board>();
		//if !on edge
		if(zedIndex > colSize -1 && zedIndex < N-colSize && zedIndex%colSize != (colSize-1) && zedIndex%colSize != 0){
			//up
			boardList.add(nborUp());
			//start off
			boardList.add(nborDown());
			//start off
			boardList.add(nborL());
			//start off
			boardList.add(nborR());
		}
		//if on top
		else if(zedIndex <= colSize-1){
			boardList.add(nborDown());
			if(zedIndex%colSize != (colSize-1)) boardList.add(nborR());
			if(zedIndex%colSize != 0) boardList.add(nborL());
		}
		//if on bottom
		else if(zedIndex >= N-colSize){
			boardList.add(nborUp());
			if(zedIndex%colSize != (colSize-1)) boardList.add(nborR());
			if(zedIndex%colSize != 0) boardList.add(nborL());
		}
		//if on right margin
		else if(zedIndex%colSize == (colSize-1)){
			boardList.add(nborL());
			if(zedIndex > colSize -1) boardList.add(nborUp());
			if(zedIndex < N-colSize) boardList.add(nborDown());
		}//if on left margin
		else if(zedIndex%colSize == 0){
			boardList.add(nborR());
			if(zedIndex > colSize -1) boardList.add(nborUp());
			if(zedIndex < N-colSize) boardList.add(nborDown());
		}
		return boardList;
		
	}
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append(colSize);
		sb.append("\n");
		for(int i = 0; i < N; i++){
			if(i%colSize == colSize-1){
				sb.append(String.format("%3d",board[i]));
				sb.append("\n");
			}
			else
				sb.append(String.format("%3d",board[i]));
		}
		
		return sb.toString();
	}
	
	//************************PRIVATE METHODS*************************************
	
	private int mergeSort(int[] a, int lo, int hi){
		if (lo == hi -1) return 0; //base case
		int mid = lo + (hi - lo)/2;
		return mergeSort(a,lo, mid) + mergeSort(a,mid,hi) + merge(a,lo,mid,hi); 
	}
	
	private int merge(int[] a, int lo, int mid, int hi){
		int count = 0;
		
		for(int i = lo, lp = lo, hp = mid; i <  hi; i++){
			if(hp >= hi || lp < mid && a[lp] <= a[hp]) lp++;
			else{
				if (a[lp] > a[hp]){
					count += (mid-lp);
					hp++;
				}
			}
		}return count;
	}
	
	private int[][] toTwoD(int[] arr){
		int[][] toTwoArr = new int[rowSize][colSize];
		for (int i = 0; i < N; i++){
			int row = i/colSize;
			int col = i%colSize;
			toTwoArr[row][col] = arr[i];
			}
	return toTwoArr; //this way I only allocate memory for 1 2dimensional array that gets repopulated by the neighbors. methinks 
	}
	
	private Board nborUp(){
		int[] neighbor = (int[])board.clone();
		int swapVal = neighbor[zedIndex-colSize];
		neighbor[zedIndex] = swapVal;
		neighbor[zedIndex-colSize] = 0;
		return new Board(toTwoD(neighbor));
	}
	
	private Board nborDown(){
		int[] neighbor = (int[])board.clone();
		int swapVal = neighbor[zedIndex+colSize];
		neighbor[zedIndex] = swapVal;
		neighbor[zedIndex+colSize] = 0;
		return new Board(toTwoD(neighbor));
	}
	
	private Board nborL(){
		int[] neighbor = (int[])board.clone();
		int swapVal = neighbor[zedIndex-1];
		neighbor[zedIndex] = swapVal;
		neighbor[zedIndex-1] = 0;
		return new Board(toTwoD(neighbor));
	}
	
	private Board nborR(){
		int[] neighbor = (int[])board.clone();
		int swapVal = neighbor[zedIndex+1];
		neighbor[zedIndex] = swapVal;
		neighbor[zedIndex+1] = 0;
		return new Board(toTwoD(neighbor));	 
	}
}
