package me.lortseam.completeconfig.gui.cloth;

import com.google.common.collect.Lists;
import com.google.common.collect.MoreCollectors;
import com.google.common.reflect.TypeToken;
import lombok.NonNull;
import me.lortseam.completeconfig.CompleteConfig;
import me.lortseam.completeconfig.data.*;
import me.lortseam.completeconfig.gui.ConfigScreenBuilder;
import me.lortseam.completeconfig.gui.GuiProvider;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * A screen builder based on the Cloth Config API.
 */
public final class ClothConfigScreenBuilder extends ConfigScreenBuilder<AbstractConfigListEntry<?>> {

    private static final List<GuiProvider<AbstractConfigListEntry<?>>> globalProviders = Lists.newArrayList(
            GuiProvider.create(BooleanEntry.class, entry -> {
                        var builder = ConfigEntryBuilder.create()
                                .startBooleanToggle(entry.getName(), entry.getValue())
                                .setDefaultValue(entry.getDefaultValue())
                                .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                                .setSaveConsumer(entry::setValue);
                        entry.getValueFormatter().ifPresent(builder::setYesNoTextSupplier);
                        return builder.build();
                    },
                    entry -> !entry.isCheckbox(), boolean.class, Boolean.class),
            GuiProvider.create((Entry<Integer> entry) -> ConfigEntryBuilder.create()
                    .startIntField(entry.getName(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setSaveConsumer(entry::setValue)
                    .build(),
                    int.class, Integer.class),
            GuiProvider.create(BoundedEntry.class, (BoundedEntry<Integer> entry) -> ConfigEntryBuilder.create()
                    .startIntField(entry.getName(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setMin(entry.getMin())
                    .setMax(entry.getMax())
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setSaveConsumer(entry::setValue)
                    .build(),
                    int.class, Integer.class),
            GuiProvider.create(SliderEntry.class, (SliderEntry<Integer> entry) -> {
                        var builder = ConfigEntryBuilder.create()
                                .startIntSlider(entry.getName(), entry.getValue(), entry.getMin(), entry.getMax())
                                .setDefaultValue(entry.getDefaultValue())
                                .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                                .setSaveConsumer(entry::setValue);
                        entry.getValueFormatter().ifPresent(builder::setTextGetter);
                        return builder.build();
                    },
                    int.class, Integer.class),
            GuiProvider.create(ColorEntry.class, (ColorEntry<Integer> entry) -> ConfigEntryBuilder.create()
                    .startColorField(entry.getName(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setAlphaMode(entry.isAlphaMode())
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setSaveConsumer(entry::setValue)
                    .build(),
                    int.class, Integer.class),
            GuiProvider.create((Entry<Long> entry) -> ConfigEntryBuilder.create()
                    .startLongField(entry.getName(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setSaveConsumer(entry::setValue)
                    .build(),
                    long.class, Long.class),
            GuiProvider.create(BoundedEntry.class, (BoundedEntry<Long> entry) -> ConfigEntryBuilder.create()
                    .startLongField(entry.getName(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setMin(entry.getMin())
                    .setMax(entry.getMax())
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setSaveConsumer(entry::setValue)
                    .build(),
                    long.class, Long.class),
            GuiProvider.create(SliderEntry.class, (SliderEntry<Long> entry) -> {
                        var builder = ConfigEntryBuilder.create()
                                .startLongSlider(entry.getName(), entry.getValue(), entry.getMin(), entry.getMax())
                                .setDefaultValue(entry.getDefaultValue())
                                .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                                .setSaveConsumer(entry::setValue);
                        entry.getValueFormatter().ifPresent(builder::setTextGetter);
                        return builder.build();
                    },
                    long.class, Long.class),
            GuiProvider.create((Entry<Float> entry) -> ConfigEntryBuilder.create()
                    .startFloatField(entry.getName(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setSaveConsumer(entry::setValue)
                    .build(),
                    float.class, Float.class),
            GuiProvider.create(BoundedEntry.class, (BoundedEntry<Float> entry) -> ConfigEntryBuilder.create()
                    .startFloatField(entry.getName(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setMin(entry.getMin())
                    .setMax(entry.getMax())
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setSaveConsumer(entry::setValue)
                    .build(),
                    float.class, Float.class),
            GuiProvider.create((Entry<Double> entry) -> ConfigEntryBuilder.create()
                    .startDoubleField(entry.getName(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setSaveConsumer(entry::setValue)
                    .build(),
                    double.class, Double.class),
            GuiProvider.create(BoundedEntry.class, (BoundedEntry<Double> entry) -> ConfigEntryBuilder.create()
                    .startDoubleField(entry.getName(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setMin(entry.getMin())
                    .setMax(entry.getMax())
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setSaveConsumer(entry::setValue)
                    .build(),
                    double.class, Double.class),
            GuiProvider.create((Entry<String> entry) -> ConfigEntryBuilder.create()
                    .startStrField(entry.getName(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setSaveConsumer(entry::setValue)
                    .build(),
                    String.class),
            GuiProvider.create(EnumEntry.class, (EnumEntry<Enum<?>> entry) -> {
                var builder = ConfigEntryBuilder.create()
                        .startEnumSelector(entry.getName(), entry.getTypeClass(), entry.getValue())
                        .setDefaultValue(entry.getDefaultValue())
                        .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                        .setSaveConsumer(entry::setValue);
                entry.getValueFormatter().ifPresent(formatter -> {
                    builder.setEnumNameProvider(value -> formatter.apply(value));
                });
                return builder.build();
            }),
            GuiProvider.create(DropdownEntry.class, (DropdownEntry<Enum<?>> entry) -> {
                List<Enum<?>> enumValues = Arrays.asList(entry.getEnumConstants());
                var valueFormatter = entry.getValueFormatter().orElse(value -> Text.literal(value.toString()));
                return ConfigEntryBuilder.create()
                        .startDropdownMenu(entry.getName(), DropdownMenuBuilder.TopCellElementBuilder.of(
                                entry.getValue(),
                                enumTranslation -> enumValues.stream().filter(enumValue -> valueFormatter.apply(enumValue).getString().equals(enumTranslation)).collect(MoreCollectors.toOptional()).orElse(null),
                                valueFormatter
                        ), DropdownMenuBuilder.CellCreatorBuilder.of(valueFormatter))
                        .setSelections(enumValues)
                        .setSuggestionMode(entry.isSuggestionMode())
                        .setDefaultValue(entry.getDefaultValue())
                        .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                        .setSaveConsumer(entry::setValue)
                        .build();
            }),
            GuiProvider.create((Entry<List<Integer>> entry) -> ConfigEntryBuilder.create()
                    .startIntList(entry.getName(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setSaveConsumer(entry::setValue)
                    .build(),
                    new TypeToken<List<Integer>>() {}.getType()),
            GuiProvider.create((Entry<Integer[]> entry) -> ConfigEntryBuilder.create()
                    .startIntList(entry.getName(), Arrays.asList(entry.getValue()))
                    .setDefaultValue(Arrays.asList(entry.getDefaultValue()))
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setSaveConsumer(list -> entry.setValue(list.toArray(new Integer[0])))
                    .build(),
                    int[].class, Integer[].class),
            GuiProvider.create((Entry<List<Long>> entry) -> ConfigEntryBuilder.create()
                    .startLongList(entry.getName(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setSaveConsumer(entry::setValue)
                    .build(),
                    new TypeToken<List<Long>>() {}.getType()),
            GuiProvider.create((Entry<Long[]> entry) -> ConfigEntryBuilder.create()
                    .startLongList(entry.getName(), Arrays.asList(entry.getValue()))
                    .setDefaultValue(Arrays.asList(entry.getDefaultValue()))
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setSaveConsumer(list -> entry.setValue(list.toArray(new Long[0])))
                    .build(),
                    long[].class, Long[].class),
            GuiProvider.create((Entry<List<Float>> entry) -> ConfigEntryBuilder.create()
                    .startFloatList(entry.getName(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setSaveConsumer(entry::setValue)
                    .build(),
                    new TypeToken<List<Float>>() {}.getType()),
            GuiProvider.create((Entry<Float[]> entry) -> ConfigEntryBuilder.create()
                    .startFloatList(entry.getName(), Arrays.asList(entry.getValue()))
                    .setDefaultValue(Arrays.asList(entry.getDefaultValue()))
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setSaveConsumer(list -> entry.setValue(list.toArray(new Float[0])))
                    .build(),
                    float[].class, Float[].class),
            GuiProvider.create((Entry<List<Double>> entry) -> ConfigEntryBuilder.create()
                    .startDoubleList(entry.getName(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setSaveConsumer(entry::setValue)
                    .build(),
                    new TypeToken<List<Double>>() {}.getType()),
            GuiProvider.create((Entry<Double[]> entry) -> ConfigEntryBuilder.create()
                    .startDoubleList(entry.getName(), Arrays.asList(entry.getValue()))
                    .setDefaultValue(Arrays.asList(entry.getDefaultValue()))
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setSaveConsumer(list -> entry.setValue(list.toArray(new Double[0])))
                    .build(),
                    double[].class, Double[].class),
            GuiProvider.create((Entry<List<String>> entry) -> ConfigEntryBuilder.create()
                    .startStrList(entry.getName(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setSaveConsumer(entry::setValue)
                    .build(),
                    new TypeToken<List<String>>() {}.getType()),
            GuiProvider.create((Entry<String[]> entry) -> ConfigEntryBuilder.create()
                    .startStrList(entry.getName(), Arrays.asList(entry.getValue()))
                    .setDefaultValue(Arrays.asList(entry.getDefaultValue()))
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setSaveConsumer(list -> entry.setValue(list.toArray(new String[0])))
                    .build(),
                    String[].class),
            GuiProvider.create(ColorEntry.class, (ColorEntry<Color> entry) -> ConfigEntryBuilder.create()
                    .startColorField(entry.getName(), entry.getValue().getRGB())
                    .setDefaultValue(entry.getDefaultValue().getRGB())
                    .setAlphaMode(entry.isAlphaMode())
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setSaveConsumer(rgb -> entry.setValue(new Color(rgb)))
                    .build(),
                    Color.class)
    );

    static {
        for (Collection<GuiProvider<AbstractConfigListEntry<?>>> providers : CompleteConfig.collectExtensions(ClothConfigGuiExtension.class, ClothConfigGuiExtension::getProviders)) {
            globalProviders.addAll(providers);
        }
    }

    private final Supplier<ConfigBuilder> supplier;

    public ClothConfigScreenBuilder(@NonNull Supplier<ConfigBuilder> supplier) {
        super(globalProviders);
        this.supplier = supplier;
    }

    public ClothConfigScreenBuilder() {
        this(ConfigBuilder::create);
    }

    @Override
    public Screen build(Screen parentScreen, Config config) {
        ConfigBuilder builder = supplier.get()
                .setParentScreen(parentScreen)
                .setDefaultBackgroundTexture(background)
                .setSavingRunnable(config::save);
        builder.setTitle(getTitle(config));
        if (!config.getEntries().isEmpty()) {
            ConfigCategory category = builder.getOrCreateCategory(config.getName());
            for (Entry<?> entry : config.getEntries()) {
                category.addEntry(buildEntry(entry));
            }
        }
        for(Cluster cluster : config.getClusters()) {
            ConfigCategory category = builder.getOrCreateCategory(cluster.getName());
            category.setDescription(() -> cluster.getDescription().map(description -> new StringVisitable[]{description}));
            cluster.getBackground().ifPresent(category::setBackground);
            for (AbstractConfigListEntry<?> entry : buildCategoryList(cluster)) {
                category.addEntry(entry);
            }
        }
        return builder.build();
    }

    private List<AbstractConfigListEntry> buildCategoryList(Cluster cluster) {
        List<AbstractConfigListEntry> list = new ArrayList<>();
        for (Entry<?> entry : cluster.getEntries()) {
            list.add(buildEntry(entry));
        }
        for (Cluster subCluster : cluster.getClusters()) {
            SubCategoryBuilder builder = ConfigEntryBuilder.create()
                    .startSubCategory(subCluster.getName())
                    .setTooltip(subCluster.getDescription().map(description -> new Text[]{description}));
            builder.addAll(buildCategoryList(subCluster));
            list.add(builder.build());
        }
        return list;
    }

    private AbstractConfigListEntry<?> buildEntry(Entry<?> entry) {
        AbstractConfigListEntry<?> listEntry = createEntry(entry);
        listEntry.setRequiresRestart(entry.requiresRestart());
        return listEntry;
    }

}
