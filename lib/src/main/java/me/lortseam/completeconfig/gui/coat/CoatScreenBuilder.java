package me.lortseam.completeconfig.gui.coat;

import com.google.common.collect.Lists;
import de.siphalor.coat.input.CheckBoxConfigInput;
import de.siphalor.coat.input.SliderConfigInput;
import de.siphalor.coat.input.TextConfigInput;
import de.siphalor.coat.list.complex.ConfigCategoryWidget;
import de.siphalor.coat.list.entry.ConfigCategoryConfigEntry;
import de.siphalor.coat.list.entry.ConfigContainerEntry;
import de.siphalor.coat.screen.ConfigScreen;
import me.lortseam.completeconfig.data.*;
import me.lortseam.completeconfig.gui.ConfigScreenBuilder;
import me.lortseam.completeconfig.gui.GuiProvider;
import me.lortseam.completeconfig.gui.coat.handler.BasicEntryHandler;
import me.lortseam.completeconfig.gui.coat.handler.BoundedEntryHandler;
import me.lortseam.completeconfig.gui.coat.input.ButtonConfigInput;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;

import java.util.ArrayList;
import java.util.List;

public final class CoatScreenBuilder extends ConfigScreenBuilder<ConfigCategoryConfigEntry<?>> {

    // TODO: Add missing providers
    private static final List<GuiProvider<ConfigCategoryConfigEntry<?>>> globalProviders = Lists.newArrayList(
            GuiProvider.create(BooleanEntry.class, entry -> new ConfigCategoryConfigEntry<>(
                    (BaseText) entry.getText(),
                    (BaseText) entry.getDescription().orElse(LiteralText.EMPTY),
                    new BasicEntryHandler<>(entry),
                    new CheckBoxConfigInput(null, entry.getValue(), false)
            ), boolean.class, Boolean.class),
            GuiProvider.create(SliderEntry.class, (SliderEntry<Integer> entry) -> new ConfigCategoryConfigEntry<>(
                    (BaseText) entry.getText(),
                    (BaseText) entry.getDescription().orElse(LiteralText.EMPTY),
                    new BoundedEntryHandler<>(entry),
                    new SliderConfigInput<>(entry.getValue(), entry.getMin(), entry.getMax())
            ), int.class, Integer.class),
            GuiProvider.create(SliderEntry.class, (SliderEntry<Long> entry) -> new ConfigCategoryConfigEntry<>(
                    (BaseText) entry.getText(),
                    (BaseText) entry.getDescription().orElse(LiteralText.EMPTY),
                    new BoundedEntryHandler<>(entry),
                    new SliderConfigInput<>(entry.getValue(), entry.getMin(), entry.getMax())
            ), long.class, Long.class),
            GuiProvider.create(SliderEntry.class, (SliderEntry<Float> entry) -> new ConfigCategoryConfigEntry<>(
                    (BaseText) entry.getText(),
                    (BaseText) entry.getDescription().orElse(LiteralText.EMPTY),
                    new BoundedEntryHandler<>(entry),
                    new SliderConfigInput<>(entry.getValue(), entry.getMin(), entry.getMax())
            ), float.class, Float.class),
            GuiProvider.create(SliderEntry.class, (SliderEntry<Double> entry) -> new ConfigCategoryConfigEntry<>(
                    (BaseText) entry.getText(),
                    (BaseText) entry.getDescription().orElse(LiteralText.EMPTY),
                    new BoundedEntryHandler<>(entry),
                    new SliderConfigInput<>(entry.getValue(), entry.getMin(), entry.getMax())
            ), double.class, Double.class),
            GuiProvider.create((Entry<String> entry) -> new ConfigCategoryConfigEntry<>(
                    (BaseText) entry.getText(),
                    (BaseText) entry.getDescription().orElse(LiteralText.EMPTY),
                    new BasicEntryHandler<>(entry),
                    new TextConfigInput(entry.getValue())
            ), String.class),
            GuiProvider.create(EnumEntry.class, (EnumEntry<Enum<?>> entry) -> new ConfigCategoryConfigEntry<>(
                    (BaseText) entry.getText(),
                    (BaseText) entry.getDescription().orElse(LiteralText.EMPTY),
                    new BasicEntryHandler<>(entry),
                    new ButtonConfigInput<>(entry.getEnumConstants(), entry.getValue(), entry.getValueTextSupplier())
            ))
    );

    public CoatScreenBuilder() {
        super(globalProviders);
    }

    @Override
    public Screen build(Screen parentScreen, Config config) {
        List<ConfigCategoryWidget> list = new ArrayList<>();
        if (!config.getEntries().isEmpty()) {
            List<ConfigContainerEntry> entries = new ArrayList<>();
            for (Entry<?> entry : config.getEntries()) {
                entries.add(buildEntry(entry));
            }
            list.add(new ConfigCategoryWidget(MinecraftClient.getInstance(), config.getText(), entries, DrawableHelper.OPTIONS_BACKGROUND_TEXTURE));
        }
        for (Cluster cluster : config.getClusters()) {
            list.add(buildListWidget(cluster));
        }
        return new ConfigScreen(parentScreen, getTitle(config), list);
    }

    private ConfigCategoryWidget buildListWidget(Cluster cluster) {
        List<ConfigContainerEntry> list = new ArrayList<>();
        for (Entry<?> entry : cluster.getEntries()) {
            list.add(buildEntry(entry));
        }
        ConfigCategoryWidget widget = new ConfigCategoryWidget(MinecraftClient.getInstance(), cluster.getText(), list, DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);
        for (Cluster subCluster : cluster.getClusters()) {
            widget.addSubTree(buildListWidget(subCluster));
        }
        return widget;
    }

}
