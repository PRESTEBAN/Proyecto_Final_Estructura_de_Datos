package solver;

import models.Cell;
import models.SolveResults;


public interface MazeSolver {
    SolveResults findSolution(Cell[][] grid, Cell startPoint, Cell endPoint);
}
