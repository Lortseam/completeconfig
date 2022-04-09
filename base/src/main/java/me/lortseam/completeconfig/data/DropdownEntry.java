package me.lortseam.completeconfig.data;

import lombok.Getter;
import me.lortseam.completeconfig.api.ConfigEntry;

public class DropdownEntry<T extends Enum<?>> extends EnumEntry<T> {

    @Getter
    private final boolean suggestionMode;

    public DropdownEntry(EntryOrigin origin) {
        super(origin);
        suggestionMode = origin.getAnnotation(ConfigEntry.Dropdown.class).suggestionMode();
    }

}
