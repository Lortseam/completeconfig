package me.lortseam.completeconfig.data;

import me.lortseam.completeconfig.io.ConfigSource;
import me.lortseam.completeconfig.api.ConfigGroup;
import me.lortseam.completeconfig.data.gui.TranslationIdentifier;

import java.util.List;

public class Config extends CollectionMap {

    private final ConfigSource source;

    public Config(ConfigSource source, List<ConfigGroup> topLevelGroups) {
        super(new TranslationIdentifier(source.getModID()));
        this.source = source;
        for (ConfigGroup group : topLevelGroups) {
            resolve(group);
        }
    }

    public String getModID() {
        return source.getModID();
    }

    public TranslationIdentifier getTranslation() {
        return translation;
    }

    public void load() {
        source.load(this);
    }

    public void save() {
        source.save(this);
    }

}
