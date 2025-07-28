import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import views.MazeFrame;

public class MazeApp {
    
    public static int[] requestGridDimensions() {
        try {
            String rowInput = JOptionPane.showInputDialog("Ingrese el número de filas: ");
            if (rowInput == null)
                return null; 
            String columnInput = JOptionPane.showInputDialog("Ingrese el número de columnas: ");
            if (columnInput == null)
                return null; 
            int rowCount = Integer.parseInt(rowInput.trim());
            int columnCount = Integer.parseInt(columnInput.trim());
            if (rowCount <= 4 || columnCount <= 4) {
                JOptionPane.showMessageDialog(null, "Se deben ingresar valores mayores a 4.");
                return null;
            } 
            return new int[] { rowCount, columnCount };
        } catch (NumberFormatException formatException) {
            JOptionPane.showMessageDialog(null, "Se debe ingresar numeros que sean válidos");
            return null;
        } 
    }
    
    public static void main(String[] applicationArguments) {
           int[] dimensionValues = requestGridDimensions();
        if (dimensionValues != null) {
            new MazeFrame(dimensionValues[0], dimensionValues[1]);
        }
    }
}
