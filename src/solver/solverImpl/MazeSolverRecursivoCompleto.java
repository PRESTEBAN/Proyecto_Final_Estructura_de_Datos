package solver.solverImpl;

import java.util.*;
import models.Cell;
import models.CellState;
import models.SolveResults;
import solver.MazeSolver;


public class MazeSolverRecursivoCompleto implements MazeSolver {
    
    private Set<Cell> visitedCells;
    private List<Cell> temporaryPath;
    private List<Cell> finalPath;
    private final int[][] allDirections = {{1,0}, {0,1}, {-1,0}, {0,-1}};
    
    @Override
    public SolveResults findSolution(Cell[][] grid, Cell start, Cell end) {
        visitedCells = new LinkedHashSet<>();
        temporaryPath = new ArrayList<>();
        finalPath = new ArrayList<>();
        
        exploreAllPaths(grid, start.row, start.col, end);
        
       
        List<Cell> correctedPath = new ArrayList<>();
        for (int i = finalPath.size() - 1; i >= 0; i--) {
            correctedPath.add(finalPath.get(i));
        }
        
        return new SolveResults(new ArrayList<>(visitedCells), correctedPath);
    }
    
   
    private boolean exploreAllPaths(Cell[][] grid, int row, int col, Cell destination) {
        if (!isInsideBounds(grid, row, col)) {
            return false;
        }
        
        Cell current = grid[row][col];
        
        if (visitedCells.contains(current)) {
            return false;
        }
        
        visitedCells.add(current);
        temporaryPath.add(current);
        
        if (current.equals(destination)) {
            finalPath.addAll(temporaryPath);
            return true;
        }
        
        
        for (int[] direction : allDirections) {
            int newRow = row + direction[0];
            int newCol = col + direction[1];
            
            if (exploreAllPaths(grid, newRow, newCol, destination)) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean isInsideBounds(Cell[][] grid, int row, int col) {
        return row >= 0 && row < grid.length && 
               col >= 0 && col < grid[0].length &&
               grid[row][col].state != CellState.BARRIER;
    }
}