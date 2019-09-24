package ui;

import core.Callback;
import core.Currency;
import core.FXComputation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by bogdantodasca on 15/09/2019.
 */
public class FXConverterUI extends JFrame {

    private final DefaultComboBoxModel<Currency> fromModel = new DefaultComboBoxModel<>();
    private final DefaultComboBoxModel<Currency> toModel = new DefaultComboBoxModel<>();
    private JLabel resultValue;
    private JLabel status;
    private JLabel date;
    private JFormattedTextField input;
    private final FXComputation fxComputation;
    private JComboBox<Currency> from;
    private JComboBox<Currency> to;

    public FXConverterUI(final FXComputation fxComputation) {
        this.fxComputation = fxComputation;
        setTitle("FX Converter");
        initLayout();
        initEvents();
        setPreferredSize(new Dimension(450, 165));
        pack();
    }

    private void initLayout() {
        final JPanel content = new JPanel();

        content.setLayout(new BorderLayout());

        final JPanel table = new JPanel(new GridLayout(4, 2));
        final JLabel fromLabel = new JLabel("From: ");
        final JLabel toLabel = new JLabel("To:");
        from = new JComboBox<>(fromModel);
        to = new JComboBox<>(toModel);
        final JLabel resultLabel = new JLabel("Result");
        resultValue = new JLabel("N/A");
        table.add(fromLabel);
        table.add(from);
        table.add(toLabel);
        table.add(to);
        table.add(new JLabel("Amount:"));
        input = new JFormattedTextField(new DecimalFormat("###,###.###"));

        table.add(input);

        table.add(resultLabel);
        table.add(resultValue);

        content.add(BorderLayout.CENTER, table);
        status = new JLabel("", JLabel.CENTER);
        status.setForeground(Color.RED);
        content.add(BorderLayout.NORTH, status);

        date = new JLabel("N/A", JLabel.CENTER);
        date.setForeground(Color.RED);
        content.add(BorderLayout.SOUTH, date);
        setContentPane(content);

    }

    private void initEvents() {
        this.input.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                final String amount = FXConverterUI.this.input.getText();
                System.out.println("Using amount" + amount);
                FXConverterUI.this.fxComputation.
                        compute(
                                (Currency)from.getSelectedItem(),
                                (Currency)to.getSelectedItem(),
                                amount.matches("-?\\d+") ? Double.parseDouble(amount) : 0);

            }
        });
    }

    public Callback<Double> getResultCallback() {
        return result -> SwingUtilities.invokeLater(() -> this.resultValue.setText(result.toString()));
    }

    public void setStatus(final String newStatus) {
        status.setText(newStatus);
    }

    public void setDate(final String s) {
        date.setText(String.format("FX: %s", s));
    }

    public void updateDropDowns(List<Currency> data) {
        data.forEach(fromModel::addElement);
        data.forEach(toModel::addElement);
    }
}
