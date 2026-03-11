package db;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class Board 
{
	//holds game play data in an instance variable
    private char[][] grid;
    
    //holds game play data in a CSV file
    private String filename;
        
    
    //non-default constructor - [5 points]
    public Board(String filename)
    {
    	
    }
    
    //loads the grid with the file contents - [5 points]
    public void loadBoardFromFile()
    {

        //Use a scanner to read the board file
        //and populate the grid with the board values
        //remember to close the scanner afterwards 
        //use isValidBoard method as a guide
    	
    }

    
    //valid if it resembles a 3x3 board that contains only E, X, O
    public boolean isValidBoardFile()
    {
    	try
    	{
    		File file = new File("src/tictactoe/" + this.filename);
    		Scanner scanner = new Scanner(file);
    		int xCount = 0, oCount = 0;
    		while(scanner.hasNextLine())
    		{
    			String line = scanner.nextLine().trim();
    			if(!line.matches("[EXO],[EXO],[EXO]"))
    			{
    				scanner.close();
    				return false;
    			}
    			//count X and O
    			if(line.charAt(4) == 'X') xCount++;
    			if(line.charAt(4) == 'O') oCount++;
    			if(line.charAt(2) == 'X') xCount++;
    			if(line.charAt(2) == 'O') oCount++;
    			if(line.charAt(0) == 'X') xCount++;
    			if(line.charAt(0) == 'O') oCount++;
    			if(line.charAt(4) == 'X') xCount++;
    			if(line.charAt(4) == 'O') oCount++;
    			if(line.charAt(2) == 'X') xCount++;
    			if(line.charAt(2) == 'O') oCount++;
    			if(line.charAt(0) == 'X') xCount++;
    			if(line.charAt(0) == 'O') oCount++;
    			if(line.charAt(4) == 'X') xCount++;
    			if(line.charAt(4) == 'O') oCount++;
    			if(line.charAt(2) == 'X') xCount++;
    			if(line.charAt(2) == 'O') oCount++;
    			if(line.charAt(0) == 'X') xCount++;
    			if(line.charAt(0) == 'O') oCount++;

    		}
    		scanner.close();
			return xCount == oCount || xCount == oCount + 1;
    	}
    	catch(Exception Error)
    	{
    		Error.printStackTrace();
    		return false;
    	}
    
    }
    
    
    //saves the grid to the file in the proper format (CSV)
    public void saveBoardToFile()
    {
try {

    		

    		File file = new File("src/tictactoe/" + this.filename);

    		FileWriter writer = new FileWriter(file);

    		

    		String boardContents = ""; 

    		

    		for(int row = 0; row < grid.length; row++)

        	{

        		for(int col = 0; col < grid[0].length; col++)

        		{

        			if(col < 2) boardContents += grid[row][col]+",";

        			else boardContents += this.grid[row][col];

        		}	 

        		if(row < 2) boardContents += "\n";

       

        	}

    		

    		writer.write(boardContents);

    		writer.close();

    		

    	}

    	catch(Exception error) {

    		error.printStackTrace();

    	}

    	
    
    }
    
    
    /***These are the methods used to test those above***/
    //prints the current grid
    public void printGrid()
    {
    	
    }
    
    //create a random board
    public void createRandomBoard()
    {
    	
    }
    
    //clears the grid by placing E in every cell
    public void clearBoard()
    {
    
    }
    
    public static void main(String args[])
    {
    	Board b = new Board("board.csv");
    	System.out.println(b.isValidBoardFile());
    	b.createRandomBoard();
    	b.printGrid();
    	b.saveBoardToFile();
    	b.loadBoardFromFile();
    	System.out.println();
    	b.printGrid();
    }
}
