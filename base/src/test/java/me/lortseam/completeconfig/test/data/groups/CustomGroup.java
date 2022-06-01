package me.lortseam.completeconfig.test.data.groups;

import lombok.RequiredArgsConstructor;
import me.lortseam.completeconfig.api.ConfigGroup;

@RequiredArgsConstructor
public class CustomGroup implements ConfigGroup {

    private final String id;
    private final String nameKey;
    private final String descriptionKey;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getNameKey() {
        return nameKey;
    }

    @Override
    public String getDescriptionKey() {
        return descriptionKey;
    }

}
