package com.company;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

public class TableCellVisualizer implements TableCellRenderer{

    private JPanel panel = new JPanel();
    private JLabel label = new JLabel();
    private String needle = null;
    private DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();


    public TableCellVisualizer() {
        panel.add(label);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        formatter.setMaximumFractionDigits(5);
        formatter.setGroupingUsed(false);
        DecimalFormatSymbols dottedDouble = formatter.getDecimalFormatSymbols();
        dottedDouble.setDecimalSeparator('.');
        formatter.setDecimalFormatSymbols(dottedDouble);
    }

    public void setNeedle(String needle) {
        this.needle = needle;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        String formattedDouble = formatter.format(value);
        label.setText(formattedDouble);
        double d = Double.parseDouble(formattedDouble);
        int part = (int) d;
        int sum=0;
        for(int i=0;;i++)
        {
            if(part!=0)
            {
                sum += (part % 10);
                part /= 10;
            }
            else
            {
                break;
            }
            if(part<1)
            {
                break;
            }
        }

        if(column==1 && needle!=null && needle.equals(formattedDouble)){
            panel.setBackground(Color.red);
        } else{
            if(sum%10 == 0) panel.setBackground(Color.YELLOW);
            if(sum%10 != 0) panel.setBackground(Color.white);
        }
        return panel;
    }
}