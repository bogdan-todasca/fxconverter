package core;

import java.util.Properties;

public class FXProperties {
    private final String from;
    private final String to;
    private final Long amount;
    private final Properties p = new Properties();
    private static final String FROM_KEY = "from";
    private static final String TO_KEY = "to";
    private static final String AMOUNT_KEY = "amount";

    public FXProperties(String from, String to, long amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
        init();
    }

    public static FXProperties from(Properties p) {
        return new FXProperties(p.getProperty(FROM_KEY),
                p.getProperty(TO_KEY),
                Long.valueOf(String.valueOf(p.getOrDefault(AMOUNT_KEY, "0L"))));
    }

    private void init() {
        p.setProperty(FROM_KEY, from);
        p.setProperty(TO_KEY, to);
        p.setProperty(AMOUNT_KEY, Long.toString(amount));
    }

    public final Properties getProperties() {
        return p;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public Long getAmount() {
        return amount;
    }
}
