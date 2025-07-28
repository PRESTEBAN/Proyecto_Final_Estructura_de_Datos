package views;

import controllers.MazeController;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import models.Cell;
import models.CellState;

public class MazePanel extends JPanel {

    private final int totalRows;
    private final int totalColumns;
    private final Cell[][] gridCellMatrix;
    private final JButton[][] buttonMatrix;
    private MazeController navigationController;

    public JButton fetchButtonAtPosition(int rowIndex, int columnIndex) {
        return this.buttonMatrix[rowIndex][columnIndex];
    }

    public Cell[][] retrieveCellMatrix() {
        return this.gridCellMatrix;
    }

    public void clearVisitedCellStates() {
        Color creamYellow = new Color(255, 253, 208);

        for (byte rowIterator = 0; rowIterator < this.totalRows; rowIterator++) {
            
            for (byte columnIterator = 0; columnIterator < this.totalColumns; columnIterator++) {
                Cell currentCell = this.gridCellMatrix[rowIterator][columnIterator];

                if (currentCell.state != CellState.BARRIER && currentCell.state != CellState.ORIGIN && currentCell.state != CellState.DESTINATION) {
                    currentCell.state = CellState.VACANT;
                    this.buttonMatrix[rowIterator][columnIterator].setBackground(creamYellow);
                }
            }
        }
    }

    private void establishGridStructure() {
        Color creamYellow = new Color(255, 253, 208); 
        Color borderColor = new Color(200, 190, 140); 

        for (byte rowIterator = 0; rowIterator < this.totalRows; rowIterator++) {
            for (byte columnIterator = 0; columnIterator < this.totalColumns; columnIterator++) {

                Cell gridCell = new Cell(rowIterator, columnIterator);
                JButton interactionButton = new JButton();

                interactionButton.setBackground(creamYellow);
                interactionButton.setOpaque(true);
                interactionButton.setBorder(BorderFactory.createLineBorder(borderColor));


                byte finalRowIndex = rowIterator, finalColumnIndex = columnIterator;
                interactionButton.addActionListener(actionEvent -> {
                    if (this.navigationController != null) {
                        this.navigationController.processGridInteraction(finalRowIndex, finalColumnIndex);
                    }
                });

                add(interactionButton);
                this.gridCellMatrix[rowIterator][columnIterator] = gridCell;
                this.buttonMatrix[rowIterator][columnIterator] = interactionButton;
            }
        }
    }

    public void assignNavigationController(MazeController controllerInstance) {
        this.navigationController = controllerInstance;
    }

    public MazePanel(int dimensionRows, int dimensionColumns) {
        this.totalRows = dimensionRows;
        this.totalColumns = dimensionColumns;

        this.gridCellMatrix = new Cell[dimensionRows][dimensionColumns];
        this.buttonMatrix = new JButton[dimensionRows][dimensionColumns];

        setLayout(new GridLayout(dimensionRows, dimensionColumns));
        establishGridStructure();
    }

}
