package me.lortseam.completeconfig.gui.coat;

import com.google.common.collect.Lists;
import de.siphalor.coat.input.CheckBoxConfigInput;
import de.siphalor.coat.input.SliderConfigInput;
import de.siphalor.coat.input.TextConfigInput;
import de.siphalor.coat.list.complex.ConfigCategoryWidget;
import de.siphalor.coat.list.entry.ConfigCategoryConfigEntry;
import de.siphalor.coat.list.entry.ConfigContainerEntry;
import de.siphalor.coat.screen.ConfigScreen;
import me.lortseam.completeconfig.CompleteConfig;
import me.lortseam.completeconfig.data.*;
import me.lortseam.completeconfig.gui.ConfigScreenBuilder;
import me.lortseam.completeconfig.gui.GuiProvider;
import me.lortseam.completeconfig.gui.coat.handler.BasicEntryHandler;
import me.lortseam.completeconfig.gui.coat.handler.BoundedEntryHandler;
import me.lortseam.completeconfig.gui.coat.handler.EntryHandlerConverter;
import me.lortseam.completeconfig.gui.coat.input.ButtonConfigInput;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.BooleanUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class CoatScreenBuilder extends ConfigScreenBuilder<ConfigCategoryConfigEntry<?>> {

    private static final List<GuiProvider<ConfigCategoryConfigEntry<?>>> globalProviders = Lists.newArrayList(
            GuiProvider.create(BooleanEntry.class, entry -> new ConfigCategoryConfigEntry<>(
                    (MutableText) entry.getName(),
                    (MutableText) entry.getDescription().orElse(Text.empty()),
                    new BasicEntryHandler<>(entry),
                    new CheckBoxConfigInput(null, entry.getValue(), false)
            ), BooleanEntry::isCheckbox, boolean.class, Boolean.class),
            GuiProvider.create(BooleanEntry.class, entry -> new ConfigCategoryConfigEntry<>(
                    (MutableText) entry.getName(),
                    (MutableText) entry.getDescription().orElse(Text.empty()),
                    new BasicEntryHandler<>(entry),
                    new ButtonConfigInput<>(BooleanUtils.booleanValues(), entry.getValue(), entry.getValueTextSupplier())
            ), entry -> !entry.isCheckbox(), boolean.class, Boolean.class),
            GuiProvider.create((Entry<Integer> entry) -> new ConfigCategoryConfigEntry<>(
                    (MutableText) entry.getName(),
                    (MutableText) entry.getDescription().orElse(Text.empty()),
                    EntryHandlerConverter.numberToString(entry, Integer::parseInt),
                    new TextConfigInput(entry.getValue().toString())
            ), int.class, Integer.class),
            GuiProvider.create(BoundedEntry.class, (BoundedEntry<Integer> entry) -> new ConfigCategoryConfigEntry<>(
                    (MutableText) entry.getName(),
                    (MutableText) entry.getDescription().orElse(Text.empty()),
                    EntryHandlerConverter.numberToString(new BoundedEntryHandler<>(entry), Integer::parseInt),
                    new TextConfigInput(entry.getValue().toString())
            ), int.class, Integer.class),
            GuiProvider.create(SliderEntry.class, (SliderEntry<Integer> entry) -> new ConfigCategoryConfigEntry<>(
                    (MutableText) entry.getName(),
                    (MutableText) entry.getDescription().orElse(Text.empty()),
                    new BoundedEntryHandler<>(entry),
                    new SliderConfigInput<>(entry.getValue(), entry.getMin(), entry.getMax())
            ), int.class, Integer.class),
            GuiProvider.create((Entry<Long> entry) -> new ConfigCategoryConfigEntry<>(
                    (MutableText) entry.getName(),
                    (MutableText) entry.getDescription().orElse(Text.empty()),
                    EntryHandlerConverter.numberToString(entry, Long::parseLong),
                    new TextConfigInput(entry.getValue().toString())
            ), long.class, Long.class),
            GuiProvider.create(BoundedEntry.class, (BoundedEntry<Long> entry) -> new ConfigCategoryConfigEntry<>(
                    (MutableText) entry.getName(),
                    (MutableText) entry.getDescription().orElse(Text.empty()),
                    EntryHandlerConverter.numberToString(new BoundedEntryHandler<>(entry), Long::parseLong),
                    new TextConfigInput(entry.getValue().toString())
            ), long.class, Long.class),
            GuiProvider.create(SliderEntry.class, (SliderEntry<Long> entry) -> new ConfigCategoryConfigEntry<>(
                    (MutableText) entry.getName(),
                    (MutableText) entry.getDescription().orElse(Text.empty()),
                    new BoundedEntryHandler<>(entry),
                    new SliderConfigInput<>(entry.getValue(), entry.getMin(), entry.getMax())
            ), long.class, Long.class),
            GuiProvider.create((Entry<Float> entry) -> new ConfigCategoryConfigEntry<>(
                    (MutableText) entry.getName(),
                    (MutableText) entry.getDescription().orElse(Text.empty()),
                    EntryHandlerConverter.numberToString(entry, Float::parseFloat),
                    new TextConfigInput(entry.getValue().toString())
            ), float.class, Float.class),
            GuiProvider.create(BoundedEntry.class, (BoundedEntry<Float> entry) -> new ConfigCategoryConfigEntry<>(
                    (MutableText) entry.getName(),
                    (MutableText) entry.getDescription().orElse(Text.empty()),
                    EntryHandlerConverter.numberToString(new BoundedEntryHandler<>(entry), Float::parseFloat),
                    new TextConfigInput(entry.getValue().toString())
            ), float.class, Float.class),
            GuiProvider.create(SliderEntry.class, (SliderEntry<Float> entry) -> new ConfigCategoryConfigEntry<>(
                    (MutableText) entry.getName(),
                    (MutableText) entry.getDescription().orElse(Text.empty()),
                    new BoundedEntryHandler<>(entry),
                    new SliderConfigInput<>(entry.getValue(), entry.getMin(), entry.getMax())
            ), float.class, Float.class),
            GuiProvider.create((Entry<Double> entry) -> new ConfigCategoryConfigEntry<>(
                    (MutableText) entry.getName(),
                    (MutableText) entry.getDescription().orElse(Text.empty()),
                    EntryHandlerConverter.numberToString(entry, Double::parseDouble),
                    new TextConfigInput(entry.getValue().toString())
            ), double.class, Double.class),
            GuiProvider.create(BoundedEntry.class, (BoundedEntry<Double> entry) -> new ConfigCategoryConfigEntry<>(
                    (MutableText) entry.getName(),
                    (MutableText) entry.getDescription().orElse(Text.empty()),
                    EntryHandlerConverter.numberToString(new BoundedEntryHandler<>(entry), Double::parseDouble),
                    new TextConfigInput(entry.getValue().toString())
            ), double.class, Double.class),
            GuiProvider.create(SliderEntry.class, (SliderEntry<Double> entry) -> new ConfigCategoryConfigEntry<>(
                    (MutableText) entry.getName(),
                    (MutableText) entry.getDescription().orElse(Text.empty()),
                    new BoundedEntryHandler<>(entry),
                    new SliderConfigInput<>(entry.getValue(), entry.getMin(), entry.getMax())
            ), double.class, Double.class),
            GuiProvider.create((Entry<String> entry) -> new ConfigCategoryConfigEntry<>(
                    (MutableText) entry.getName(),
                    (MutableText) entry.getDescription().orElse(Text.empty()),
                    new BasicEntryHandler<>(entry),
                    new TextConfigInput(entry.getValue())
            ), String.class),
            GuiProvider.create(EnumEntry.class, (EnumEntry<Enum<?>> entry) -> new ConfigCategoryConfigEntry<>(
                    (MutableText) entry.getName(),
                    (MutableText) entry.getDescription().orElse(Text.empty()),
                    new BasicEntryHandler<>(entry),
                    new ButtonConfigInput<>(entry.getEnumConstants(), entry.getValue(), entry.getValueTextSupplier())
            ))
    );

    static {
        for (Collection<GuiProvider<ConfigCategoryConfigEntry<?>>> providers : CompleteConfig.collectExtensions(CoatGuiExtension.class, CoatGuiExtension::getProviders)) {
            globalProviders.addAll(providers);
        }
    }

    public CoatScreenBuilder() {
        super(globalProviders);
    }

    @Override
    public Screen build(Screen parentScreen, Config config) {
        List<ConfigCategoryWidget> list = new ArrayList<>();
        if (!config.getEntries().isEmpty()) {
            List<ConfigContainerEntry> entries = new ArrayList<>();
            for (Entry<?> entry : config.getEntries()) {
                entries.add(createEntry(entry));
            }
            list.add(new ConfigCategoryWidget(MinecraftClient.getInstance(), config.getName(), entries, background));
        }
        for (Cluster cluster : config.getClusters()) {
            list.add(buildListWidget(cluster));
        }
        return new ConfigScreen(parentScreen, getTitle(config), list);
    }

    private ConfigCategoryWidget buildListWidget(Cluster cluster) {
        List<ConfigContainerEntry> list = new ArrayList<>();
        for (Entry<?> entry : cluster.getEntries()) {
            list.add(createEntry(entry));
        }
        ConfigCategoryWidget widget = new ConfigCategoryWidget(MinecraftClient.getInstance(), cluster.getName(), list, cluster.getBackground().orElse(background));
        for (Cluster subCluster : cluster.getClusters()) {
            widget.addSubTree(buildListWidget(subCluster));
        }
        return widget;
    }

}
