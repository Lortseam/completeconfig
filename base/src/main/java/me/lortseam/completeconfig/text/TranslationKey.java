package me.lortseam.completeconfig.text;

import lombok.EqualsAndHashCode;
import me.lortseam.completeconfig.data.Config;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

@EqualsAndHashCode
public final class TranslationKey {

    private static final char DELIMITER = '.';

    private final String modKey;
    private final String subKey;

    @Environment(EnvType.CLIENT)
    private TranslationKey(String modKey, String subKey) {
        this.modKey = modKey;
        this.subKey = subKey;
    }

    public TranslationKey(Config config) {
        this("config" + DELIMITER + config.getMod().getId(), null);
    }

    private String getKey() {
        if (subKey == null) {
            return modKey;
        } else {
            return modKey + subKey;
        }
    }

    public TranslationKey root() {
        return new TranslationKey(modKey, null);
    }

    public boolean exists() {
        return I18n.hasTranslation(getKey());
    }

    public Text toText(Object... args) {
        return Text.translatable(getKey(), args);
    }

    public TranslationKey append(String... subKeys) {
        StringBuilder subKeyBuilder = new StringBuilder();
        if (subKey != null) {
            subKeyBuilder.append(subKey);
        }
        for (String subKey : subKeys) {
            subKeyBuilder.append(DELIMITER).append(subKey);
        }
        return new TranslationKey(modKey, subKeyBuilder.toString());
    }

    @Override
    public String toString() {
        return getKey();
    }

}
