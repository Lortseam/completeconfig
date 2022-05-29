package me.lortseam.completeconfig.text;

import lombok.EqualsAndHashCode;
import me.lortseam.completeconfig.data.Config;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import org.apache.commons.lang3.ArrayUtils;

@EqualsAndHashCode
public final class TranslationKey {

    private final String[] elements;

    @Environment(EnvType.CLIENT)
    private TranslationKey(String... elements) {
        this.elements = elements;
    }

    public TranslationKey(Config config) {
        this("config", config.getMod().getId());
    }

    private String getKey() {
        return String.join(".", elements);
    }

    public boolean exists() {
        return I18n.hasTranslation(getKey());
    }

    public Text toText(Object... args) {
        return Text.translatable(getKey(), args);
    }

    public TranslationKey append(String... elements) {
        return new TranslationKey(ArrayUtils.addAll(this.elements, elements));
    }

    @Override
    public String toString() {
        return getKey();
    }

}
