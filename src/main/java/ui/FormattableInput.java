package ui;

import core.Notifier;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class FormattableInput {
    private final JTextField input;
    private final String placeholder;
    private static final Color PLACEHOLDER_COLOR = Color.GRAY;
    private static final Color REGULAR_COLOR = Color.BLACK;
    private boolean pauseEvents;
    private boolean placeholderVisible;
    private final Function<String, String> format;
    private final Function<String, Long> unformat;
    private final Set<Notifier> listeners = new HashSet<>();

    public FormattableInput(final String placeholder, final Function<String, String> format, final Function<String, Long> unformat) {
        this.input = new JTextField();
        this.placeholder = placeholder;
        this.format = format;
        this.unformat = unformat;
        setPlaceHolderVisible(true);
        initEvents();

    }

    private void initEvents() {
        this.input.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                textChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                textChanged();

            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                textChanged();

            }
        });
        this.input.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (input.getText().equals(placeholder)) {
                    setPlaceHolderVisible(false);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (input.getText().trim().isEmpty()) {
                    setPlaceHolderVisible(true);
                }
            }
        });
    }

    private void textChanged() {
        if (pauseEvents) {
            return;
        }
        pauseEvents = true;
        SwingUtilities.invokeLater(() -> {
            if (!placeholderVisible) {
                input.setText(format.apply(input.getText()));
            }
            notifyListeners();
            pauseEvents = false;
        });

    }

    private void setPlaceHolderVisible(boolean visible) {
        if (placeholderVisible == visible) {
            return;
        }
        SwingUtilities.invokeLater(() -> {

            if (visible) {
                input.setText(placeholder);
                input.setFont(input.getFont().deriveFont(Font.ITALIC));
                input.setForeground(PLACEHOLDER_COLOR);
                input.setCaretPosition(0);
            } else {
                input.setText(input.getText().replaceFirst(placeholder, ""));
                input.setFont(input.getFont().deriveFont(Font.PLAIN));
                input.setForeground(REGULAR_COLOR);
            }
            placeholderVisible = visible;
        });
    }

    public JTextField getComponent() {
        return input;
    }

    public long getValue() {
        return placeholderVisible ? 0 : unformat.apply(input.getText());
    }

    public void addAmountChangedListener(Notifier listener) {
        this.listeners.add(listener);
    }

    private void notifyListeners() {
        this.listeners.forEach(Notifier::notifyChanged);
    }
}
