package views;

import controllers.MazeController;
import dao.AlgorithmResultDAO;
import dao.daoImpl.AlgorithmResultDAOFile;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import models.AlgorithmResult;
import models.Cell;
import models.CellState;
import models.SolveResults;
import solver.MazeSolver;
import solver.solverImpl.MazeSolverBFS;
import solver.solverImpl.MazeSolverBacktracking;
import solver.solverImpl.MazeSolverDFS;
import solver.solverImpl.MazeSolverRecursivo;
import solver.solverImpl.MazeSolverRecursivoCompleto;
import solver.solverImpl.MazeSolverRecursivoCompletoBT;

public class MazeFrame extends JFrame {

    private final MazePanel gridInterface;
    private final MazeController navigationController;
    private final JComboBox<String> algorithmSelectionComponent;
    private final JButton executeSolutionButton;
    private final JButton stepByStepExecutionButton = new JButton("Paso a paso");
    private List<Cell> stepProcessedCells;
    private List<Cell> stepSolutionPath;
    private int currentStepIndex = 0;
    private boolean hasExecutedStepByStep = false;
    private final AlgorithmResultDAO persistenceManager;
    private static final Map<CellState, Color> VISUAL_STATE_MAPPING = new HashMap<>();

    static {
        VISUAL_STATE_MAPPING.put(CellState.VACANT, new Color(255, 253, 208));
        VISUAL_STATE_MAPPING.put(CellState.BARRIER, Color.BLACK);
        VISUAL_STATE_MAPPING.put(CellState.ORIGIN, Color.GREEN);
        VISUAL_STATE_MAPPING.put(CellState.DESTINATION, Color.RED);
        VISUAL_STATE_MAPPING.put(CellState.ROUTE, Color.BLUE);
    }

    public MazePanel retrieveGridInterface() {
        return this.gridInterface;
    }

    private void styleControlButton(JButton button, Font font, Color background, Color foreground, Color hoverColor) {
        button.setFont(font);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(background);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLoweredBevelBorder(),
                        BorderFactory.createEmptyBorder(8, 16, 8, 16)));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createRaisedBevelBorder(),
                        BorderFactory.createEmptyBorder(8, 16, 8, 16)));
            }
        });
    }

    private void displayDeveloperInformation() {
        JPanel informationContainer = new JPanel();
        informationContainer.setLayout(new BoxLayout(informationContainer, 1));
        JLabel developerCredits = new JLabel("Desarrollado por Pedro Panjón, Esteban Pesantez , Axel Banegas");
        developerCredits.setAlignmentX(0.0F);
        informationContainer.add(developerCredits);
        informationContainer.add(Box.createVerticalStrut(10));
        JPanel linkContainer = new JPanel(new FlowLayout(0));
        try {
            ImageIcon githubIcon = new ImageIcon(getClass().getResource("/resources/github-original-wordmark.png"));
            Image scaledImage = githubIcon.getImage().getScaledInstance(30, 30, 4);
            JLabel iconLabel = new JLabel(new ImageIcon(scaledImage));
            linkContainer.add(iconLabel);
        } catch (Exception resourceException) {
            System.out.println("No se pudo cargar el logo local de GitHub.");
        }

        JLabel profileLink1 = new JLabel("<html><a href=''>PRESTEBAN</a></html>");
        profileLink1.setCursor(Cursor.getPredefinedCursor(12));
        profileLink1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent clickEvent) {
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/PRESTEBAN"));
                } catch (Exception browserException) {
                    browserException.printStackTrace();
                }
            }
        });
        linkContainer.add(profileLink1);

        linkContainer.add(new JLabel(" | "));

        JLabel profileLink2 = new JLabel("<html><a href=''>PEDROP57xd</a></html>");
        profileLink2.setCursor(Cursor.getPredefinedCursor(12));
        profileLink2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent clickEvent) {
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/PEDROP57xd"));
                } catch (Exception browserException) {
                    browserException.printStackTrace();
                }
            }
        });
        linkContainer.add(profileLink2);

        linkContainer.add(new JLabel(" | "));

        JLabel profileLink3 = new JLabel("<html><a href=''>axelbanegas</a></html>");
        profileLink3.setCursor(Cursor.getPredefinedCursor(12));
        profileLink3.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent clickEvent) {
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/axelbanegas"));
                } catch (Exception browserException) {
                    browserException.printStackTrace();
                }
            }
        });
        linkContainer.add(profileLink3);

        linkContainer.setAlignmentX(0.0F);
        informationContainer.add(linkContainer);
        JOptionPane.showMessageDialog(this, informationContainer, "Acerca de", 1);
    }

    private void reinitializeMazeStructure() {
        int[] dimensionValues = requestGridDimensions();
        if (dimensionValues == null) {
            return;
        }
        dispose();
        SwingUtilities.invokeLater(() -> new MazeFrame(dimensionValues[0], dimensionValues[1]));
    }

    public int[] requestGridDimensions() {
        try {
            String rowInput = JOptionPane.showInputDialog("Ingrese numero de filas:");
            if (rowInput == null) {
                return null;
            }
            String columnInput = JOptionPane.showInputDialog("Ingrese numero de columnas:");
            if (columnInput == null) {
                return null;
            }
            int rowCount = Integer.parseInt(rowInput.trim());
            int columnCount = Integer.parseInt(columnInput.trim());
            if (rowCount <= 4 || columnCount <= 4) {
                JOptionPane.showMessageDialog(null, "Debe ingresar valores mayores a 4.");
                return null;
            }
            return new int[] { rowCount, columnCount };
        } catch (NumberFormatException formatException) {
            JOptionPane.showMessageDialog(null, "Debe ingresar números válidos");
            return null;
        }
    }

    private void executeVisualAnimation(List<Cell> exploredCells, List<Cell> solutionRoute) {
        (new Thread(() -> {
            for (Cell currentCell : exploredCells) {
                if (currentCell.state == CellState.VACANT) {
                    applyVisualUpdateWithDelay(currentCell, CellState.VACANT);
                }
                try {
                    Thread.sleep(15L);
                } catch (InterruptedException threadException) {
                    threadException.printStackTrace();
                }
            }
            if (solutionRoute.isEmpty()) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "No se encontró ningún camino."));
                return;
            }

            for (Cell pathCell : solutionRoute) {
                if (pathCell.state != CellState.ORIGIN && pathCell.state != CellState.DESTINATION) {
                    applyVisualUpdateWithDelay(pathCell, CellState.ROUTE);
                }
                try {
                    Thread.sleep(40L);
                } catch (InterruptedException threadException) {
                    threadException.printStackTrace();
                }
            }
        })).start();
    }

    private void updateCellVisualRepresentation(Cell targetCell, CellState newVisualState) {
        JButton cellButton = this.gridInterface.fetchButtonAtPosition(targetCell.row, targetCell.col);
        cellButton.setBackground(VISUAL_STATE_MAPPING.getOrDefault(newVisualState, Color.WHITE));
    }

    private void applyVisualUpdateWithDelay(Cell targetCell, CellState newVisualState) {
        SwingUtilities.invokeLater(() -> updateCellVisualRepresentation(targetCell, newVisualState));
    }

    private void resetStepByStepExecution() {
        this.stepProcessedCells = null;
        this.stepSolutionPath = null;
        this.currentStepIndex = 0;
        this.hasExecutedStepByStep = false;
    }

    private SolveResults executeAlgorithmAndObtainResults() {
        Cell originCell = this.navigationController.obtainSourceReference();
        Cell destinationCell = this.navigationController.obtainTargetReference();
        if (originCell == null || destinationCell == null) {
            JOptionPane.showMessageDialog(this, "Debe establecer origen y destino.");
            return null;
        }
        this.gridInterface.clearVisitedCellStates();
        resetStepByStepExecution();
        String selectedAlgorithm = (String) this.algorithmSelectionComponent.getSelectedItem();
        MazeSolver algorithmImplementation;

        switch (selectedAlgorithm) {
            case "Recursivo":
                algorithmImplementation = new MazeSolverRecursivo();
                break;
            case "Recursivo Completo":
                algorithmImplementation = new MazeSolverRecursivoCompleto();
                break;
            case "Recursivo Completo BT":
                algorithmImplementation = new MazeSolverRecursivoCompletoBT();
                break;
            case "DFS":
                algorithmImplementation = new MazeSolverDFS();
                break;
            case "BFS":
                algorithmImplementation = new MazeSolverBFS();
                break;
            case "Backtracking":
                algorithmImplementation = new MazeSolverBacktracking();
                break;
            default:
                algorithmImplementation = new MazeSolverRecursivo();
                break;
        }

        long startTime = System.nanoTime();
        SolveResults algorithmResults = algorithmImplementation.getPath(this.gridInterface.retrieveCellMatrix(),
                originCell, destinationCell);
        long endTime = System.nanoTime();
        if (algorithmResults != null && !algorithmResults.solutionRoute.isEmpty()) {
            AlgorithmResult performanceData = new AlgorithmResult(selectedAlgorithm,
                    algorithmResults.solutionRoute.size(), endTime - startTime);
            this.persistenceManager.persistAlgorithmData(performanceData);
        }
        return algorithmResults;
    }

    public MazeFrame(int rowDimension, int columnDimension) {
        this.persistenceManager = (AlgorithmResultDAO) new AlgorithmResultDAOFile("results.csv");
        setTitle("Maze Solver – Proyecto Final Estructura de Datos");
        setDefaultCloseOperation(3);
        setSize(950, 700);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(255, 239, 184));
        this.gridInterface = new MazePanel(rowDimension, columnDimension);
        this.navigationController = new MazeController(this.gridInterface);
        this.gridInterface.assignNavigationController(this.navigationController);
        add(this.gridInterface, "Center");

        Font coolFont = new Font("Comic Sans MS", Font.BOLD, 14);
        UIManager.put("Button.font", coolFont);
        UIManager.put("Label.font", coolFont);
        UIManager.put("ComboBox.font", coolFont);
        UIManager.put("Menu.font", coolFont);
        UIManager.put("MenuItem.font", coolFont);
        UIManager.put("TitledBorder.font", coolFont);
        UIManager.put("Table.font", coolFont);
        UIManager.put("OptionPane.messageFont", coolFont);
        UIManager.put("OptionPane.buttonFont", coolFont);

        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(255, 253, 208));

        Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
        Color buttonBackground = new Color(70, 130, 180);
        Color buttonForeground = Color.WHITE;
        Color buttonHover = new Color(100, 149, 237);

        JButton sourceButton = new JButton("Set Start");
        styleControlButton(sourceButton, buttonFont, buttonBackground, buttonForeground, buttonHover);

        JButton targetButton = new JButton("Set End");
        styleControlButton(targetButton, buttonFont, buttonBackground, buttonForeground, buttonHover);

        JButton barrierButton = new JButton("Toggle Wall");
        styleControlButton(barrierButton, buttonFont, buttonBackground, buttonForeground, buttonHover);

        sourceButton.addActionListener(actionEvent -> this.navigationController
                .modifyInteractionType(MazeController.InteractionType.SOURCE_POINT));
        targetButton.addActionListener(actionEvent -> this.navigationController
                .modifyInteractionType(MazeController.InteractionType.TARGET_POINT));
        barrierButton.addActionListener(actionEvent -> this.navigationController
                .modifyInteractionType(MazeController.InteractionType.BARRIER_TOGGLE));

        controlPanel.add(sourceButton);
        controlPanel.add(targetButton);
        controlPanel.add(barrierButton);
        add(controlPanel, "North");

        String[] algorithmOptions = { "Recursivo", "Recursivo Completo", "Recursivo Completo BT", "BFS", "DFS",
                "Backtracking" };
        this.algorithmSelectionComponent = new JComboBox<>(algorithmOptions);

        this.algorithmSelectionComponent.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        this.algorithmSelectionComponent.setBackground(new Color(255, 253, 208));
        this.algorithmSelectionComponent.setOpaque(true);

        this.algorithmSelectionComponent.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(value.toString());
            label.setOpaque(true);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            label.setBackground(isSelected ? new Color(255, 239, 184) : new Color(255, 253, 208));
            return label;
        });

        JPanel solutionPanel = new JPanel();
        solutionPanel.setBackground(new Color(255, 253, 208));

        this.algorithmSelectionComponent.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        this.algorithmSelectionComponent.setBackground(Color.WHITE);

        JLabel algorithmLabel = new JLabel("Algoritmo:");
        algorithmLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        solutionPanel.add(algorithmLabel);
        solutionPanel.add(this.algorithmSelectionComponent);

        this.executeSolutionButton = new JButton("Resolver");
        Color solveButtonBg = new Color(34, 139, 34);
        Color solveButtonHover = new Color(50, 205, 50);
        styleControlButton(this.executeSolutionButton, buttonFont, solveButtonBg, buttonForeground, solveButtonHover);

        styleControlButton(this.stepByStepExecutionButton, buttonFont, new Color(255, 140, 0), buttonForeground,
                new Color(255, 165, 0));

        JButton clearButton = new JButton("Limpiar");
        Color clearButtonBg = new Color(220, 20, 60);
        Color clearButtonHover = new Color(255, 69, 0);
        styleControlButton(clearButton, buttonFont, clearButtonBg, buttonForeground, clearButtonHover);

        solutionPanel.add(this.executeSolutionButton);
        solutionPanel.add(this.stepByStepExecutionButton);
        solutionPanel.add(clearButton);
        add(solutionPanel, "South");

        this.executeSolutionButton.addActionListener(actionEvent -> {
            SolveResults executionResults = executeAlgorithmAndObtainResults();
            if (executionResults != null) {
                executeVisualAnimation(executionResults.exploredNodes, executionResults.solutionRoute);
            }
        });

        this.stepByStepExecutionButton.addActionListener(actionEvent -> {
            if (!this.hasExecutedStepByStep) {
                SolveResults executionResults = executeAlgorithmAndObtainResults();
                if (executionResults != null) {
                    this.stepProcessedCells = executionResults.exploredNodes;
                    this.stepSolutionPath = executionResults.solutionRoute;
                    this.currentStepIndex = 0;
                    this.hasExecutedStepByStep = true;
                }
            } else if (this.currentStepIndex < this.stepProcessedCells.size()) {
                Cell currentCell = this.stepProcessedCells.get(this.currentStepIndex++);
                if (currentCell.state == CellState.VACANT) {
                    updateCellVisualRepresentation(currentCell, CellState.ROUTE); 
                }

            } else if (this.currentStepIndex - this.stepProcessedCells.size() < this.stepSolutionPath.size()) {
                int pathIndex = this.currentStepIndex - this.stepProcessedCells.size();
                Cell pathCell = this.stepSolutionPath.get(pathIndex);
                this.currentStepIndex++;
                if (pathCell.state != CellState.ORIGIN && pathCell.state != CellState.DESTINATION) {
                    updateCellVisualRepresentation(pathCell, CellState.ROUTE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Ya se ha visto el recorrido completo.");
                this.hasExecutedStepByStep = false;
            }
        });

        clearButton.addActionListener(actionEvent -> {
            this.gridInterface.clearVisitedCellStates();
            resetStepByStepExecution();
        });

        JMenuBar menuBarComponent = new JMenuBar();
        JMenu fileMenu = new JMenu("Archivo");
        JMenuItem newMazeItem = new JMenuItem("Nuevo laberinto");
        newMazeItem.addActionListener(actionEvent -> reinitializeMazeStructure());
        fileMenu.add(newMazeItem);
        menuBarComponent.add(fileMenu);

        JMenu helpMenu = new JMenu("Ayuda");
        JMenuItem aboutItem = new JMenuItem("Acerca de");
        aboutItem.addActionListener(actionEvent -> displayDeveloperInformation());
        helpMenu.add(aboutItem);
        menuBarComponent.add(helpMenu);

        JMenuItem resultsItem = new JMenuItem("Ver resultados");
        resultsItem.addActionListener(actionEvent -> {
            ResultadosDialog resultsDialog = new ResultadosDialog(this, this.persistenceManager);
            resultsDialog.setVisible(true);
        });
        fileMenu.add(resultsItem);

        setJMenuBar(menuBarComponent);
        setVisible(true);
    }
}
