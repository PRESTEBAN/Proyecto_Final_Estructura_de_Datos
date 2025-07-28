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
    
    private final int gridRows;
    private final int gridCols;
    private final Cell[][] cellGrid;
    private final JButton[][] buttonGrid;
    private MazeController controller;
    
    // Colores renovados - paleta completamente diferente
    private final Color emptyColor = new Color(240, 248, 255);    
    private final Color wallColor = new Color(47, 79, 79);        
    private final Color visitedColor = new Color(255, 182, 193);  
    private final Color borderColor = new Color(176, 196, 222);   
    
    public MazePanel(int rows, int cols) {
        this.gridRows = rows;
        this.gridCols = cols;
        this.cellGrid = new Cell[rows][cols];
        this.buttonGrid = new JButton[rows][cols];
        
        initializePanel();
        buildGrid();
    }
    

    private void initializePanel() {
        setLayout(new GridLayout(gridRows, gridCols));
        setBackground(new Color(230, 230, 250)); 
    }
    

    private void buildGrid() {
        for (int row = 0; row < gridRows; row++) {
            for (int col = 0; col < gridCols; col++) {
                createCellAt(row, col);
            }
        }
    }
    

    private void createCellAt(int row, int col) {
        Cell newCell = new Cell(row, col);
        JButton newButton = createStyledButton();
        
        final int finalRow = row;
        final int finalCol = col;
        
        newButton.addActionListener(e -> {
            if (controller != null) {
                controller.processGridInteraction(finalRow, finalCol);
            }
        });
        
        cellGrid[row][col] = newCell;
        buttonGrid[row][col] = newButton;
        add(newButton);
    }
    

    private JButton createStyledButton() {
        JButton button = new JButton();
        button.setBackground(emptyColor);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createLineBorder(borderColor, 1));
        return button;
    }
    

    public void clearVisitedCellStates() {
        for (int row = 0; row < gridRows; row++) {
            for (int col = 0; col < gridCols; col++) {
                Cell cell = cellGrid[row][col];
                
                if (shouldResetCell(cell.state)) {
                    cell.state = CellState.VACANT;
                    buttonGrid[row][col].setBackground(emptyColor);
                }
            }
        }
    }
    

    private boolean shouldResetCell(CellState state) {
        return state != CellState.BARRIER && 
               state != CellState.ORIGIN && 
               state != CellState.DESTINATION;
    }

    public Cell[][] retrieveCellMatrix() {
        return cellGrid;
    }
    
    public JButton fetchButtonAtPosition(int row, int col) {
        return buttonGrid[row][col];
    }
    
    public void assignNavigationController(MazeController ctrl) {
        this.controller = ctrl;
    }
}