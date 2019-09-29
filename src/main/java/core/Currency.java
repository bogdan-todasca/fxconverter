package core;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by bogdantodasca on 15/09/2019.
 */
public class Currency {
    private final String display;
    private final String description;
    private final String RONvalue;
    private final int multiplier;
    private static ThreadLocal<Pattern> multiplierPattern =
            ThreadLocal.withInitial(() -> Pattern.compile("(\\d+)([A-Z]+)"));

    public Currency(final String display){
        this.display = display;
        this.description = "";
        this.RONvalue = "";
        this.multiplier = 1;
    }

    public Currency(String display, String description, String roNvalue) {
        this.RONvalue = roNvalue;
        final Matcher m = multiplierPattern.get().matcher(display);
        this.multiplier = m.matches() ? Integer.parseInt(m.group(1)) : 1;
        this.display = m.matches() ? m.group(2) : display;
        this.description = m.matches() ? description.replace(m.group(1), "") : description;

    }

    public String getDisplay() {
        return display;
    }

    public String getDescription() {
        return description;
    }

    public String getRONvalue() {
        return RONvalue;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Currency currency = (Currency) o;
        return display.equals(currency.display);
    }

    @Override
    public int hashCode() {
        return Objects.hash(display);
    }

    @Override
    public String toString() {
        return String.format(" %s (%s)", display, description);
    }

    public double convert(final Currency to, final double amount){
        return amount / multiplier * Double.parseDouble(RONvalue) / Double.parseDouble(to.RONvalue) * to.multiplier;
    }
}
