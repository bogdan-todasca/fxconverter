package application;

import core.*;
import core.Currency;
import ui.FXConverterUI;

import javax.swing.*;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

class Manager {
    private static final Manager INSTANCE = new Manager();
    private AtomicReference<FXConverterUI> ui = new AtomicReference<>();
    private AtomicReference<List<Currency>> data =
            new AtomicReference<>(new ArrayList<>(Arrays.asList(new Currency("RON", "Leu", "1"))));
    private final ExecutorService s = Executors.newSingleThreadExecutor();
    private static final String PROPERTIES_FILE = "fx.properties";

    static Manager getInstance() {
        return INSTANCE;
    }

    void start() {
        SwingUtilities.invokeLater(() -> {
            ui.set(new FXConverterUI(
                    Manager.this::compute,
                    Manager.this::saveToDisk,
                    loadProperties()));
            ui.get().setVisible(true);
            setStatus("Initializing");
            initializeDownloader();
        });
    }

    private void setStatus(final String s) {
        runOnSwing(() -> ui.get().setStatus(s));
    }

    private void setDate(final String s) {
        runOnSwing(() -> ui.get().setDate(s));
    }

    private void runOnSwing(final Runnable r) {
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            SwingUtilities.invokeLater(r);
        }
    }

    private void updateDropdowns(final List<Currency> data) {
        runOnSwing(() -> ui.get().updateDropDowns(data));
    }

    private void compute(final Currency from, final Currency to, final double amount) {
        s.submit(() -> {
            final double result = from.convert(to, amount);
            ui.get().getResultCallback().onDone(result);
        });
    }

    private void initializeDownloader() {
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

    private void saveToDisk(final Currency from, final Currency to, long amount) {
        final Properties p = new FXProperties(from.getDisplay(), to.getDisplay(), amount).getProperties();
        s.submit(() -> saveProperties(p));
    }

    private void saveProperties(final Properties p) {
        try {
            final Writer w = Files.newBufferedWriter(
                    Paths.get(System.getProperty("user.home") + "/" + PROPERTIES_FILE));
            p.store(w,
                    "FX properties");
            w.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private FXProperties loadProperties() {
        final Properties p = new Properties();
        final Path source = Paths.get(System.getProperty("user.home") + "/" + PROPERTIES_FILE);
        if (!Files.exists(source)) {
            return null;
        }
        try {
            final Reader r = Files.newBufferedReader(source);
            p.load(r);
            r.close();
            return FXProperties.from(p);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

}
