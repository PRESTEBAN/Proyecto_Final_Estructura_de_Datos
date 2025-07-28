package views;

import controllers.MazeController;
import dao.AlgorithmResultDAO;
import dao.daoImpl.AlgorithmResultDAOFile;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import models.AlgorithmResult;
import models.Cell;
import models.CellState;
import models.SolveResults;
import solver.MazeSolver;
import solver.solverImpl.*;


public class MazeFrame extends JFrame {
    
    private final MazePanel gridPanel;
    private final MazeController gameController;
    private final JComboBox<String> algorithmChooser;
    private final JButton solveBtn;
    private final JButton stepBtn;
    private final AlgorithmResultDAO resultStorage;
    

    private List<Cell> stepVisited;
    private List<Cell> stepPath;
    private int stepCounter;
    private boolean isStepMode;
    

    private final Color[] cellColors = {
        new Color(240, 248, 255), 
        new Color(47, 79, 79),      
        new Color(50, 205, 50),    
        new Color(220, 20, 60),    
        new Color(30, 144, 255)    
    };
    
    public MazeFrame(int rows, int cols) {
        resultStorage = new AlgorithmResultDAOFile("results.csv");
        
        setupMainWindow();
        
        gridPanel = new MazePanel(rows, cols);
        gameController = new MazeController(gridPanel);
        
        algorithmChooser = createAlgorithmSelector();
        solveBtn = createSolveButton();
        stepBtn = createStepButton();
        
        assembleInterface();
        setupEventHandlers();
        
        setVisible(true);
    }
    

    private void setupMainWindow() {
        setTitle("Maze Solver - Proyecto final");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(245, 245, 220)); 
    }
    

    private JComboBox<String> createAlgorithmSelector() {
        String[] algorithms = {
            "Recursivo", 
            "Recursivo Completo", 
            "Recursivo Completo BT", 
            "BFS", 
            "DFS", 
            "Backtracking"
        };
        
        JComboBox<String> selector = new JComboBox<>(algorithms);
        selector.setFont(new Font("Arial", Font.PLAIN, 12));
        return selector;
    }
    

    private JButton createSolveButton() {
        JButton btn = new JButton("Encontrar Solución");
        styleButton(btn, new Color(34, 139, 34), Color.WHITE);
        return btn;
    }
    
    private JButton createStepButton() {
        JButton btn = new JButton("Ver Paso a Paso");
        styleButton(btn, new Color(255, 140, 0), Color.WHITE);
        return btn;
    }
    

    private void styleButton(JButton button, Color bg, Color fg) {
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setOpaque(true);
    }

    private void assembleInterface() {
        setLayout(new BorderLayout());
        
        add(gridPanel, BorderLayout.CENTER);
        add(createControlPanel(), BorderLayout.NORTH);
        add(createSolutionPanel(), BorderLayout.SOUTH);
        
        setJMenuBar(createMenuSystem());
    }
    

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(new Color(245, 245, 220));
        
        JButton startBtn = new JButton("Colocar Inicio");
        JButton endBtn = new JButton("Colocar Final");
        JButton wallBtn = new JButton("Alternar Pared");
        
        styleButton(startBtn, new Color(70, 130, 180), Color.WHITE);
        styleButton(endBtn, new Color(178, 34, 34), Color.WHITE);
        styleButton(wallBtn, new Color(105, 105, 105), Color.WHITE);
        
        startBtn.addActionListener(e -> gameController.modifyInteractionType(
            MazeController.InteractionMode.SOURCE_POINT));
        endBtn.addActionListener(e -> gameController.modifyInteractionType(
            MazeController.InteractionMode.TARGET_POINT));
        wallBtn.addActionListener(e -> gameController.modifyInteractionType(
            MazeController.InteractionMode.BARRIER_TOGGLE));
        
        panel.add(startBtn);
        panel.add(endBtn);
        panel.add(wallBtn);
        
        return panel;
    }
    

    private JPanel createSolutionPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(new Color(245, 245, 220));
        
        panel.add(new JLabel("Algoritmo:"));
        panel.add(algorithmChooser);
        panel.add(solveBtn);
        panel.add(stepBtn);
        
        JButton clearBtn = new JButton("Limpiar Todo");
        styleButton(clearBtn, new Color(220, 20, 60), Color.WHITE);
        clearBtn.addActionListener(e -> resetGrid());
        panel.add(clearBtn);
        
        return panel;
    }
    

    private JMenuBar createMenuSystem() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("Archivo");
        JMenuItem newItem = new JMenuItem("Nuevo Laberinto");
        JMenuItem resultsItem = new JMenuItem("Ver Resultados");
        
        newItem.addActionListener(e -> createNewMaze());
        resultsItem.addActionListener(e -> showResults());
        
        fileMenu.add(newItem);
        fileMenu.add(resultsItem);
        
        JMenu helpMenu = new JMenu("Ayuda");
        JMenuItem aboutItem = new JMenuItem("Acerca de");
        aboutItem.addActionListener(e -> showAbout());
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        
        return menuBar;
    }
    

    private void setupEventHandlers() {
        solveBtn.addActionListener(e -> executeSolution());
        stepBtn.addActionListener(e -> executeStepByStep());
    }
    

    private void executeSolution() {
        SolveResults results = runSelectedAlgorithm();
        if (results != null) {
            animateResults(results);
        }
    }
    

    private void executeStepByStep() {
        if (!isStepMode) {
            initializeStepMode();
        } else {
            performNextStep();
        }
    }
    
    
    private void initializeStepMode() {
        SolveResults results = runSelectedAlgorithm();
        if (results != null) {
            stepVisited = results.getVisitedCells();
            stepPath = results.getSolutionPath();
            stepCounter = 0;
            isStepMode = true;
            stepBtn.setText("Siguiente Paso");
        }
    }
    
    
    private void performNextStep() {
        if (stepCounter < stepVisited.size()) {
            Cell cell = stepVisited.get(stepCounter);
            if (cell.state == CellState.VACANT) {
                paintCell(cell, cellColors[CellState.ROUTE.ordinal()]);
            }
            stepCounter++;
        } else if (stepCounter - stepVisited.size() < stepPath.size()) {
            int pathIndex = stepCounter - stepVisited.size();
            Cell cell = stepPath.get(pathIndex);
            if (cell.state != CellState.ORIGIN && cell.state != CellState.DESTINATION) {
                paintCell(cell, cellColors[CellState.ROUTE.ordinal()]);
            }
            stepCounter++;
        } else {
            finishStepMode();
        }
    }
    
    
    private void finishStepMode() {
        JOptionPane.showMessageDialog(this, "Recorrido completado");
        isStepMode = false;
        stepBtn.setText("Ver Paso a Paso");
    }
    

    private SolveResults runSelectedAlgorithm() {
        Cell start = gameController.obtainSourceReference();
        Cell end = gameController.obtainTargetReference();
        
        if (start == null || end == null) {
            JOptionPane.showMessageDialog(this, "Debe ingresar inicio y final");
            return null;
        }
        
        resetGrid();
        
        MazeSolver solver = createSolverInstance();
        long startTime = System.nanoTime();
        SolveResults results = solver.findSolution(gridPanel.retrieveCellMatrix(), start, end);
        long endTime = System.nanoTime();
        
        if (results.isPathFound()) {
            saveResult(results, endTime - startTime);
        }
        
        return results;
    }
    
    
    private MazeSolver createSolverInstance() {
        String selected = (String) algorithmChooser.getSelectedItem();
        
        switch (selected) {
            case "Recursivo":
                return new MazeSolverRecursivo();
            case "Recursivo Completo":
                return new MazeSolverRecursivoCompleto();
            case "Recursivo completo BT":
                return new MazeSolverRecursivoCompletoBT();
            case "BFS":
                return new MazeSolverBFS();
            case "DFS":
                return new MazeSolverDFS();
            case "Backtracking":
                return new MazeSolverBacktracking();
            default:
                return new MazeSolverRecursivo();
        }
    }
    
   
    private void saveResult(SolveResults results, long time) {
        String algorithmName = (String) algorithmChooser.getSelectedItem();
        AlgorithmResult result = new AlgorithmResult(algorithmName, results.getPathLength(), time);
        resultStorage.saveResult(result);
    }
    
    
    private void animateResults(SolveResults results) {
        new Thread(() -> {
            for (Cell cell : results.getVisitedCells()) {
                if (cell.state == CellState.VACANT) {
                    SwingUtilities.invokeLater(() -> 
                        paintCell(cell, new Color(255, 192, 203))); 
                }
                sleep(20);
            }
            
           
            for (Cell cell : results.getSolutionPath()) {
                if (cell.state != CellState.ORIGIN && cell.state != CellState.DESTINATION) {
                    SwingUtilities.invokeLater(() -> 
                        paintCell(cell, cellColors[CellState.ROUTE.ordinal()]));
                }
                sleep(50);
            }
            
            if (!results.isPathFound()) {
                SwingUtilities.invokeLater(() -> 
                    JOptionPane.showMessageDialog(this, "No se encontró solución"));
            }
        }).start();
    }
    
   
    private void paintCell(Cell cell, Color color) {
        gridPanel.fetchButtonAtPosition(cell.row, cell.col).setBackground(color);
    }
    

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
 
    private void resetGrid() {
        gridPanel.clearVisitedCellStates();
        isStepMode = false;
        stepBtn.setText("Ver Paso a Paso");
    }
    
    
    private void createNewMaze() {
        int[] dimensions = requestDimensions();
        if (dimensions != null) {
            dispose();
            SwingUtilities.invokeLater(() -> new MazeFrame(dimensions[0], dimensions[1]));
        }
    }
    
   
    private int[] requestDimensions() {
        try {
            String rowStr = JOptionPane.showInputDialog("Número de filas:");
            if (rowStr == null) return null;
            
            String colStr = JOptionPane.showInputDialog("Número de columnas:");
            if (colStr == null) return null;
            
            int rows = Integer.parseInt(rowStr.trim());
            int cols = Integer.parseInt(colStr.trim());
            
            if (rows < 5 || cols < 5) {
                JOptionPane.showMessageDialog(null, "Mínimo 5 filas y columnas");
                return null;
            }
            
            return new int[]{rows, cols};
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Ingrese números válidos");
            return null;
        }
    }
    

    private void showResults() {
        ResultadosDialog dialog = new ResultadosDialog(this, resultStorage);
        dialog.setVisible(true);
    }
    
    
   private void showAbout() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    panel.add(new JLabel("Maze Solver - Proyecto Final"));
    panel.add(Box.createVerticalStrut(10));
    panel.add(new JLabel("DESARROLLADO POR:"));
    panel.add(new JLabel("Esteban Pesantez, Pedro Panjon, Axel Banegas"));
    panel.add(Box.createVerticalStrut(10));

    JLabel link1 = createLinkLabel("PRESTEBAN", "https://github.com/PRESTEBAN");
    JLabel link2 = createLinkLabel("PEDROP57xd", "https://github.com/PEDROP57xd");
    JLabel link3 = createLinkLabel("axelbanegas", "https://github.com/axelbanegas");

    panel.add(link1);
    panel.add(link2);
    panel.add(link3);

    JOptionPane.showMessageDialog(this, panel, "Información", JOptionPane.INFORMATION_MESSAGE);
}


private JLabel createLinkLabel(String text, String url) {
    JLabel linkLabel = new JLabel("<html><a href=''>" + text + "</a></html>");
    linkLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    linkLabel.setForeground(Color.BLUE);

    linkLabel.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent e) {
            try {
                Desktop.getDesktop().browse(new java.net.URI(url));
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "No se pudo abrir el enlace");
            }
        }
    });

    return linkLabel;
}


}