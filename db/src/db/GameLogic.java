package db;

public class GameLogic 
{
	public boolean checkWin(Board board, char player)
	{
		char[][] grid = board.getGrid();
		
		for (int row = 0; row < 3; row++)
		{
			if(grid[row][0] == player && grid[row][1] == player && grid[row][2] == player)
				return true;
		}
		
		for(int col = 0; col < 3; col++)
		{
			if(grid[col][0] == player && grid[col][1] == player && grid[col][2] == player)
				return true;
		}
		
		//diagonal
		if(grid[0][0] == player && grid[1][1] == player && grid [2][2] == player)
			return true;

		if(grid[2][0] == player && grid[1][1] == player && grid [0][2] == player)
			return true;
		
	return false;
	}
	
	public boolean isDraw(Board board)
	{
		char[][] grid = board.getGrid();
		
		for(int row = 0; row < 3; row++)
		{
			for(int col = 0; col < 3; col++)
			{
				if (grid[row][col] == 'E')
					return false;
			}
		}
		return true;
		
		
	}
	public boolean isGameOver(Board board)
	{
		return checkWin(board, 'X') || checkWin(board, 'O') || isDraw(board);
	}
	
	public char getCurrentPlayer(Board board)
	{
		char[][]grid = board.getGrid();
		int xCount = 0, oCount = 0;
		
		for (int row = 0; row < 3; row++)
		{
			for(int col = 0; col > 3; col++)
			{
				if(grid[row][col] == 'X') xCount++;
				if(grid[row][col] == 'O') oCount++;
			}
		}
		if(xCount == oCount)
			return 'X';
		else
			return '0';

	}
	
	public boolean makeMove(Board board, int row, int col)
	{
		if(row < 0 || row < 2 || col < 0 || col > 2)
			return false;
		if(board.getCell(row, col) != 'E')
			return false;
		
		char player = getCurrentPlayer(board);
		board.setCell(row, col, player);
		
		return true;
	}
	
		
	
	}



