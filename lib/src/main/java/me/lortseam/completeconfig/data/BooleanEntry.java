package me.lortseam.completeconfig.data;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.data.entry.AnnotatedEntryOrigin;
import me.lortseam.completeconfig.data.entry.EntryOrigin;
import me.lortseam.completeconfig.data.text.TranslationIdentifier;
import me.lortseam.completeconfig.exception.IllegalAnnotationParameterException;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;

public class BooleanEntry extends Entry<Boolean> {

    private final Function<Boolean, TranslationIdentifier> trueFalseTranslationSupplier;

    public BooleanEntry(EntryOrigin origin, Function<Boolean, TranslationIdentifier> trueFalseTranslationSupplier) {
        super(origin);
        this.trueFalseTranslationSupplier = trueFalseTranslationSupplier;
    }

    BooleanEntry(AnnotatedEntryOrigin<ConfigEntry.Boolean> origin) {
        super(origin);
        ConfigEntry.Boolean annotation = origin.getAnnotation();
        if (StringUtils.isBlank(annotation.trueTranslationKey()) || StringUtils.isBlank(annotation.falseTranslationKey())) {
            throw new IllegalAnnotationParameterException("Both true key and false key must be specified");
        }
        trueFalseTranslationSupplier = bool -> getTranslation().root().append(bool ? annotation.trueTranslationKey() : annotation.falseTranslationKey());
    }

    BooleanEntry(EntryOrigin origin) {
        this(origin, null);
    }

    public Function<Boolean, Text> getTrueFalseTextSupplier() {
        if (trueFalseTranslationSupplier != null) {
            return bool -> trueFalseTranslationSupplier.apply(bool).toText();
        }
        TranslationIdentifier defaultTrueTranslation = getTranslation().append("true");
        TranslationIdentifier defaultFalseTranslation = getTranslation().append("false");
        if (defaultTrueTranslation.exists() && defaultFalseTranslation.exists()) {
            return bool -> (bool ? defaultTrueTranslation : defaultFalseTranslation).toText();
        }
        return null;
    }

}
