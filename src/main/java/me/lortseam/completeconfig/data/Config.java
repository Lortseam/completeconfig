package me.lortseam.completeconfig.data;

import me.lortseam.completeconfig.api.ConfigGroup;
import me.lortseam.completeconfig.data.gui.TranslationIdentifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class Config extends CollectionMap {

    private static final Logger LOGGER = LogManager.getLogger();

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
