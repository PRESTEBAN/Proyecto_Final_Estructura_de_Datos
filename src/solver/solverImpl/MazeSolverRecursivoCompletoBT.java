package solver.solverImpl;

import java.util.*;
import models.Cell;
import models.CellState;
import models.SolveResults;
import solver.MazeSolver;


public class MazeSolverRecursivoCompletoBT implements MazeSolver {
    
    private Set<Cell> exploredSet;
    private Stack<Cell> pathStack;
    private List<Cell> allExplored;
    
    @Override
    public SolveResults findSolution(Cell[][] grid, Cell start, Cell end) {
        exploredSet = new HashSet<>();
        pathStack = new Stack<>();
        allExplored = new ArrayList<>();
        
        boolean success = backtrackSearch(grid, start.row, start.col, end);
        
        
        List<Cell> finalPath = new ArrayList<>();
        if (success) {
            while (!pathStack.isEmpty()) {
                finalPath.add(0, pathStack.pop()); 
            }
        }
        
        return new SolveResults(allExplored, finalPath);
    }
    
   
    private boolean backtrackSearch(Cell[][] grid, int row, int col, Cell target) {
        if (!canMoveTo(grid, row, col)) {
            return false;
        }
        
        Cell currentCell = grid[row][col];
        
        if (exploredSet.contains(currentCell)) {
            return false;
        }
        
        exploredSet.add(currentCell);
        allExplored.add(currentCell);
        pathStack.push(currentCell);
        
        if (currentCell.equals(target)) {
            return true;
        }
        
        
        int[][] moves = {{1,0}, {0,1}, {-1,0}, {0,-1}};
        
        for (int[] move : moves) {
            int nextRow = row + move[0];
            int nextCol = col + move[1];
            
            if (backtrackSearch(grid, nextRow, nextCol, target)) {
                return true;
            }
        }
        
        
        pathStack.pop();
        return false;
    }
    
    private boolean canMoveTo(Cell[][] grid, int row, int col) {
        return row >= 0 && row < grid.length && 
               col >= 0 && col < grid[0].length &&
               grid[row][col].state != CellState.BARRIER;
    }
}