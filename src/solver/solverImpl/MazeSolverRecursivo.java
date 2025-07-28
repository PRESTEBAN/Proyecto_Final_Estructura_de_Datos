
package solver.solverImpl;

import java.util.*;
import models.Cell;
import models.CellState;
import models.SolveResults;
import solver.MazeSolver;


public class MazeSolverRecursivo implements MazeSolver {
    
    private List<Cell> pathCells;
    private Set<Cell> exploredCells;
    private final int[] rowMoves = {1, 0}; 
    private final int[] colMoves = {0, 1};
    
    @Override
    public SolveResults findSolution(Cell[][] grid, Cell start, Cell end) {
        pathCells = new ArrayList<>();
        exploredCells = new LinkedHashSet<>();
        
        boolean found = searchRecursively(grid, start.row, start.col, end);
        
        return new SolveResults(new ArrayList<>(exploredCells), 
                               found ? new ArrayList<>(pathCells) : new ArrayList<>());
    }
    
    private boolean searchRecursively(Cell[][] grid, int row, int col, Cell target) {
        if (!isValidPosition(grid, row, col)) {
            return false;
        }
        
        Cell currentCell = grid[row][col];
        
        if (exploredCells.contains(currentCell)) {
            return false;
        }
        
        exploredCells.add(currentCell);
        
        if (currentCell.equals(target)) {
            pathCells.add(currentCell);
            return true;
        }
        
        
        for (int i = 0; i < rowMoves.length; i++) {
            int nextRow = row + rowMoves[i];
            int nextCol = col + colMoves[i];
            
            if (searchRecursively(grid, nextRow, nextCol, target)) {
                pathCells.add(currentCell);
                return true;
            }
        }
        
        return false;
    }
    
    private boolean isValidPosition(Cell[][] grid, int row, int col) {
        return row >= 0 && row < grid.length && 
               col >= 0 && col < grid[0].length &&
               grid[row][col].state != CellState.BARRIER;
    }
}