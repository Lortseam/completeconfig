package me.lortseam.completeconfig.data.text;

import me.lortseam.completeconfig.io.ConfigSource;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public final class TranslationKey {

    private static final String DELIMITER = ".";

    public static TranslationKey from(ConfigSource source) {
        return new TranslationKey("config" + DELIMITER + source.getModId());
    }

    private final String modKey;
    private final String[] keyParts;

    @Environment(EnvType.CLIENT)
    private TranslationKey(String modKey, String... keyParts) {
        this.modKey = modKey;
        this.keyParts = keyParts;
    }

    private String getKey() {
        StringBuilder builder = new StringBuilder(modKey);
        for (String keyPart : keyParts) {
            builder.append(DELIMITER);
            builder.append(keyPart);
        }
        return builder.toString();
    }

    public TranslationKey root() {
        return new TranslationKey(modKey);
    }

    public boolean exists() {
        return I18n.hasTranslation(getKey());
    }

    public Text toText(Object... args) {
        return new TranslatableText(getKey(), args);
    }

    public TranslationKey append(String... subKeys) {
        return new TranslationKey(modKey, ArrayUtils.addAll(keyParts, Arrays.stream(subKeys).map(subKey -> {
            return subKey.split(Pattern.quote(DELIMITER));
        }).flatMap(Arrays::stream).toArray(String[]::new)));
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
