package View;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

/**
 * A coloumn renderer for the Jtable so that it is able to change the colour of its rows.
 */
class ColumnColorRenderer extends DefaultTableCellRenderer {
    private Color backgroundColor, foregroundColor;
    private  List<Integer> chosenRows;

    public ColumnColorRenderer(Color backgroundColor, Color foregroundColor, List<Integer> chosenRows) {
        super();
        this.backgroundColor = backgroundColor;
        this.foregroundColor = foregroundColor;
        this.chosenRows = chosenRows;
    }

    /**
     * Changes the colour of rows that were specified
     * @param table
     * @param value
     * @param isSelected
     * @param hasFocus
     * @param row
     * @param column
     * @return: The return of this method is irrelevant.
     */
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (chosenRows.contains(row)) {
            cell.setBackground(backgroundColor);
            cell.setForeground(foregroundColor);
        }
        else{
            cell.setForeground(Color.BLACK);
        }

        return cell;
    }
}
