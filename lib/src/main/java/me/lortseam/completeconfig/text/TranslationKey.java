package me.lortseam.completeconfig.text;

import lombok.EqualsAndHashCode;
import me.lortseam.completeconfig.data.Config;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@EqualsAndHashCode
public final class TranslationKey {

    private static final char DELIMITER = '.';

    public static TranslationKey from(Config config) {
        return new TranslationKey("config" + DELIMITER + config.getMod().getId(), null);
    }

    private final String modKey;
    private final String subKey;

    @Environment(EnvType.CLIENT)
    private TranslationKey(String modKey, String subKey) {
        this.modKey = modKey;
        this.subKey = subKey;
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
        return new TranslatableText(getKey(), args);
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

    public Optional<TranslationKey[]> appendTooltip() {
        TranslationKey baseTranslation = append("tooltip");
        if (baseTranslation.exists()) {
            return Optional.of(new TranslationKey[] {baseTranslation});
        } else {
            List<TranslationKey> multiLineTranslation = new ArrayList<>();
            for(int i = 0;; i++) {
                TranslationKey key = baseTranslation.append(Integer.toString(i));
                if(key.exists()) {
                    multiLineTranslation.add(key);
                } else {
                    if (!multiLineTranslation.isEmpty()) {
                        return Optional.of(multiLineTranslation.toArray(new TranslationKey[0]));
                    }
                    break;
                }
            }
        }
        return Optional.empty();
    }

}
