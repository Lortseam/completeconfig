package me.lortseam.completeconfig.data;

import com.google.common.base.CaseFormat;
import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigGroup;
import me.lortseam.completeconfig.data.structure.Identifiable;
import me.lortseam.completeconfig.data.structure.client.DescriptionSupplier;
import me.lortseam.completeconfig.text.TranslationBase;
import me.lortseam.completeconfig.text.TranslationKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.util.Optional;

public final class Cluster extends Parent implements Identifiable, DescriptionSupplier {

    private final Parent parent;
    private final ConfigGroup group;
    private final String comment;
    @Environment(EnvType.CLIENT)
    private TranslationKey translation;
    @Environment(EnvType.CLIENT)
    private TranslationKey descriptionTranslation;
    @Environment(EnvType.CLIENT)
    private Identifier background;

    Cluster(Parent parent, ConfigGroup group) {
        this.parent = parent;
        this.group = group;
        comment = group.getComment();
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
                translation = getRoot().getBaseTranslation().append(customKey);
            } else {
                translation = getBaseTranslation();
            }
        }
        return translation;
    }

    @Override
    public Optional<TranslationKey> getDescriptionTranslation() {
        if (descriptionTranslation == null) {
            String customKey = group.getDescriptionKey();
            if (customKey != null && !customKey.isBlank()) {
                descriptionTranslation = getRoot().getBaseTranslation().append(customKey);
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
        if (comment != null) {
            node.comment(comment);
        }
        super.fetch(node);
    }

    @Override
    public String getId() {
        return group.getId();
    }

    @Override
    public TranslationKey getBaseTranslation(TranslationBase translationBase, @Nullable Class<? extends ConfigContainer> clazz) {
        return switch (translationBase) {
            case INSTANCE -> parent.getBaseTranslation().append(group.getId());
            case CLASS -> {
                if (clazz == null || !clazz.isInstance(group)) {
                    clazz = group.getClass();
                }
                yield parent.getBaseTranslation(translationBase, null).append(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, clazz.getSimpleName()));
            }
        };
    }

}