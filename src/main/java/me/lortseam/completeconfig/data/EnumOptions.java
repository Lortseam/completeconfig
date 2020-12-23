package me.lortseam.completeconfig.data;

import com.google.common.base.CaseFormat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lortseam.completeconfig.api.ConfigEntry;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.function.Function;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class EnumOptions {

    private final Entry<?> parent;
    @Getter
    private final DisplayType displayType;

    public Function<Enum, Text> getNameProvider() {
        return enumValue -> new TranslatableText(parent.getTranslationKey() + "." + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, enumValue.name()));
    }

    public enum DisplayType {

        BUTTON, DROPDOWN;

        private static final DisplayType defaultValue;

        static {
            try {
                defaultValue = (DisplayType) ConfigEntry.EnumOptions.class.getDeclaredMethod("displayType").getDefaultValue();
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        public static DisplayType getDefault() {
            return defaultValue;
        }

    }

}
