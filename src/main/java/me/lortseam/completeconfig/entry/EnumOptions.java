package me.lortseam.completeconfig.entry;

import com.google.common.base.CaseFormat;
import lombok.Getter;
import me.lortseam.completeconfig.api.ConfigEntry;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.function.Function;

public class EnumOptions {

    @Getter
    private final DisplayType displayType;
    @Getter
    private final Function<Enum, Text> nameProvider;

    EnumOptions(Entry<?> entry, DisplayType displayType) {
        this.displayType = displayType;
        nameProvider = enumValue -> new TranslatableText(entry.getTranslationKey() + "." + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, enumValue.name()));
    }

    public enum DisplayType {

        BUTTON, DROPDOWN;

        private static final DisplayType defaultValue;

        static {
            try {
                defaultValue = (DisplayType) ConfigEntry.EnumGuiOptions.class.getDeclaredMethod("displayType").getDefaultValue();
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        public static DisplayType getDefault() {
            return defaultValue;
        }

    }

}
