package me.lortseam.completeconfig.data;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;

@Log4j2(topic = "CompleteConfig")
public class BoundedEntry<T extends Number> extends Entry<T> {

    @Getter
    private final T min, max;

    public BoundedEntry(EntryOrigin origin, T min, T max) {
        super(origin, value -> {
            if (new BigDecimal(value.toString()).compareTo(new BigDecimal(min.toString())) < 0) {
                logger.warn("Tried to set value of field " + origin.getField() + " to a value less than lower bound, setting to minimum now");
                return min;
            } else if (new BigDecimal(value.toString()).compareTo(new BigDecimal(max.toString())) > 0) {
                logger.warn("Tried to set value of field " + origin.getField() + " to a value greater than upper bound, setting to maximum now");
                return max;
            }
            return value;
        });
        this.min = min;
        this.max = max;
    }

}
