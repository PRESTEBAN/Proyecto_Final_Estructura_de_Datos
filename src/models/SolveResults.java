package models;

import java.util.List;


public class SolveResults {
    private final List<Cell> visitedCells;
    private final List<Cell> solutionPath;
    private final boolean pathFound;
    
    public SolveResults(List<Cell> visited, List<Cell> path) {
        this.visitedCells = visited;
        this.solutionPath = path;
        this.pathFound = path != null && !path.isEmpty();
    }
    

    public List<Cell> getVisitedCells() {
        return visitedCells;
    }
    
    public List<Cell> getSolutionPath() {
        return solutionPath;
    }
    
    public boolean isPathFound() {
        return pathFound;
    }
    
    public int getPathLength() {
        return pathFound ? solutionPath.size() : 0;
    }
    
    public int getVisitedCount() {
        return visitedCells.size();
    }
}