package solver.solverImpl;

import java.util.*;
import models.Cell;
import models.CellState;
import models.SolveResults;
import solver.MazeSolver;


public class MazeSolverBFS implements MazeSolver {
    
    private List<Cell> processedCells;
    private List<Cell> pathResult;
    private boolean[][] visitedGrid;
    
    @Override
    public SolveResults findSolution(Cell[][] maze, Cell start, Cell end) {
        initializeSearchVariables(maze.length, maze[0].length);
        
        boolean pathExists = executeBreadthFirstSearch(maze, start, end);
        
        return new SolveResults(processedCells, pathExists ? pathResult : new ArrayList<>());
    }

    private void initializeSearchVariables(int rows, int cols) {
        processedCells = new ArrayList<>();
        pathResult = new ArrayList<>();
        visitedGrid = new boolean[rows][cols];
    }
    

    private boolean executeBreadthFirstSearch(Cell[][] maze, Cell origin, Cell target) {
        ArrayList<Cell> searchQueue = new ArrayList<>();
        Map<Cell, Cell> parentTracker = new HashMap<>();
        
        searchQueue.add(origin);
        visitedGrid[origin.row][origin.col] = true;
        
        while (!searchQueue.isEmpty()) {
            Cell current = searchQueue.remove(0); 
            processedCells.add(current);
            
            if (current.equals(target)) {
                buildPathFromParents(parentTracker, origin, target);
                return true;
            }
            
            exploreNeighboringCells(maze, current, searchQueue, parentTracker);
        }
        
        return false;
    }
    

    private void exploreNeighboringCells(Cell[][] maze, Cell current, ArrayList<Cell> queue, Map<Cell, Cell> parents) {

        int[][] directions = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
        
        for (int[] dir : directions) {
            int newRow = current.row + dir[0];
            int newCol = current.col + dir[1];
            
            if (isValidMove(maze, newRow, newCol)) {
                Cell neighbor = maze[newRow][newCol];
                visitedGrid[newRow][newCol] = true;
                parents.put(neighbor, current);
                queue.add(neighbor);
            }
        }
    }
    

    private boolean isValidMove(Cell[][] maze, int row, int col) {
        return row >= 0 && row < maze.length &&
               col >= 0 && col < maze[0].length &&
               !visitedGrid[row][col] &&
               maze[row][col].state != CellState.BARRIER;
    }
    

    private void buildPathFromParents(Map<Cell, Cell> parents, Cell origin, Cell target) {
        List<Cell> tempPath = new ArrayList<>();
        Cell current = target;
        

        while (current != null) {
            tempPath.add(current);
            current = parents.get(current);
        }
        

        for (int i = tempPath.size() - 1; i >= 0; i--) {
            pathResult.add(tempPath.get(i));
        }
    }
}
