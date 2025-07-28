package solver.solverImpl;

import java.util.*;
import models.Cell;
import models.CellState;
import models.SolveResults;
import solver.MazeSolver;


public class MazeSolverBacktracking implements MazeSolver {
    
    private List<Cell> visitedSequence;
    private List<Cell> currentPath;
    private List<Cell> bestPath;
    private boolean[][] cellsUsed;
    
    @Override
    public SolveResults findSolution(Cell[][] maze, Cell start, Cell end) {
        initializeBacktrackingStructures(maze.length, maze[0].length);
        
        boolean pathExists = performBacktrackingSearch(maze, start, end, 0);
        
        return new SolveResults(visitedSequence, pathExists ? new ArrayList<>(bestPath) : new ArrayList<>());
    }
    
    private void initializeBacktrackingStructures(int rows, int cols) {
        visitedSequence = new ArrayList<>();
        currentPath = new ArrayList<>();
        bestPath = new ArrayList<>();
        cellsUsed = new boolean[rows][cols];
    }
    
    private boolean performBacktrackingSearch(Cell[][] maze, Cell current, Cell target, int depth) {

        if (!isPositionValid(maze, current.row, current.col)) {
            return false;
        }
        

        if (cellsUsed[current.row][current.col]) {
            return false;
        }
        
        cellsUsed[current.row][current.col] = true;
        visitedSequence.add(current);
        currentPath.add(current);

        if (current.equals(target)) {
            bestPath.clear();
            bestPath.addAll(currentPath);
            return true;
        }
        
        if (exploreAllDirections(maze, current, target, depth + 1)) {
            return true;
        }
        
        cellsUsed[current.row][current.col] = false;
        currentPath.remove(currentPath.size() - 1);
        
        return false;
    }
    
  
    private boolean exploreAllDirections(Cell[][] maze, Cell current, Cell target, int depth) {
        int[][] moves = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        
        for (int[] move : moves) {
            int newRow = current.row + move[0];
            int newCol = current.col + move[1];
            
            if (isWithinBounds(maze, newRow, newCol)) {
                Cell nextCell = maze[newRow][newCol];
                
                if (performBacktrackingSearch(maze, nextCell, target, depth)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private boolean isPositionValid(Cell[][] maze, int row, int col) {
        return isWithinBounds(maze, row, col) && 
               maze[row][col].state != CellState.BARRIER;
    }
    
    private boolean isWithinBounds(Cell[][] maze, int row, int col) {
        return row >= 0 && row < maze.length && 
               col >= 0 && col < maze[0].length;
    }
}