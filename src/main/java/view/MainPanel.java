package view;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

final class MainPanel extends JPanel {
    private static final JPanel cardsPanel = createDoubleBufferedPanel(new CardLayout());
    private static final JPanel buttonsPanel = createDoubleBufferedPanel(null);
    private static final Dimension buttonDimension = new Dimension(270, 50);

    @FunctionalInterface
    interface RowCountGetter {
        int getRowCount();
    }

    @FunctionalInterface
    interface ValueGetter {
        Object getValueAt();
    }

    @FunctionalInterface
    interface TableViewShowingHandler {
        void showTableView();
    }

    MainPanel(final String name) {
        super(new BorderLayout(), true);
        setName(name);
        add(cardsPanel, BorderLayout.CENTER);

        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.add(DefaultView.createLabel("Wyświetl " + name.toLowerCase(), buttonDimension));
        setEmptyBorder(buttonsPanel, 30, 20, 30, 50);

        add(buttonsPanel, BorderLayout.EAST);

        //addTableView("wszystkie", cardsPanel, "wszystkie", dimension, buttonsPanel, rowCountGetter, valueGetter, tableViewShowingHandler);
    }

    static final void addTableView(final String name, final RowCountGetter rowCountGetter, final ValueGetter valueGetter, final String text, final TableViewShowingHandler tableViewShowingHandler, final String... columnNames) {
        final JPanel tablePanel = createDoubleBufferedPanel(new BorderLayout());
        tablePanel.setName(name);

        final JTable table = new JTable(new AbstractTableModel() {
            @Override
            public final int getColumnCount() {
                return columnNames.length;
            }

            @Override
            public final String getColumnName(final int columnIndex) {
                return columnNames[columnIndex];
            }

            @Override
            public final int getRowCount() {
                try {
                    return rowCountGetter.getRowCount();
                } catch (final NullPointerException e) {
                    return 0;
                }
            }

            @Override
            public final Object getValueAt(final int rowIndex, final int columnIndex) {
                return valueGetter.getValueAt();
            }
        });
        table.setAutoCreateRowSorter(true);

        final JScrollPane scrollPane = new JScrollPane(table);
        setEmptyBorder(scrollPane, 20, 10, 10, 10);

        tablePanel.add(scrollPane, BorderLayout.CENTER);

        final JPanel internalButtonsPanel = createDoubleBufferedPanel(new FlowLayout());
        internalButtonsPanel.add(createAdjustedButton("Dodaj rekord", null));
        internalButtonsPanel.add(createAdjustedButton("Usuń rekordy", null));
        setEmptyBorder(internalButtonsPanel, 10, 10, 80, 10);

        tablePanel.add(internalButtonsPanel, BorderLayout.SOUTH);

        cardsPanel.add(tablePanel);

        final JButton button = createAdjustedButton(text, e1 -> {
            try {
                tableViewShowingHandler.showTableView();
            } catch (final NullPointerException e) {
                ((CardLayout)cardsPanel.getLayout()).show(cardsPanel, name);
            }
        });
        DefaultView.setDefaultJComponentProperties(button, buttonDimension);

        buttonsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonsPanel.add(button);
    }

    private static JPanel createDoubleBufferedPanel(final LayoutManager layout) {
        return new JPanel(layout, true);
    }

    private static void setEmptyBorder(@NotNull final JComponent component, final int top, final int left, final int bottom, final int right) {
        component.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
    }

    private static JButton createAdjustedButton(final String text, final ActionListener actionListener) {
        final JButton button = DefaultView.createButton(text, actionListener);
        button.setFont(new Font(DefaultView.FONT_NAME, Font.PLAIN, 20));
        return button;
    }
}
