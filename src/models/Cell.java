package models;

public class Cell {
  public CellState state;
  public int col;
  public int row;
  
  public int hashCode() {
    return 31 * this.row + this.col;
  }
  
  public boolean equals(Object comparisonObject) {
    if (this == comparisonObject)
      return true; 
    if (!(comparisonObject instanceof Cell))
      return false; 
    Cell targetCell = (Cell)comparisonObject;
    return (this.row == targetCell.row && this.col == targetCell.col);
  }
  
  @Override
  public String toString() {
    return this.row + "," + this.col + "," + this.state;
  }
  
  public Cell(int verticalIndex, int horizontalIndex) {
    this.row = verticalIndex;
    this.col = horizontalIndex;
    this.state = CellState.VACANT;
  }
}