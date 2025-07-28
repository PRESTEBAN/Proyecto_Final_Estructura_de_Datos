package models;

import java.util.List;

public class SolveResults {
  public final List<Cell> solutionRoute;
  public final List<Cell> exploredNodes;
  
  public SolveResults(List<Cell> traversedCells, List<Cell> finalPath) {
    this.exploredNodes = traversedCells;
    this.solutionRoute = finalPath;
  }
}