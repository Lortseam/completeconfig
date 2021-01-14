package me.lortseam.completeconfig.data;

import lombok.Getter;
import me.lortseam.completeconfig.data.entry.EntryOrigin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;

public class BoundedEntry<T extends Number> extends Entry<T> {

    private static final Logger LOGGER = LogManager.getLogger();

    @Getter
    private final T min, max;
    @Getter
    private final boolean slider;

    public BoundedEntry(EntryOrigin origin, T min, T max, boolean slider) {
        super(origin, value -> {
            if (new BigDecimal(value.toString()).compareTo(new BigDecimal(min.toString())) < 0) {
                LOGGER.warn("[CompleteConfig] Tried to set value of field " + origin.getField() + " to a value less than minimum bound, setting to minimum now!");
                return min;
            } else if (new BigDecimal(value.toString()).compareTo(new BigDecimal(max.toString())) > 0) {
                LOGGER.warn("[CompleteConfig] Tried to set value of field " + origin.getField() + " to a value greater than maximum bound, setting to maximum now!");
                return max;
            }
            return value;
        });
        this.min = min;
        this.max = max;
        this.slider = slider;
    }

    public BoundedEntry(EntryOrigin origin, T min, T max) {
        this(origin, min, max, false);
    }

}
