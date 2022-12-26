package com.company;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class BoolTableCellVisualizer implements TableCellRenderer {

    private JPanel panel = new JPanel();
    private JCheckBox check= new JCheckBox();

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        Boolean temp = (Boolean)value;
        if(temp == true) check.setSelected(true);
        else check.setSelected(false);
        return check;
    }
}
