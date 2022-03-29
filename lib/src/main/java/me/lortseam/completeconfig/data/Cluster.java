package me.lortseam.completeconfig.data;

import me.lortseam.completeconfig.api.ConfigGroup;
import me.lortseam.completeconfig.data.structure.Identifiable;
import me.lortseam.completeconfig.data.structure.client.DescriptionSupplier;
import me.lortseam.completeconfig.text.TranslationKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.util.Optional;

public final class Cluster extends Parent implements Identifiable, DescriptionSupplier {

    private final Parent parent;
    private final ConfigGroup group;
    @Environment(EnvType.CLIENT)
    private TranslationKey translation;
    @Environment(EnvType.CLIENT)
    private TranslationKey descriptionTranslation;
    @Environment(EnvType.CLIENT)
    private Identifier background;

    Cluster(Parent parent, ConfigGroup group) {
        this.parent = parent;
        this.group = group;
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            background = group.getBackground();
        }
    }

    @Override
    Config getRoot() {
        return parent.getRoot();
    }

    @Override
    public TranslationKey getNameTranslation() {
        if (translation == null) {
            String customKey = group.getNameKey();
            if (customKey != null && !customKey.isBlank()) {
                translation = parent.getNameTranslation().root().append(customKey);
            } else {
                translation = parent.getNameTranslation().append(group.getId());
            }
        }
        return translation;
    }

    @Override
    public Optional<TranslationKey> getDescriptionTranslation() {
        if (descriptionTranslation == null) {
            String customKey = group.getDescriptionKey();
            if (customKey != null && !customKey.isBlank()) {
                descriptionTranslation = getNameTranslation().root().append(customKey);
            } else {
                descriptionTranslation = getNameTranslation().append("description");
            }
        }
        return descriptionTranslation.exists() ? Optional.of(descriptionTranslation) : Optional.empty();
    }

    @Environment(EnvType.CLIENT)
    public Optional<Identifier> getBackground() {
        return Optional.ofNullable(background);
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