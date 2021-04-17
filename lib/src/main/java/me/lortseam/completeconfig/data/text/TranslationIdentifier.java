package me.lortseam.completeconfig.data.text;

import me.lortseam.completeconfig.io.ConfigSource;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public final class TranslationIdentifier {

    public static TranslationIdentifier from(ConfigSource source) {
        return new TranslationIdentifier("config." + source.getModID());
    }

    private final String modKey;
    private final String[] keyParts;

    private TranslationIdentifier(String modKey, String... keyParts) {
        this.modKey = modKey;
        this.keyParts = keyParts;
    }

    private String getKey() {
        StringBuilder builder = new StringBuilder(modKey);
        for (String keyPart : keyParts) {
            builder.append(".");
            builder.append(keyPart);
        }
        return builder.toString();
    }

    public TranslationIdentifier root() {
        return new TranslationIdentifier(modKey);
    }

    public boolean exists() {
        return I18n.hasTranslation(getKey());
    }

    public Text toText(Object... args) {
        return new TranslatableText(getKey(), args);
    }

    public TranslationIdentifier append(String... subKeys) {
        return new TranslationIdentifier(modKey, ArrayUtils.addAll(keyParts, Arrays.stream(subKeys).map(subKey -> {
            return subKey.split(Pattern.quote("."));
        }).flatMap(Arrays::stream).toArray(String[]::new)));
    }

    public Optional<TranslationIdentifier[]> appendTooltip() {
        TranslationIdentifier baseTranslation = append("tooltip");
        if (baseTranslation.exists()) {
            return Optional.of(new TranslationIdentifier[] {baseTranslation});
        } else {
            List<TranslationIdentifier> multiLineTranslation = new ArrayList<>();
            for(int i = 0;; i++) {
                TranslationIdentifier key = baseTranslation.append(Integer.toString(i));
                if(key.exists()) {
                    multiLineTranslation.add(key);
                } else {
                    if (!multiLineTranslation.isEmpty()) {
                        return Optional.of(multiLineTranslation.toArray(new TranslationIdentifier[0]));
                    }
                    break;
                }
            }
        }
        return Optional.empty();
    }

}
