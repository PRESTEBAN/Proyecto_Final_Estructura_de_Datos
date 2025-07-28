package views;

import org.jfree.data.category.DefaultCategoryDataset;
//import org.w3c.dom.events.MouseEvent;
import org.jfree.data.category.CategoryDataset;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import models.AlgorithmResult;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTable;
import java.awt.Cursor;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.Font;
import dao.AlgorithmResultDAO;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ResultadosDialog extends JDialog {

    private final DefaultTableModel dataModel;
    private final AlgorithmResultDAO persistenceManager;
    private List<AlgorithmResult> algorithmResults;

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
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));

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
                        BorderFactory.createEmptyBorder(8, 16, 8, 16)
                ));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createRaisedBevelBorder(),
                        BorderFactory.createEmptyBorder(8, 16, 8, 16)
                ));
            }
        });
    }

    private void displayGraphicalResults() {
        if (this.algorithmResults.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No existen datos para graficar.");
            return;
        }

        DefaultCategoryDataset chartDataset = new DefaultCategoryDataset();
        for (AlgorithmResult resultEntry : this.algorithmResults) {
            chartDataset.addValue(resultEntry.retrieveExecutionTime(), "Tiempo en (ns)", resultEntry.fetchProcessName());
        }
        JFreeChart performanceChart = ChartFactory.createLineChart("Tiempos de Ejecución por Algoritmo", "Algoritmo", "Tiempo en (ns)", (CategoryDataset) chartDataset, PlotOrientation.VERTICAL, true, true, false);

        performanceChart.setBackgroundPaint(new java.awt.Color(255, 253, 208));
        performanceChart.getPlot().setBackgroundPaint(new java.awt.Color(255, 253, 208));

        Font titleFont = new Font("Comic Sans MS", Font.BOLD, 18);
        Font axisFont = new Font("Comic Sans MS", Font.BOLD, 14);
        Font legendFont = new Font("Comic Sans MS", Font.BOLD, 12);

        performanceChart.getTitle().setFont(titleFont);

        if (performanceChart.getLegend() != null) {
            performanceChart.getLegend().setItemFont(legendFont);
        }
        performanceChart.getCategoryPlot().getDomainAxis().setLabelFont(axisFont);
        performanceChart.getCategoryPlot().getDomainAxis().setTickLabelFont(axisFont);
        performanceChart.getCategoryPlot().getRangeAxis().setLabelFont(axisFont);
        performanceChart.getCategoryPlot().getRangeAxis().setTickLabelFont(axisFont);

        ChartPanel visualizationPanel = new ChartPanel(performanceChart);
        JDialog chartWindow = new JDialog(this, "Gráfica del tiempo de algoritmos", true);
        chartWindow.setContentPane((Container) visualizationPanel);

        chartWindow.setSize(600, 400);
        chartWindow.setLocationRelativeTo(this);
        chartWindow.setVisible(true);
    }

    private void populateDataTable() {
        this.algorithmResults = this.persistenceManager.retrieveAllRecords();
        for (AlgorithmResult resultRecord : this.algorithmResults) {
            this.dataModel.addRow(new Object[]{resultRecord
                .fetchProcessName(),
                Integer.valueOf(resultRecord.obtainRouteLength()),
                Long.valueOf(resultRecord.retrieveExecutionTime())});
        }
    }

    public ResultadosDialog(JFrame parentFrame, AlgorithmResultDAO dataAccessObject) {

        super(parentFrame, "Resultados Obtenidos De Algoritmos", true);
        this.persistenceManager = dataAccessObject;
        setLayout(new BorderLayout());
        java.awt.Color cream = new java.awt.Color(255, 253, 208);

        this.dataModel = new DefaultTableModel((Object[]) new String[]{"Algoritmo Utilizado", "Celdas recorridas", "Tiempo en (ns)"}, 0);

        JTable resultsTable = new JTable(this.dataModel);

        JTableHeader header = resultsTable.getTableHeader();
        header.setBackground(cream);
        header.setForeground(Color.BLACK);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));

        resultsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBackground(cream);
                setForeground(Color.BLACK);
                return this;
            }
        });

        JScrollPane scrollableTable = new JScrollPane(resultsTable);

        getContentPane().setBackground(cream);
        scrollableTable.getViewport().setBackground(cream);
        resultsTable.setBackground(cream);

        add(scrollableTable, "Center");
        populateDataTable();
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(cream);

        JButton clearResultsButton = new JButton("Limpiar Los Resultados");
        clearResultsButton.addActionListener(actionEvent -> {
            int confirmationResult = JOptionPane.showConfirmDialog(this, "¿Estás seguro que deseas borrar los resultados obtenidos?", "Confirmacióm", 0);
            if (confirmationResult == 0) {
                dataAccessObject.eraseStorageContent();
                this.dataModel.setRowCount(0);
            }
        });

        JButton chartDisplayButton = new JButton("Graficar Los Resultados");
        chartDisplayButton.addActionListener(actionEvent -> displayGraphicalResults());

        Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);

        Color clearButtonBg = new Color(220, 20, 60);
        Color clearButtonHover = new Color(255, 69, 0);

        Color chartButtonBg = new Color(255, 215, 0);
        Color chartButtonHover = new Color(255, 255, 102);

        Color buttonForeground = Color.WHITE;

        styleControlButton(clearResultsButton, buttonFont, clearButtonBg, buttonForeground, clearButtonHover);
        styleControlButton(chartDisplayButton, buttonFont, chartButtonBg, buttonForeground, chartButtonHover);

        controlPanel.add(clearResultsButton);
        controlPanel.add(chartDisplayButton);
        add(controlPanel, "South");

        setSize(500, 400);
        setLocationRelativeTo(parentFrame);
    }
}
