package me.lortseam.completeconfig.gui;

import lombok.NonNull;
import me.lortseam.completeconfig.data.Config;
import me.lortseam.completeconfig.data.Entry;
import me.lortseam.completeconfig.text.TranslationKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.*;

@Environment(EnvType.CLIENT)
public abstract class ConfigScreenBuilder<T> {

    private static final Map<String, ConfigScreenBuilder> builders = new HashMap<>();

    /**
     * Sets the main screen builder for a mod. The main screen builder will be used to build the config screen if no
     * custom builder was specified.
     *
     * @param modId the mod's ID
     * @param screenBuilder the screen builder
     */
    public static void setMain(@NonNull String modId, @NonNull ConfigScreenBuilder<?> screenBuilder) {
        builders.put(modId, screenBuilder);
    }

    public static Optional<ConfigScreenBuilder<?>> getMain(String modId) {
        return Optional.ofNullable(builders.get(modId));
    }

    private final List<GuiProvider<T>> providers = new ArrayList<>();

    protected ConfigScreenBuilder(List<GuiProvider<T>> globalProviders) {
        providers.addAll(globalProviders);
    }

    /**
     * Registers one or more custom GUI providers.
     *
     * @param providers the custom GUI providers
     */
    public final void register(GuiProvider<T>... providers) {
        Collections.addAll(this.providers, providers);
    }

    protected final Text getTitle(Config config) {
        TranslationKey customTitle = config.getTranslation(true).append("title");
        if (customTitle.exists()) {
            return customTitle.toText();
        }
        return new TranslatableText("completeconfig.gui.defaultTitle", config.getMod().getName());
    }

    /**
     * Builds a screen based on a config.
     *
     * @param parentScreen the parent screen
     * @param config the config to build the screen of
     * @return the built screen
     */
    public abstract Screen build(Screen parentScreen, Config config);

    protected T buildEntry(Entry<?> entry) {
        return providers.stream().filter(provider -> provider.test(entry)).findFirst().map(provider -> {
            return (EntryBuilder<Entry<?>, T>) provider.getBuilder();
        }).orElseThrow(() -> {
            return new UnsupportedOperationException("Could not generate GUI for entry " + entry);
        }).build(entry);
    }

}
