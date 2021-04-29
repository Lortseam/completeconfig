package me.lortseam.completeconfig;

import lombok.Getter;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.data.EnumEntry;
import me.lortseam.completeconfig.data.entry.EntryOrigin;

public class DropdownEntry<T extends Enum<?>> extends EnumEntry<T> {

    @Getter
    private final boolean suggestionMode;

    public DropdownEntry(EntryOrigin origin) {
        super(origin);
        suggestionMode = origin.getAnnotation(ConfigEntry.Dropdown.class).suggestionMode();
    }

}
