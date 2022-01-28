package me.lortseam.completeconfig.data;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.lortseam.completeconfig.api.ConfigGroup;
import me.lortseam.completeconfig.data.structure.Identifiable;
import me.lortseam.completeconfig.data.structure.client.TooltipSupplier;
import me.lortseam.completeconfig.text.TranslationKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.util.Arrays;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class Cluster extends Parent implements Identifiable, TooltipSupplier {

    private final Parent parent;
    private final ConfigGroup group;
    @Environment(EnvType.CLIENT)
    private TranslationKey translation;
    @Environment(EnvType.CLIENT)
    private TranslationKey[] tooltipTranslation;

    @Override
    public TranslationKey getTranslation() {
        if (translation == null) {
            translation = parent.getTranslation().append(group.getId());
        }
        return translation;
    }

    @Override
    public TranslationKey[] getTooltipTranslation() {
        if (tooltipTranslation == null) {
            if (ArrayUtils.isNotEmpty(group.getTooltipTranslationKeys())) {
                tooltipTranslation = Arrays.stream(group.getTooltipTranslationKeys()).map(key -> getTranslation().root().append(key)).toArray(TranslationKey[]::new);
            } else {
                tooltipTranslation = getTranslation().appendTooltip().orElse(new TranslationKey[0]);
            }
        }
        return tooltipTranslation;
    }

    @Override
    public void fetch(CommentedConfigurationNode node) {
        if (!StringUtils.isEmpty(group.getComment())) {
            node.comment(group.getComment());
        }
        super.fetch(node);
    }

    @Override
    public String getId() {
        return group.getId();
    }

}