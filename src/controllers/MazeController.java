package controllers;

import java.awt.Color;
import javax.swing.JButton;
import models.Cell;
import models.CellState;
import views.MazePanel;

public class MazeController {

    public enum InteractionType {
        SOURCE_POINT, TARGET_POINT, BARRIER_TOGGLE;
    }

    private InteractionType activeInteraction = InteractionType.BARRIER_TOGGLE;
    private Cell sourceLocation;
    private Cell targetLocation;
    private final MazePanel gridInterface;

    public MazeController(MazePanel viewComponent) {
        this.gridInterface = viewComponent;
        // Cambio: setController -> assignNavigationController
        viewComponent.assignNavigationController(this);
    }

    public void modifyInteractionType(InteractionType newType) {
        this.activeInteraction = newType;
    }

    public void processGridInteraction(int rowIndex, int columnIndex) {
        switch (this.activeInteraction) {
            case SOURCE_POINT:
                establishSourcePosition(rowIndex, columnIndex);
                break;
            case TARGET_POINT:
                establishTargetPosition(rowIndex, columnIndex);
                break;
            case BARRIER_TOGGLE:
                alternateBarrierState(rowIndex, columnIndex);
                break;
        }
    }

    public void onCellClickedLegacy(int rowIndex, int columnIndex) {
        Cell selectedCell = fetchCellFromGrid(rowIndex, columnIndex);
        JButton selectedButton = fetchButtonFromGrid(rowIndex, columnIndex);

        switch (this.activeInteraction) {
            case SOURCE_POINT:
                resetPreviousSourceLocation();
                this.sourceLocation = selectedCell;
                modifyCellProperties(selectedCell, CellState.ORIGIN);
                alterButtonAppearance(selectedButton, Color.GREEN);
                break;
            case TARGET_POINT:
                resetPreviousTargetLocation();
                this.targetLocation = selectedCell;
                modifyCellProperties(selectedCell, CellState.DESTINATION);
                alterButtonAppearance(selectedButton, Color.RED);
                break;
            case BARRIER_TOGGLE:
                if (checkIfCellIsWall(selectedCell)) {
                    applyEmptyStateToCell(selectedCell, selectedButton);
                } else {
                    applyWallStateToCell(selectedCell, selectedButton);
                }
                break;
        }
    }

    public void purgeVisitedLocations() {
        // Cambio: limpiarCeldasVisitadas -> clearVisitedCellStates
        this.gridInterface.clearVisitedCellStates();
    }

    public Cell obtainSourceReference() {
        return this.sourceLocation;
    }

    public Cell obtainTargetReference() {
        return this.targetLocation;
    }

    public void establishSourcePosition(int rowIndex, int columnIndex) {
        Cell selectedCell = fetchCellFromGrid(rowIndex, columnIndex);
        JButton selectedButton = fetchButtonFromGrid(rowIndex, columnIndex);

        resetPreviousSourceLocation();

        this.sourceLocation = selectedCell;
        modifyCellProperties(selectedCell, CellState.ORIGIN);
        alterButtonAppearance(selectedButton, Color.GREEN);
    }

    public void establishTargetPosition(int rowIndex, int columnIndex) {
        Cell selectedCell = fetchCellFromGrid(rowIndex, columnIndex);
        JButton selectedButton = fetchButtonFromGrid(rowIndex, columnIndex);

        resetPreviousTargetLocation();

        this.targetLocation = selectedCell;
        modifyCellProperties(selectedCell, CellState.DESTINATION);
        alterButtonAppearance(selectedButton, Color.RED);
    }

    public void alternateBarrierState(int rowIndex, int columnIndex) {
        Cell selectedCell = fetchCellFromGrid(rowIndex, columnIndex);
        JButton selectedButton = fetchButtonFromGrid(rowIndex, columnIndex);

        if (checkIfCellIsEmpty(selectedCell)) {
            applyWallStateToCell(selectedCell, selectedButton);
        } else if (checkIfCellIsWall(selectedCell)) {
            applyEmptyStateToCell(selectedCell, selectedButton);
        }
    }

    private void resetPreviousSourceLocation() {
        if (this.sourceLocation != null) {
            JButton oldButton = fetchButtonFromGrid(this.sourceLocation.row, this.sourceLocation.col);
            modifyCellProperties(this.sourceLocation, CellState.VACANT);
            alterButtonAppearance(oldButton, Color.WHITE);
        }
    }

    private void resetPreviousTargetLocation() {
        if (this.targetLocation != null) {
            JButton oldButton = fetchButtonFromGrid(this.targetLocation.row, this.targetLocation.col);
            modifyCellProperties(this.targetLocation, CellState.VACANT);
            alterButtonAppearance(oldButton, Color.WHITE);
        }
    }

    private Cell fetchCellFromGrid(int rowIndex, int columnIndex) {
        // Cambio: getCells -> retrieveCellMatrix
        return this.gridInterface.retrieveCellMatrix()[rowIndex][columnIndex];
    }

    private JButton fetchButtonFromGrid(int rowIndex, int columnIndex) {
        // Cambio: getButton -> fetchButtonAtPosition
        return this.gridInterface.fetchButtonAtPosition(rowIndex, columnIndex);
    }

    private void modifyCellProperties(Cell cellReference, CellState newState) {
        cellReference.state = newState;
    }

    private void alterButtonAppearance(JButton buttonReference, Color colorValue) {
        buttonReference.setBackground(colorValue);
    }

    private boolean checkIfCellIsWall(Cell cellReference) {
        return cellReference.state == CellState.BARRIER;
    }

    private boolean checkIfCellIsEmpty(Cell cellReference) {
        return cellReference.state == CellState.VACANT;
    }

    private void applyWallStateToCell(Cell cellReference, JButton buttonReference) {
        modifyCellProperties(cellReference, CellState.BARRIER);
        alterButtonAppearance(buttonReference, Color.BLACK);
    }

    private void applyEmptyStateToCell(Cell cellReference, JButton buttonReference) {
        modifyCellProperties(cellReference, CellState.VACANT);
        alterButtonAppearance(buttonReference, Color.WHITE);
    }
}