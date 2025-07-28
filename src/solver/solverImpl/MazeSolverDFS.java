package solver.solverImpl;

import java.util.*;
import models.Cell;
import models.CellState;
import models.SolveResults;
import solver.MazeSolver;


public class MazeSolverDFS implements MazeSolver {
    
    private Set<Cell> exploredCells;
    private List<Cell> solutionPath;
    private Stack<Cell> processingStack;
    
    @Override
    public SolveResults findSolution(Cell[][] maze, Cell start, Cell end) {
        prepareSearchStructures();
        
        boolean solutionFound = executeIterativeDFS(maze, start, end);
        
        return new SolveResults(new ArrayList<>(exploredCells), 
                               solutionFound ? solutionPath : new ArrayList<>());
    }
    

    private void prepareSearchStructures() {
        exploredCells = new LinkedHashSet<>();
        solutionPath = new ArrayList<>();
        processingStack = new Stack<>();
    }
    

    private boolean executeIterativeDFS(Cell[][] maze, Cell origin, Cell destination) {
        Map<Cell, Cell> pathParents = new HashMap<>();
        
        processingStack.push(origin);
        
        while (!processingStack.isEmpty()) {
            Cell current = processingStack.pop();
            
            if (exploredCells.contains(current)) {
                continue;
            }
            
            exploredCells.add(current);
            
            if (current.equals(destination)) {
                reconstructPath(pathParents, origin, destination);
                return true;
            }
            
            addNeighborsToStack(maze, current, pathParents);
        }
        
        return false;
    }
    
   
    private void addNeighborsToStack(Cell[][] maze, Cell current, Map<Cell, Cell> parents) {
        int[][] directions = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}}; 
        
        for (int[] dir : directions) {
            int nextRow = current.row + dir[0];
            int nextCol = current.col + dir[1];
            
            if (canMoveToPosition(maze, nextRow, nextCol)) {
                Cell neighbor = maze[nextRow][nextCol];
                if (!exploredCells.contains(neighbor)) {
                    parents.put(neighbor, current);
                    processingStack.push(neighbor);
                }
            }
        }
    }
    

    private boolean canMoveToPosition(Cell[][] maze, int row, int col) {
        return row >= 0 && row < maze.length &&
               col >= 0 && col < maze[0].length &&
               maze[row][col].state != CellState.BARRIER;
    }
    

    private void reconstructPath(Map<Cell, Cell> parents, Cell origin, Cell destination) {
        Stack<Cell> pathStack = new Stack<>();
        Cell current = destination;
        

        while (current != null) {
            pathStack.push(current);
            current = parents.get(current);
        }
        

        while (!pathStack.isEmpty()) {
            solutionPath.add(pathStack.pop());
        }
    }
}