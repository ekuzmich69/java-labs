package com.company;
import javax.swing.table.AbstractTableModel;

public class GornerTable extends AbstractTableModel{

    private Double from, to, step, parameter;

    public GornerTable(Double from, Double to, Double step, Double parameter) {
        this.from = from;
        this.to = to;
        this.step = step;
        this.parameter = parameter;
    }

    public Double getParameter() { return parameter; }

    public Double getFrom() {
        return from;
    }

    public Double getTo() {
        return to;
    }

    public Double getStep() {
        return step;
    }

    @Override
    public int getRowCount() {
        return (int)(Math.ceil((to-from)/step))+1;
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Double x = from + step*rowIndex;
        Double y = x + parameter;
        String s = String.valueOf(y);
        Boolean z = (s.substring (0,1)).toUpperCase().equals(s.substring(s.length()-1).toUpperCase()) ;
        switch (columnIndex){
            case 0: return x;
            case 1: return y;
            case 2: return z;
        }
        return null;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if(columnIndex == 2) return Boolean.class;
        return Double.class;
    }

    @Override
    public String getColumnName(int column) {
        switch (column){
            case 0: return "Значение Х";
            case 1: return "Значение Y";
            case 2: return "Краевая симметрия?";
        }
        return "";
    }
}