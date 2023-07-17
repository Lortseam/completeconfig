package me.lortseam.completeconfig.gui.yacl;

import com.google.common.collect.Lists;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import me.lortseam.completeconfig.CompleteConfig;
import me.lortseam.completeconfig.data.*;
import me.lortseam.completeconfig.gui.ConfigScreenBuilder;
import me.lortseam.completeconfig.gui.GuiProvider;
import net.minecraft.client.gui.screen.Screen;

import java.awt.*;
import java.util.Collection;
import java.util.List;

/**
 * A screen builder based on the YetAnotherConfigLib library.
 */
public final class YaclScreenBuilder extends ConfigScreenBuilder<ControllerFunction<?>> {

    private static final List<GuiProvider<ControllerFunction<?>>> globalProviders = Lists.newArrayList(
            GuiProvider.create(BooleanEntry.class, entry -> (Option<Boolean> option) -> BooleanControllerBuilder.create(option)
                            .valueFormatter(entry.getValueFormatter())
                            .coloured(false),
                    (BooleanEntry entry) -> !entry.isCheckbox(), boolean.class, Boolean.class),
            GuiProvider.create(BooleanEntry.class, entry -> (Option<Boolean> option) -> TickBoxControllerBuilder.create(option),
                    BooleanEntry::isCheckbox, boolean.class, Boolean.class),
            GuiProvider.create((Entry<Integer> entry) -> (Option<Integer> option) -> IntegerFieldControllerBuilder.create(option)
                            .valueFormatter(entry.getValueFormatter()),
                    int.class, Integer.class),
            GuiProvider.create((Entry<Long> entry) -> (Option<Long> option) -> LongFieldControllerBuilder.create(option)
                            .valueFormatter(entry.getValueFormatter()),
                    long.class, Long.class),
            GuiProvider.create((Entry<Float> entry) -> (Option<Float> option) -> FloatFieldControllerBuilder.create(option)
                            .valueFormatter(entry.getValueFormatter()),
                    float.class, Float.class),
            GuiProvider.create((Entry<Double> entry) -> (Option<Double> option) -> DoubleFieldControllerBuilder.create(option)
                            .valueFormatter(entry.getValueFormatter()),
                    double.class, Double.class),
            GuiProvider.create(BoundedEntry.class, (BoundedEntry<Integer> entry) -> (Option<Integer> option) -> IntegerFieldControllerBuilder.create(option)
                            .min(entry.getMin())
                            .max(entry.getMax())
                            .valueFormatter(entry.getValueFormatter()),
                    int.class, Integer.class),
            GuiProvider.create(BoundedEntry.class, (BoundedEntry<Long> entry) -> (Option<Long> option) -> LongFieldControllerBuilder.create(option)
                            .min(entry.getMin())
                            .max(entry.getMax())
                            .valueFormatter(entry.getValueFormatter()),
                    long.class, Long.class),
            GuiProvider.create(BoundedEntry.class, (BoundedEntry<Float> entry) -> (Option<Float> option) -> FloatFieldControllerBuilder.create(option)
                            .min(entry.getMin())
                            .max(entry.getMax())
                            .valueFormatter(entry.getValueFormatter()),
                    float.class, Float.class),
            GuiProvider.create(BoundedEntry.class, (BoundedEntry<Double> entry) -> (Option<Double> option) -> DoubleFieldControllerBuilder.create(option)
                            .min(entry.getMin())
                            .max(entry.getMax())
                            .valueFormatter(entry.getValueFormatter()),
                    double.class, Double.class),
            GuiProvider.create(SliderEntry.class, (SliderEntry<Integer> entry) -> (Option<Integer> option) -> IntegerSliderControllerBuilder.create(option)
                            .range(entry.getMin(), entry.getMax())
                            .step(entry.getInterval().orElse(1)),
                    int.class, Integer.class),
            GuiProvider.create(SliderEntry.class, (SliderEntry<Long> entry) -> (Option<Long> option) -> LongSliderControllerBuilder.create(option)
                            .range(entry.getMin(), entry.getMax())
                            .step(entry.getInterval().orElse(1L)),
                    long.class, Long.class),
            GuiProvider.create(SliderEntry.class, (SliderEntry<Float> entry) -> (Option<Float> option) -> FloatSliderControllerBuilder.create(option)
                            .range(entry.getMin(), entry.getMax())
                            .step(entry.getInterval().orElse(0.1f)),
                    float.class, Float.class),
            GuiProvider.create(SliderEntry.class, (SliderEntry<Double> entry) -> (Option<Double> option) -> DoubleSliderControllerBuilder.create(option)
                            .range(entry.getMin(), entry.getMax())
                            .step(entry.getInterval().orElse(0.01)),
                    double.class, Double.class),
            GuiProvider.create(entry -> (Option<String> option) -> StringControllerBuilder.create(option),
                    String.class),
            GuiProvider.create(EnumEntry.class, (EnumEntry<?> entry) -> (Option<Enum<?>> option) -> EnumControllerBuilder.create((Option) option)
                            .enumClass(entry.getTypeClass())
                            .valueFormatter(entry.getValueFormatter())
                    ),
            GuiProvider.create(ColorEntry.class, (ColorEntry<Color> entry) -> (Option<Color> option) -> ColorControllerBuilder.create(option)
                            .allowAlpha(entry.isAlphaMode()),
                    Color.class)
    );

    static {
        for (Collection<GuiProvider<ControllerFunction<?>>> providers : CompleteConfig.collectExtensions(YaclGuiExtension.class, YaclGuiExtension::getProviders)) {
            globalProviders.addAll(providers);
        }
    }

    public YaclScreenBuilder() {
        super(globalProviders);
    }

    @Override
    public Screen build(Screen parentScreen, Config config) {
        var configBuilder = YetAnotherConfigLib.createBuilder()
                .title(getTitle(config))
                .save(config::save);
        if (!config.getEntries().isEmpty()) {
            // If there is only one cluster, use the config title for the cluster name
            var name = config.getClusters().isEmpty() ? getTitle(config) : config.getName();
            var categoryBuilder = ConfigCategory.createBuilder()
                    .name(name);
            for (Entry<?> entry : config.getEntries()) {
                categoryBuilder.option(buildOption(entry));
            }
            configBuilder.category(categoryBuilder.build());
        }
        for (var cluster : config.getClusters()) {
            var categoryBuilder = ConfigCategory.createBuilder()
                    .name(cluster.getName());
            cluster.getDescription().ifPresent(categoryBuilder::tooltip);
            for (Entry<?> entry : cluster.getEntries()) {
                categoryBuilder.option(buildOption(entry));
            }
            for (var subCluster : cluster.getClusters()) {
                var groupBuilder = OptionGroup.createBuilder()
                        .name(subCluster.getName());
                subCluster.getDescription().ifPresent(description -> groupBuilder.description(OptionDescription.of(description)));
                for (Entry<?> entry : subCluster.getEntries()) {
                    groupBuilder.option(buildOption(entry));
                }
                if (!subCluster.getClusters().isEmpty()) {
                    throw new UnsupportedOperationException("YACL screen builder doesn't support more than 2 levels of groups");
                }
                categoryBuilder.group(groupBuilder.build());
            }
            configBuilder.category(categoryBuilder.build());
        }
        return configBuilder.build().generateScreen(parentScreen);
    }

    private <T> Option<T> buildOption(Entry<T> entry) {
        var builder = Option.<T>createBuilder()
                .name(entry.getName())
                .binding(entry.getDefaultValue(), entry::getValue, entry::setValue)
                .controller(option -> ((ControllerFunction<T>) createEntry(entry)).apply(option));
        entry.getDescription().ifPresent(description -> builder.description(OptionDescription.of(description)));
        if (entry.requiresRestart()) {
            builder.flag(OptionFlag.GAME_RESTART);
        }
        return builder.build();
    }

}
