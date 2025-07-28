import javax.swing.JOptionPane;
import views.MazeFrame;

public class MazeApp {

    public static void main(String[] applicationArguments) {
        int[] dimensionValues = null;
        try {
            String rowInput = JOptionPane.showInputDialog("Ingrese el número de filas: ");
            if (rowInput != null) {
                String columnInput = JOptionPane.showInputDialog("Ingrese el número de columnas: ");
                if (columnInput != null) {
                    int rowCount = Integer.parseInt(rowInput.trim());
                    int columnCount = Integer.parseInt(columnInput.trim());
                    if (rowCount <= 4 || columnCount <= 4) {
                        JOptionPane.showMessageDialog(null, "Se deben ingresar valores mayores a 4.");
                    } else {
                        dimensionValues = new int[] { rowCount, columnCount };
                    }
                }
            }
        } catch (NumberFormatException formatException) {
            JOptionPane.showMessageDialog(null, "Se debe ingresar números que sean válidos");
        }

        if (dimensionValues != null) {
            new MazeFrame(dimensionValues[0], dimensionValues[1]);
        }
    }
}
