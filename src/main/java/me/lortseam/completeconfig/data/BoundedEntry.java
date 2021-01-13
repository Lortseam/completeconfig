package me.lortseam.completeconfig.data;

import lombok.Getter;
import me.lortseam.completeconfig.api.ConfigEntryContainer;
import me.lortseam.completeconfig.data.text.TranslationIdentifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.math.BigDecimal;

public class BoundedEntry<T extends Number> extends Entry<T> {

    private static final Logger LOGGER = LogManager.getLogger();

    @Getter
    private final T min, max;
    @Getter
    private final boolean slider;

    BoundedEntry(Field field, ConfigEntryContainer parentObject, TranslationIdentifier parentTranslation, T min, T max, boolean slider) {
        super(field, parentObject, parentTranslation, value -> {
            if (new BigDecimal(value.toString()).compareTo(new BigDecimal(min.toString())) < 0) {
                LOGGER.warn("[CompleteConfig] Tried to set value of field " + field + " to a value less than minimum bound, setting to minimum now!");
                return min;
            } else if (new BigDecimal(value.toString()).compareTo(new BigDecimal(max.toString())) > 0) {
                LOGGER.warn("[CompleteConfig] Tried to set value of field " + field + " to a value greater than maximum bound, setting to maximum now!");
                return max;
            }
            return value;
        });
        this.min = min;
        this.max = max;
        this.slider = slider;
    }

    BoundedEntry(Field field, ConfigEntryContainer parentObject, TranslationIdentifier parentTranslation, T min, T max) {
        this(field, parentObject, parentTranslation, min, max, false);
    }

}
