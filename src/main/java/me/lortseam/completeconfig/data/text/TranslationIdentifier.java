package me.lortseam.completeconfig.data.text;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableText;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class TranslationIdentifier {

    private final String modKey;
    private final String[] keyParts;

    public TranslationIdentifier(String modID) {
        modKey = "config." + modID;
        keyParts = new String[0];
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
        return new TranslationIdentifier(modKey, new String[0]);
    }

    public boolean exists() {
        return I18n.hasTranslation(getKey());
    }

    public TranslatableText translate() {
        return new TranslatableText(getKey());
    }

    public TranslationIdentifier append(String... keyParts) {
        return new TranslationIdentifier(modKey, ArrayUtils.addAll(this.keyParts, keyParts));
    }

    public TranslationIdentifier appendKey(String key) {
        return new TranslationIdentifier(modKey, key.split(Pattern.quote(".")));
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
