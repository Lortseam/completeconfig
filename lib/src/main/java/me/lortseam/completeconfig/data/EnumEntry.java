package me.lortseam.completeconfig.data;

import com.google.common.base.CaseFormat;
import lombok.Getter;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.data.entry.EntryOrigin;
import net.minecraft.text.Text;

import java.util.function.Function;

public class EnumEntry<T extends Enum> extends Entry<T> {

    @Getter
    private final DisplayType displayType;

    public EnumEntry(EntryOrigin origin, DisplayType displayType) {
        super(origin);
        this.displayType = displayType;
    }

    public EnumEntry(EntryOrigin origin) {
        this(origin, DisplayType.DEFAULT);
    }

    public Function<Enum, Text> getEnumNameProvider() {
        return enumValue -> getTranslation().append(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, enumValue.name())).toText();
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
