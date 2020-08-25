package me.lortseam.completeconfig.entry;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.lortseam.completeconfig.api.ConfigEntry;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class EnumOptions {

    @Getter
    private final DisplayType displayType;

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
