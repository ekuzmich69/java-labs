package com.company;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class MainFrame extends JFrame {
    private static final int WIDTH = 700;
    private static final int HEIGHT = 500;

    private JFileChooser fileChooser = null;
    private JMenuItem saveToTextMenuItem;
    private JMenuItem searchValueMenuItem;
    private JMenuItem infoMenuItem;
    private JCheckBoxMenuItem showColumnMenuItem;
    private JCheckBoxMenuItem filterMenuItem;

    private JTextField textFieldFrom;
    private JTextField textFieldTo;
    private JTextField textFieldStep;
    private JTextField textFieldParameter;
    private Box hBoxResult;

    private TableCellVisualizer renderer = new TableCellVisualizer();
    private BoolTableCellVisualizer rendererBoolean = new BoolTableCellVisualizer();
    private GornerTable data;

    private JTable table;
    private TableColumn bool_column;

    public MainFrame(){
        super("Табулирование функции на отрезке");
        setSize(WIDTH, HEIGHT);
        Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - WIDTH)/2, (kit.getScreenSize().height - HEIGHT)/2);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("Файл");
        menuBar.add(fileMenu);
        JMenu tableMenu = new JMenu("Таблица");
        menuBar.add(tableMenu);
        JMenu infoMenu = new JMenu("Справка");
        menuBar.add(infoMenu);

        Action saveToTextAction = new AbstractAction( "Сохранить в текстовый файл") {
            public void actionPerformed(ActionEvent event) {
                if (fileChooser == null) {
                    fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File("."));
                }
                if (fileChooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION){
                    saveToTextFile(fileChooser.getSelectedFile());
                }
            }
        };
        saveToTextMenuItem = fileMenu.add(saveToTextAction);

        fileMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                if(data == null) saveToTextMenuItem.setEnabled(false);
                else saveToTextMenuItem.setEnabled(true);
            }

            @Override
            public void menuDeselected(MenuEvent e) { }

            @Override
            public void menuCanceled(MenuEvent e) { }
        });

        Action searchValueAction = new AbstractAction("Найти значение функции") {
            public void actionPerformed(ActionEvent event) {
                // Запросить пользователя ввести искомую строку
                String value = JOptionPane.showInputDialog(MainFrame.this, "Введите значение для поиска",
                        "Поиск значения", JOptionPane.QUESTION_MESSAGE);
                // Установить введенное значение в качестве иголки
                renderer.setNeedle(value);
                // Обновить таблицу
                getContentPane().repaint();
            }
        };
        // Добавить действие в меню "Таблица"
        searchValueMenuItem = tableMenu.add(searchValueAction);
        tableMenu.add(new JSeparator());
        showColumnMenuItem = new JCheckBoxMenuItem("Показать третий столбец", true);
        tableMenu.add(showColumnMenuItem);

        showColumnMenuItem.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {

                if(e.getStateChange() == 2) {
                    bool_column = table.getColumnModel().getColumn(2);
                    table.removeColumn(bool_column);
                }if(e.getStateChange() == 1){
                    table.addColumn(bool_column);
                }
            }
        });

        filterMenuItem = new JCheckBoxMenuItem("Фильтр", false);
        tableMenu.add(filterMenuItem);

        RowFilter<GornerTable, Integer> tableFilter = new RowFilter<GornerTable, Integer>() {
            @Override
            public boolean include(Entry<? extends GornerTable, ? extends Integer> entry) {
                Double value0 = (Double)entry.getValue(0);
                Double value1 = (Double)entry.getValue(1);
                Boolean temp0 = value0>=-2 && value0<=2;
                Boolean temp1 = value1>=-2 && value1<=2;
                return temp0||temp1;
            }
        };


        filterMenuItem.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {

                if(e.getStateChange() == 1){
                    TableRowSorter<GornerTable> sorter = new TableRowSorter<GornerTable>(data);
                    sorter.setRowFilter(tableFilter);
                    table.setRowSorter(sorter);
                }
                if(e.getStateChange() == 2) {
                    TableRowSorter<GornerTable> sorter = new TableRowSorter<GornerTable>(data);
                    table.setRowSorter(sorter);
                }
            }
        });

        tableMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                if(data == null){
                    showColumnMenuItem.setEnabled(false);
                    searchValueMenuItem.setEnabled(false);
                    filterMenuItem.setEnabled(false);
                }else{
                    showColumnMenuItem.setEnabled(true);
                    searchValueMenuItem.setEnabled(true);
                    filterMenuItem.setEnabled(true);
                }
            }

            @Override
            public void menuDeselected(MenuEvent e) { }

            @Override
            public void menuCanceled(MenuEvent e) { }
        });

        Action aboutProgrammAction = new AbstractAction("О программе") {
            public void actionPerformed(ActionEvent event) {
                //Box infoBox = Box.createVerticalBox();
                JLabel info = new JLabel("Кузьмич Евгений 6 группа");
                info.setIcon(null);
                info.setHorizontalTextPosition(JLabel.CENTER);
                info.setVerticalTextPosition(JLabel.BOTTOM);
                info.setIconTextGap(10);
                JOptionPane.showMessageDialog(MainFrame.this, info,
                        "О программе", JOptionPane.PLAIN_MESSAGE);
            }
        };
        infoMenuItem = infoMenu.add(aboutProgrammAction);

        textFieldFrom = new JTextField("0.0", 10);
        textFieldFrom.setMaximumSize(textFieldFrom.getPreferredSize());

        textFieldTo = new JTextField("1.0", 10);
        textFieldTo.setMaximumSize(textFieldTo.getPreferredSize());

        textFieldStep = new JTextField("0.1", 10);
        textFieldStep.setMaximumSize(textFieldStep.getPreferredSize());

        textFieldParameter = new JTextField("0.1", 10);
        textFieldParameter.setMaximumSize(textFieldStep.getPreferredSize());

        Box hboxXRange = Box.createHorizontalBox();
        hboxXRange.setBorder(BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), "Настройки:"));
        hboxXRange.add(Box.createHorizontalGlue());
        hboxXRange.add(new JLabel("X начальное:"));
        hboxXRange.add(Box.createHorizontalStrut(10));
        hboxXRange.add(textFieldFrom);
        hboxXRange.add(Box.createHorizontalStrut(20));
        hboxXRange.add(new JLabel("X конечное:"));
        hboxXRange.add(Box.createHorizontalStrut(10));
        hboxXRange.add(textFieldTo);
        hboxXRange.add(Box.createHorizontalStrut(20));
        hboxXRange.add(new JLabel("Шаг для X:"));
        hboxXRange.add(Box.createHorizontalStrut(10));
        hboxXRange.add(textFieldStep);
        hboxXRange.add(Box.createHorizontalStrut(20));
        hboxXRange.add(new JLabel("Слагаемое для Y:"));
        hboxXRange.add(Box.createHorizontalStrut(10));
        hboxXRange.add(textFieldParameter);
        hboxXRange.add(Box.createHorizontalGlue());

        // Установить предпочтительный размер области больше
        // минимального, чтобы при  компоновке область совсем не сдавили
        hboxXRange.setPreferredSize(new Dimension((int)(hboxXRange.getMaximumSize().getWidth()),
                (int)(hboxXRange.getMinimumSize().getHeight()*1.5)));

        getContentPane().add(hboxXRange, BorderLayout.NORTH);

        JButton buttonCalc = new JButton("Вычислить");
        buttonCalc.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                try {
                    showColumnMenuItem.setState(true);
                    filterMenuItem.setState(false);
                    filterMenuItem.setAccelerator(
                            KeyStroke.getKeyStroke(KeyEvent.getExtendedKeyCodeForChar('C'), InputEvent.ALT_DOWN_MASK));
                    Double from = Double.parseDouble(textFieldFrom.getText());
                    Double to = Double.parseDouble(textFieldTo.getText());
                    Double step = Double.parseDouble(textFieldStep.getText());
                    Double parameter = Double.parseDouble(textFieldParameter.getText());
                    data = new GornerTable(from, to, step, parameter);
                    // Создать новый экземпляр таблицы

                    table = new JTable(data);
                    table.setDefaultRenderer(Double.class, renderer);
                    table.setDefaultRenderer(Boolean.class, rendererBoolean);
                    table.setRowHeight(30);

                    hBoxResult.removeAll();
                    // Добавить в hBoxResult таблицу, "обернутую" в панель
                    // с полосами прокрутки
                    hBoxResult.add(new JScrollPane(table));
                    // Перерасположить компоненты в hBoxResult и выполнить
                    // перерисовку
                    hBoxResult.revalidate();

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(MainFrame.this,
                            "Ошибка в формате записи числа с плавающей точкой",
                            "Ошибочный формат числа", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        JButton buttonReset = new JButton("Очистить поля");
        buttonReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                textFieldFrom.setText("0.0");
                textFieldTo.setText("1.0");
                textFieldStep.setText("0.1");
                hBoxResult.removeAll();
                // Перерисовать сам hBoxResult
                hBoxResult.repaint();
                data = null;
            }
        });

        Box hboxButtons = Box.createHorizontalBox();
        hboxButtons.setBorder(BorderFactory.createEtchedBorder());
        hboxButtons.add(Box.createHorizontalGlue());
        hboxButtons.add(buttonCalc);
        hboxButtons.add(Box.createHorizontalStrut(30));
        hboxButtons.add(buttonReset);
        hboxButtons.add(Box.createHorizontalGlue());

        hboxButtons.setPreferredSize(new Dimension(
                (int)(hboxButtons.getMaximumSize().getWidth()),
                (int)(hboxButtons.getMinimumSize().getHeight()*1.5)));

        getContentPane().add(hboxButtons, BorderLayout.SOUTH);
        hBoxResult = Box.createHorizontalBox();
        getContentPane().add(hBoxResult);
    }

    protected void saveToTextFile(File selectedFile){
        try{
            // Создать новый символьный поток вывода, направленный в  указанный файл
            PrintStream out = new PrintStream(selectedFile);
            out.println("Результаты табулирования функции:");
            out.println("");
            out.println("Интервал от " + data.getFrom() + " до " + data.getTo()+
                    " с шагом " + data.getStep()+ "и параметром" + data.getParameter());
            out.println("====================================================");
            for (int i = 0; i<data.getRowCount(); i++)
            {
                out.println("Значение в точке " + data.getValueAt(i,0)  + " равно " +
                        data.getValueAt(i,1));
            }
            out.close();
        } catch (FileNotFoundException e){
            // Исключительную ситуацию "ФайлНеНайден" можно не
            // обрабатывать, так как мы файл создаем, а не открываем
        }
    }


}