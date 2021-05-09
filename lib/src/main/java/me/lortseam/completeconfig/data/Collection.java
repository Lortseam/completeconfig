package me.lortseam.completeconfig.data;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import me.lortseam.completeconfig.data.structure.Identifiable;
import me.lortseam.completeconfig.data.text.TranslationKey;
import net.minecraft.text.Text;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.util.Arrays;
import java.util.Optional;

@Log4j2(topic = "CompleteConfig")
public class Collection extends BaseCollection implements Identifiable {

    private final String id;
    private final TranslationKey[] customTooltipTranslation;
    @Getter
    private final String comment;

    Collection(String id, TranslationKey translation, String[] customTooltipTranslationKeys, String comment) {
        super(translation);
        this.id = id;
        customTooltipTranslation = ArrayUtils.isNotEmpty(customTooltipTranslationKeys) ? Arrays.stream(customTooltipTranslationKeys).map(key -> translation.root().append(key)).toArray(TranslationKey[]::new) : null;
        this.comment = !StringUtils.isBlank(comment) ? comment : null;
    }

    public Optional<Text[]> getTooltipTranslation() {
        return (customTooltipTranslation != null ? Optional.of(customTooltipTranslation) : translation.appendTooltip()).map(lines -> {
            return Arrays.stream(lines).map(TranslationKey::toText).toArray(Text[]::new);
        });
    }

    @Override
    public void fetch(CommentedConfigurationNode node) {
        if (comment != null) {
            node.comment(comment);
        }
        super.fetch(node);
    }

    @Override
    public String getId() {
        return id;
    }

}