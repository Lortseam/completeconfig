package me.lortseam.completeconfig.data;

import me.lortseam.completeconfig.api.ConfigGroup;
import me.lortseam.completeconfig.data.gui.TranslationIdentifier;

import java.util.List;

public class Config extends CollectionMap {

    public Config(String modID, List<ConfigGroup> topLevelGroups) {
        super(new TranslationIdentifier(modID));
        for (ConfigGroup group : topLevelGroups) {
            resolve(group);
        }
    }

    public TranslationIdentifier getTranslation() {
        return translation;
    }

}
