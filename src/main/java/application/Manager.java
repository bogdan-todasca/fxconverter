package application;

import core.BNRFXDownloader;
import core.Callback;
import core.Currency;
import core.FXComputation;
import ui.FXConverterUI;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class Manager {
    private static final Manager INSTANCE = new Manager();
    private AtomicReference<FXConverterUI> ui = new AtomicReference<>();
    private AtomicReference<List<Currency>> data =
            new AtomicReference<>(new ArrayList<>(Arrays.asList(new Currency("RON", "Leu", "1"))));
    final ExecutorService s = Executors.newSingleThreadExecutor();

    static Manager getInstance() {
        return INSTANCE;
    }

    void start() {
        SwingUtilities.invokeLater(() -> {
            ui.set(new FXConverterUI(Manager.this::compute));
            ui.get().setVisible(true);
            setStatus("Initializing");
            initializeDownloader();
        });
    }

    void setStatus(final String s) {
        runOnSwing(() -> ui.get().setStatus(s));
    }

    void setDate(final String s) {
        runOnSwing(() -> ui.get().setDate(s));
    }

    void runOnSwing(final Runnable r) {
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            SwingUtilities.invokeLater(r);
        }
    }

    void updateDropdowns(final List<Currency> data) {
        runOnSwing(() -> ui.get().updateDropDowns(data));
    }

    void compute(final Currency from, final Currency to, final double amount) {
        s.submit(() -> {
            final double result = from.convert(to, amount);
            ui.get().getResultCallback().onDone(result);
        });
    }

    void initializeDownloader() {
        setStatus("Connecting");
        new BNRFXDownloader() {
            @Override
            public void onDone(List<Currency> data) {
                Manager.this.data.get().addAll(data);
                Manager.this.data.get().sort(Comparator.comparing(Currency::getDisplay));
                updateDropdowns(Manager.this.data.get());
                setStatus("Ready");
            }

            @Override
            public void showProgress(String s) {
                setStatus(s);
            }

            @Override
            protected void updateDate(String date) {
                setDate(date);
            }
        }.parsePage();
    }

}
