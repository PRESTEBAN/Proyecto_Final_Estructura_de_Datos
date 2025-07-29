
package controllers;

import java.awt.Color;
import javax.swing.JButton;
import models.Cell;
import models.CellState;
import views.MazePanel;


public class MazeController {

    public enum InteractionMode {
        SOURCE_POINT, TARGET_POINT, BARRIER_TOGGLE
    }

    private InteractionMode currentMode = InteractionMode.BARRIER_TOGGLE;
    private Cell sourcePosition;
    private Cell destinationPosition;
    private final MazePanel gridView;
    
   
    private final Color[] stateColors = {
        new Color(255, 253, 208),  
        Color.BLACK,              
        Color.GREEN,               
        Color.RED,                 
        Color.BLUE                 
    };

    public MazeController(MazePanel panel) {
        this.gridView = panel;
        panel.assignNavigationController(this);
    }

   
    public void processGridInteraction(int row, int col) {
        Cell targetCell = gridView.retrieveCellMatrix()[row][col];
        JButton targetButton = gridView.fetchButtonAtPosition(row, col);
        
        if (currentMode == InteractionMode.SOURCE_POINT) {
            processSourceSelection(targetCell, targetButton, row, col);
        } else if (currentMode == InteractionMode.TARGET_POINT) {
            processDestinationSelection(targetCell, targetButton, row, col);
        } else {
            processBarrierToggle(targetCell, targetButton);
        }
    }


    private void processSourceSelection(Cell newCell, JButton newButton, int row, int col) {
        clearPreviousSource();
        sourcePosition = newCell;
        applyStateChange(newCell, newButton, CellState.ORIGIN);
    }

    private void processDestinationSelection(Cell newCell, JButton newButton, int row, int col) {
        clearPreviousDestination();
        destinationPosition = newCell;
        applyStateChange(newCell, newButton, CellState.DESTINATION);
    }


    private void processBarrierToggle(Cell cell, JButton button) {
        CellState newState = (cell.state == CellState.VACANT) ? CellState.BARRIER : CellState.VACANT;
        applyStateChange(cell, button, newState);
    }


    private void applyStateChange(Cell cell, JButton button, CellState newState) {
        cell.state = newState;
        button.setBackground(stateColors[newState.ordinal()]);
    }

    private void clearPreviousSource() {
        if (sourcePosition != null) {
            JButton oldButton = gridView.fetchButtonAtPosition(sourcePosition.row, sourcePosition.col);
            applyStateChange(sourcePosition, oldButton, CellState.VACANT);
        }
    }


    private void clearPreviousDestination() {
        if (destinationPosition != null) {
            JButton oldButton = gridView.fetchButtonAtPosition(destinationPosition.row, destinationPosition.col);
            applyStateChange(destinationPosition, oldButton, CellState.VACANT);
        }
    }


    public void modifyInteractionType(InteractionMode mode) {
        this.currentMode = mode;
    }

    public Cell obtainSourceReference() {
        return sourcePosition;
    }

    public Cell obtainTargetReference() {
        return destinationPosition;
    }

    public void purgeVisitedLocations() {
        gridView.clearVisitedCellStates();
    }
}