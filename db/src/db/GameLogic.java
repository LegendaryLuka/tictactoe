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
				
		
		}
	}



