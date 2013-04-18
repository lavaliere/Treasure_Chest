/* 
 * Proj7 WITH EXTRA CREDIT ******
 * by Tracy Kennedy
 * 
 * This program solves mazes whose data can be imported from the cmd line.
 * 
 * The program solves the maze recursively using a tertiarysearch tree, 2 stacks 
 * (1 to do simple preorder searches and the second to track all visited nodes),
 * and MazeNodes. 
 * 
 * It prints out the path the program took out of the maze by leaving breadcrumbs.
 * 
 */

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Stack;

public class Proj73037 {
	public static Character[][] maze = new Character[16][26];
	public final static boolean debug = true;
	public static int exitRow = 0;
	public static int exitCol = 0;
	public static int startRow = -1;
	public static int startCol = -1;
	public static boolean validStart = false;
	
	public static void main(String args[]) throws FileNotFoundException{
	FileInputStream stream = new FileInputStream(args[0]);
	try {
		importMaze(stream);
	} catch (IOException e) { e.printStackTrace(); }
	System.out.println("Maze Traversal - T. Kennedy");
	
	renderMaze();
	Scanner keyboard = new Scanner(System.in);
	
	while(!validStart){
		System.out.print("Starting point? row: ");
			startRow = keyboard.nextInt();
		System.out.print("                col: ");
			startCol = keyboard.nextInt();
		try{
		if(startRow < 0 || startCol < 0){	System.out.println("Illegal starting position. Try again."); 	
		} else{ startRow += 2; startCol += 2; 
			if ((startRow == exitRow) && (startCol == exitCol)){ System.out.println("Try again - you cannot start at the exit");	
			} else if( maze[startRow][startCol] != ' ') { System.out.println("Illegal starting position.");
			} else { validStart = true;}
			}//else
		}catch(Exception e) { System.out.println("Illegal starting position. Try again."); }
		}//while		
		if(findSolution()){
			System.out.println("I am free!");
		}else{	System.out.println("Help, I am trapped!"); }


	}//main method
	
	private static void importMaze(FileInputStream stream) throws IOException{
		Scanner scan = new Scanner(stream);
		char[][] temp = new char[10][20];
		//set the temp array
		for(int i = 0; i < 10; i ++){
			String line = scan.next();
			for(int j = 0; j < 20; j++){
				char c = line.charAt(j);
				if(c == "E".charAt(0)){
					temp[i][j] = c;
				} else if(c == "1".charAt(0)){
					temp[i][j] = '#';
				}else if(c == "0".charAt(0)){
					temp[i][j] = ' ';					
				}
			}
		}

		buildWalls();
		int tempI = 0;
		int tempJ = 0;
		
		//rows
		for(int i = 3; i < 13; i++ ){
			//columns
			for(int j = 3; j < 23; j ++){
				maze[i][j] = temp[tempI][tempJ];
				tempJ++;
			}
			tempJ = 0;
			tempI++;
		}		
	}
	
	private static void renderMaze(){
		for(int i = 0; i < 16; i++){
			for(int j = 0; j < 26; j++){
				if(maze[i][j] == "E".charAt(0)){ exitCol = j; exitRow = i; }
				if(i == startRow && j == startCol){ maze[i][j] = 'S'; }
				if(maze[i][j] == 'X') maze[i][j] = ' ';
				System.out.print(maze[i][j]);				
			}
			System.out.print("\n");
		}
	}//render maze

	private static void buildWalls(){
		int rowCount = 1;
		int colCount = 1;
		
		for(int i = 0; i < 16; i++){
		switch(i){
		case 0:
			//first  row of the new array/0
			//just 1 and 2
			for(int j = 0; j < 26; j++){
				maze[i][j] = ' ';
			}
			maze[i][12] = '1';
			maze[i][22] = '2';
			break;
			
		case 1:
			//second row/1
			//row of numbers
			for(int j = 0; j < 26; j++){
				maze[i][j] = ' ';
				if(j > 2 && j < 23){
					maze[i][j] = Integer.toString(colCount).charAt(0);
					if(colCount!= 9){	colCount++;	} else{	colCount = 0; }
				}
			}
			break;

		case 2:
			//third row/2
			//row of ceiling
			for(int j = 0; j < 26; j++){
				maze[i][j] = ' ';
				if(j > 2 && j < 23){
					maze[i][j] = '-';
				} else if( j == 2 || j == 23){
					maze[i][j] = '+';
				}
			}
			break;

			
			//fourth row/3 - 13th/12
			//building walls w/ rowNums
		case 3:	
			for(int j = 3; j < 13; j++){
				if(j == 12){
					maze[j][0] = '1';
					maze[j][1] = '0';
					maze[j][24] = '1';
					maze[j][25] = '0';
				}else{
					maze[j][0] = ' ';
					maze[j][1] = Integer.toString(rowCount).charAt(0);
					maze[j][24] = Integer.toString(rowCount).charAt(0);
					maze[j][25] = ' ';
				}
				
				maze[j][2] = '|';
				maze[j][23] = '|';
				rowCount++;
			}
			break;

			
			//fourteenth/13
			//floor of maze
		case 13:
			for(int j = 0; j < 26; j++){
				maze[i][j] = ' ';
				if(j > 2 && j < 23){
					maze[i][j] = '-';
				} else if( j == 2 || j == 23){
					maze[i][j] = '+';
				}
			}
			break;

		
		case 14:
			//fifteenth/14
			//just 1 and 2
			for(int j = 0; j < 26; j++){
				maze[i][j] = ' ';
			}
			maze[i][12] = '1';
			maze[i][22] = '2';
			break;

			
		case 15:
			//sixteenth/15
			for(int j = 0; j < 26; j++){
				maze[15][j] = ' ';
				if(j > 2 && j < 23){
					maze[15][j] = Integer.toString(colCount).charAt(0);
					if(colCount!= 9){  colCount++;	} else{	colCount = 0; }
				}	
			}//for
			break;
			}//switch
		}//for		
	}//buildwalls
	private static boolean findSolution(){
		boolean exitFound = false;
		int currentRow = startRow; 
		int currentCol = startCol;
		TertiarySearchTree mazePath = new TertiarySearchTree(maze[currentRow][currentCol], currentRow, currentCol);		
		exitFound = mazePath.search(maze);
		renderMaze();
		return exitFound;
	}

}//proj7 class

//extra credit attempt
class TertiarySearchTree{
	
	MazeNode<Character> root;
	MazeNode<Character> current;
	PreOrder<Character> pre = new PreOrder<Character>();
	
	public TertiarySearchTree(char maze, int currentRow, int currentCol){ 
		root = new MazeNode<Character>(maze, currentRow, currentCol);
		current = root;
	}
	
	//build neighbors
	public void addLeft(char left, int x, int y){
		current.left = new MazeNode<Character>(left, x, y);
		pre.push(current.left);
	}
	public void addRight(char right, int x, int y){
		current.right = new MazeNode<Character>(right, x, y);
		pre.push(current.right);
	}
	public void addUp(char up, int x, int y){
		current.up = new MazeNode<Character>(up, x, y);
		pre.push(current.up);
	}
	public void addDown(char down, int x, int y){
		current.down = new MazeNode<Character>(down, x, y);
		pre.push(current.down);
	}
	
	public boolean search(Character[][] maze){
		boolean found = false;
		boolean movesAvailable = true;

		while(movesAvailable){
			Scanner scan = new Scanner(current.getCoords());
			scan.useDelimiter("[,]");
			int currentRow = Integer.parseInt(scan.next());
			int currentCol = Integer.parseInt(scan.next());
			//System.out.println("Current location: " + current.getCoords());
			if(current!=root)pre.pushVisited(current); 
			scan.close();
			
			//add neighboring nodes, if any
			if(maze[currentRow-1][currentCol]== 'E' || maze[currentRow-1][currentCol]== ' '){
				addUp(maze[currentRow-1][currentCol], currentRow-1, currentCol);
				}
			if(maze[currentRow+1][currentCol]== 'E' || maze[currentRow+1][currentCol]== ' '){
				addDown(maze[currentRow+1][currentCol], currentRow+1, currentCol);
				}
			if(maze[currentRow][currentCol+1]== 'E' || maze[currentRow][currentCol+1]== ' '){
				addRight(maze[currentRow][currentCol+1], currentRow, currentCol+1);
				}
			if(maze[currentRow][currentCol-1]== 'E' || maze[currentRow][currentCol-1]== ' '){
				addLeft(maze[currentRow][currentCol-1], currentRow, currentCol-1);
				}
			
			//check to see if current node and child nodes are solution
			if(current.data == Character.valueOf('E')){ found = true; break; 
				} else if(current.data != Character.valueOf('S')){
					current.data = '+';
					maze[currentRow][currentCol] = '+';
				}
					
			if(pre.hasNext()){
				if(current.left == null && current.right == null && current.up == null && current.down == null){
					maze[currentRow][currentCol] = ' ';
					//System.out.println("     Dead end. Backtracking...");
					try{
						current = pre.removeNode(root, current, maze);
					}catch(NullPointerException e) { return false; }
				} else{
					current = pre.nextNode();		
					//System.out.println("Moving to next node...");
				}

				
			}else{
				movesAvailable = false;
			}
		}
		
		return found;
	}
	
}//tertiary search tree

class PreOrder<T extends Comparable<T>> extends MazeIterator<T> implements Iterator<T>{
	protected Stack<StkNode<T>> stk;
	protected Stack<StkNode<T>> visited;
	int x;
	int y;
	
	public PreOrder(){
		stk = new Stack<StkNode<T>>();
		visited = new Stack<StkNode<T>>();
		if(stk != null)stk.push(new StkNode<T>(currentNode)); 
		}
	
	public boolean hasNext() {	return !stk.empty();  }
	
	@SuppressWarnings("unchecked")
	public T next(){ return (T) currentNode.data; }
	
	public boolean push(MazeNode<T> n){
		stk.push(new StkNode<T>(n));
		return true;
	}
	public boolean pushVisited(MazeNode<T> n){
		visited.push(new StkNode<T>(n));
		//System.out.println("Pushing into visited: " + n.getCoords() );
		return true;
	}
	
	public MazeNode<T> nextNode() {
		if(stk.empty()) throw new NoSuchElementException();
		StkNode<T> cnode;
		for(;;){
			cnode = stk.pop();
			currentNode = cnode.node; 
			return cnode.node; 
		}
	}//next()
	
	public MazeNode<T> getParent(MazeNode<T> node, Character[][] array){
		MazeNode<T> parent = node;
		Scanner scan = new Scanner(node.getCoords());
		scan.useDelimiter("[,]");
		int x = Integer.parseInt(scan.next());
		int y = Integer.parseInt(scan.next());			
		MazeNode<T> up = null;
		MazeNode<T> down = null;
		MazeNode<T> right = null;
		MazeNode<T> left = null;


		if(array[x-1][y]==Character.valueOf('+')){
			up = new MazeNode<T>(array[x-1][y], (x-1), y);
			parent = up;
		} else	if(array[x+1][y]== Character.valueOf('+')){
			down = new MazeNode<T>(array[x+1][y], (x+1), y);
			parent = down; 
		} else if(array[x][y+1]== Character.valueOf('+')){
			right = new MazeNode<T>(array[x][y+1], x, (y+1));
			parent = right;
		} else if(array[x][y-1]==Character.valueOf('+')){
			left = new MazeNode<T>(array[x][y-1], x, (y-1));
			parent = left; 
		} 

		return parent;

	}
	
	public MazeNode<T> removeNode(MazeNode<T> root, MazeNode<T> node, Character[][] array) {
		MazeNode<T> removed = node;
		StkNode<T> cnode = stk.pop();
		MazeNode<T> replacement = cnode.node;
		MazeNode<T> parent = getParent(replacement, array);

		
	    while(!removed.getCoords().equalsIgnoreCase(parent.getCoords())){

			Scanner scanner = new Scanner(removed.getCoords());
			scanner.useDelimiter("[,]");
			x = Integer.parseInt(scanner.next());
			y = Integer.parseInt(scanner.next());
			array[x][y] = Character.valueOf('X');
			try{
				StkNode<T> vnode = visited.pop();
				removed = vnode.node;
			}catch(EmptyStackException e) { return root;  }
			if(replacement.getCoords().equalsIgnoreCase(removed.getCoords())) break;
	    }
		return removed;
		//return replacement;
	}//removeNode
	
	public void remove() {		}
	}//preorder


abstract class MazeIterator<T extends Comparable<T>> implements Iterator<T>{
	protected MazeNode<T> currentNode;		
	public abstract boolean hasNext();
	public abstract T next();
	public abstract void remove();
	@SuppressWarnings("hiding")
	protected class StkNode<T extends Comparable<T>>{ 	
		MazeNode<T> node; 
		public StkNode(MazeNode<T> n) { node = n; }
	}
}//maze iterator


class MazeNode<T>{
	Comparable<T> data;
	MazeNode<T> left;
	MazeNode<T> right;
	MazeNode<T> up;
	MazeNode<T> down;
	String coords;
	
	@SuppressWarnings("unchecked")
	public MazeNode(Character array, int x, int y){
		data = (Comparable<T>) array;
		left = null; right = null; up = null; down = null;
		coords = x+ "," + y;
	}
	
	public MazeNode(Comparable<T> theData, MazeNode<T> lt, MazeNode<T> rt, 
			MazeNode<T> u, MazeNode<T> d, int x, int y){
		data = theData;
		left = lt; right = rt; up = u; down = d;
		coords = x+ "," +y;
	}
	
	public void setCoords(int x, int y){
		coords = x + "," + y;
		}
	
	public String getCoords(){
		return coords;
	}
}//mazeNode	





