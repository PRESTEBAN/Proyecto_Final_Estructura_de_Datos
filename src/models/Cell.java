package models;


public class Cell {
    public int row;
    public int col;
    public CellState state;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        this.state = CellState.VACANT;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Cell other = (Cell) obj;
        return row == other.row && col == other.col;
    }

    @Override
    public int hashCode() {
        return row * 31 + col;
    }

    @Override
    public String toString() {
        return "Cell(" + row + "," + col + "," + state + ")";
    }
}