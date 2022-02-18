package me.lortseam.completeconfig.data;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.lortseam.completeconfig.util.NumberUtils;

@Slf4j(topic = "CompleteConfig")
public class BoundedEntry<T extends Number> extends Entry<T> {

    @Getter
    private final T min, max;

    public BoundedEntry(EntryOrigin origin, T min, T max) {
        super(origin);
        this.min = min;
        this.max = max;
    }

    @Override
    protected boolean update(T value) {
        if (NumberUtils.compare(value, min) < 0) {
            logger.warn("Tried to set value of field " + origin.getField() + " to a value less than lower bound, setting to minimum now");
            value = min;
        } else if (NumberUtils.compare(value, max) > 0) {
            logger.warn("Tried to set value of field " + origin.getField() + " to a value greater than upper bound, setting to maximum now");
            value = max;
        }
        return super.update(value);
    }

}
