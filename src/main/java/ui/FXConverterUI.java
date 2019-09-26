package ui;

import core.Callback;
import core.Currency;
import core.FXComputation;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
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
    private FormattableInput input;
    private final FXComputation fxComputation;
    private JComboBox<Currency> from;
    private JComboBox<Currency> to;
    private JCheckBox saveData;
    private DecimalFormat amountFormat = new DecimalFormat("###,###");
    private JButton swapButton;
    public FXConverterUI(final FXComputation fxComputation) {
        this.fxComputation = fxComputation;
        setTitle("FX Converter");
        initLayout();
        initEvents();
        setPreferredSize(new Dimension(450, 165));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
        resultValue = new JLabel("N/A", JLabel.RIGHT);
        resultValue.setFont(resultValue.getFont().deriveFont(24F).deriveFont(Font.BOLD));
        resultValue.setForeground(Color.PINK);
        table.add(fromLabel);
        table.add(from);
        table.add(toLabel);
        table.add(to);
        table.add(new JLabel("Amount:"));
        input = new FormattableInput("Enter amount", this::format, this::unformat);

        table.add(input.getComponent());

        table.add(resultLabel);
        table.add(resultValue);

        content.add(BorderLayout.CENTER, table);
        status = new JLabel("", JLabel.CENTER);
        status.setForeground(Color.RED);
        content.add(BorderLayout.NORTH, status);

        date = new JLabel("N/A", JLabel.CENTER);
        date.setForeground(Color.RED);
        final JPanel panel = new JPanel(new BorderLayout());
        saveData = new JCheckBox("Save Data");
        saveData.setSelected(true);
        swapButton = new JButton();
        try {
            final Image img = ImageIO.read(getClass().getClassLoader().getResource("Webp.net-resizeimage.png"));
            swapButton.setIcon(new ImageIcon(img));
            swapButton.setPreferredSize(new Dimension(50, 20));
        } catch (IOException e) {
            e.printStackTrace();
        }

        panel.add(saveData, BorderLayout.WEST);
        panel.add(date, BorderLayout.CENTER);
        panel.add(swapButton, BorderLayout.EAST);
        content.add(BorderLayout.SOUTH, panel);
        setContentPane(content);

    }

    private void initEvents() {
        this.input.addAmountChangedListener(this::computeResult);
        this.from.addItemListener(e -> computeResult());
        this.to.addItemListener(e -> computeResult());
        this.swapButton.addActionListener(x -> {
            final Currency c1 = (Currency) from.getSelectedItem();
            final Currency c2 = (Currency) to.getSelectedItem();
            from.setSelectedItem(c2);
            to.setSelectedItem(c1);
        });
    }

    private void computeResult() {
        FXConverterUI.this.fxComputation.
                compute(
                        (Currency) from.getSelectedItem(),
                        (Currency) to.getSelectedItem(),
                        FXConverterUI.this.input.getValue());
    }

    public Callback<Double> getResultCallback() {
        return result -> SwingUtilities.invokeLater(() ->
                this.resultValue.setText(amountFormat.format(result) + " " +
                        ((Currency) to.getSelectedItem()).getDisplay()));
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

    private String format(String value) {
        if (value.trim().isEmpty()) {
            return "";
        }
        if(value.endsWith("k")){
            value = value.replace("k", "000");
        }
        final long number = Long.parseLong(value.replaceAll(",", ""));
        return amountFormat.format(number);
    }

    private long unformat(String value) {
        return value.trim().isEmpty() ? 0 : Long.parseLong(value.replaceAll(",", ""));
    }
}
