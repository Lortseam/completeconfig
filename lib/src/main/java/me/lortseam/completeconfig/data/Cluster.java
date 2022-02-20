package me.lortseam.completeconfig.data;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.lortseam.completeconfig.api.ConfigGroup;
import me.lortseam.completeconfig.data.structure.Identifiable;
import me.lortseam.completeconfig.data.structure.client.DescriptionSupplier;
import me.lortseam.completeconfig.text.TranslationKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.util.Optional;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class Cluster extends Parent implements Identifiable, DescriptionSupplier {

    private final Parent parent;
    private final ConfigGroup group;
    @Environment(EnvType.CLIENT)
    private TranslationKey translation;
    @Environment(EnvType.CLIENT)
    private TranslationKey descriptionTranslation;

    @Override
    Config getRoot() {
        return parent.getRoot();
    }

    @Override
    public TranslationKey getTranslation() {
        if (translation == null) {
            translation = parent.getTranslation().append(group.getId());
        }
        return translation;
    }

    @Override
    public Optional<TranslationKey> getDescriptionTranslation() {
        if (descriptionTranslation == null) {
            String customKey = group.getDescriptionKey();
            if (customKey != null) {
                descriptionTranslation = getTranslation().root().append(customKey);
            } else {
                descriptionTranslation = getTranslation().append("description");
            }
        }
        return descriptionTranslation.exists() ? Optional.of(descriptionTranslation) : Optional.empty();
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