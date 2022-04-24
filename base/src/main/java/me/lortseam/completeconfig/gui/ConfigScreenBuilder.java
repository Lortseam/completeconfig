package me.lortseam.completeconfig.gui;

import lombok.NonNull;
import me.lortseam.completeconfig.data.Config;
import me.lortseam.completeconfig.data.Entry;
import me.lortseam.completeconfig.text.TranslationKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public abstract class ConfigScreenBuilder<T> {

    private static final Map<String, Supplier<ConfigScreenBuilder<?>>> suppliers = new HashMap<>();

    /**
     * Sets the main screen builder for a mod using a supplier. The main screen builder will be used to build the config
     * screen if no custom builder was specified.
     *
     * @param modId the mod's ID
     * @param screenBuilderSupplier the screen builder supplier
     */
    public static void setMain(@NonNull String modId, @NonNull Supplier<ConfigScreenBuilder<?>> screenBuilderSupplier) {
        suppliers.put(modId, screenBuilderSupplier);
    }

    /**
     * Sets the main screen builder for a mod. The main screen builder will be used to build the config screen if no
     * custom builder was specified.
     *
     * @param modId the mod's ID
     * @param screenBuilder the screen builder
     */
    public static void setMain(@NonNull String modId, @NonNull ConfigScreenBuilder<?> screenBuilder) {
        setMain(modId, () -> screenBuilder);
    }

    public static Optional<Supplier<ConfigScreenBuilder<?>>> getMain(String modId) {
        return Optional.ofNullable(suppliers.get(modId));
    }

    private final List<GuiProvider<T>> providers = new ArrayList<>();
    protected Identifier background = DrawableHelper.OPTIONS_BACKGROUND_TEXTURE;

    protected ConfigScreenBuilder(List<GuiProvider<T>> globalProviders) {
        providers.addAll(globalProviders);
    }

    /**
     * Registers a local GUI provider.
     *
     * @param provider a GUI provider
     */
    public final ConfigScreenBuilder<T> registerProvider(GuiProvider<T> provider) {
        providers.add(provider);
        return this;
    }

    /**
     * Registers local GUI providers.
     *
     * @param providers a collection of GUI providers
     */
    public final ConfigScreenBuilder<T> registerProviders(Collection<GuiProvider<T>> providers) {
        this.providers.addAll(providers);
        return this;
    }

    protected final Text getTitle(Config config) {
        TranslationKey customTitle = config.getTranslation(true).append("title");
        if (customTitle.exists()) {
            return customTitle.toText();
        }
        return Text.translatable("completeconfig.gui.defaultTitle", config.getMod().getName());
    }

    /**
     * Sets the background of the config screen.
     *
     * @param background the background identifier
     * @return this screen builder
     */
    public final ConfigScreenBuilder<T> setBackground(Identifier background) {
        this.background = background;
        return this;
    }

    /**
     * Builds a screen based on a config.
     *
     * @param parentScreen the parent screen
     * @param config the config to build the screen of
     * @return the built screen
     */
    public abstract Screen build(Screen parentScreen, Config config);

    protected T createEntry(Entry<?> entry) {
        return providers.stream().filter(provider -> provider.test(entry)).findFirst().map(provider -> {
            return (EntryBuilder<Entry<?>, T>) provider.getBuilder();
        }).orElseThrow(() -> {
            return new UnsupportedOperationException("Could not generate GUI for entry " + entry);
        }).build(entry);
    }

}
