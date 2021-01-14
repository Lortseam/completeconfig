package me.lortseam.completeconfig.data;

import com.google.common.base.CaseFormat;
import lombok.Getter;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigEntryContainer;
import me.lortseam.completeconfig.data.text.TranslationIdentifier;
import net.minecraft.text.Text;

import java.lang.reflect.Field;
import java.util.function.Function;

public class EnumEntry<T extends Enum> extends Entry<T> {

    @Getter
    private final DisplayType displayType;

    EnumEntry(Field field, ConfigEntryContainer parentObject, TranslationIdentifier parentTranslation, DisplayType displayType) {
        super(field, parentObject, parentTranslation);
        this.displayType = displayType;
    }

    EnumEntry(Field field, ConfigEntryContainer parentObject, TranslationIdentifier parentTranslation) {
        this(field, parentObject, parentTranslation, DisplayType.DEFAULT);
    }

    public Function<Enum, Text> getEnumNameProvider() {
        return enumValue -> getTranslation().append(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, enumValue.name())).translate();
    }

    public enum DisplayType {

        BUTTON, DROPDOWN;

        private static final DisplayType DEFAULT;

        static {
            try {
                DEFAULT = (DisplayType) ConfigEntry.Enum.class.getDeclaredMethod("displayType").getDefaultValue();
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

    }

}
