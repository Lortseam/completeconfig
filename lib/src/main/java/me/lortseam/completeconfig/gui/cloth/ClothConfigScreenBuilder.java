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
import me.shedaniel.clothconfig2.impl.builders.FieldBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * A screen builder based on the Cloth Config API.
 */
public final class ClothConfigScreenBuilder extends ConfigScreenBuilder<FieldBuilder<?, ?>> {

    private static final List<GuiProvider<FieldBuilder<?, ?>>> globalProviders = Lists.newArrayList(
            GuiProvider.create(BooleanEntry.class, entry -> ConfigEntryBuilder.create()
                    .startBooleanToggle(entry.getText(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setYesNoTextSupplier(entry.getValueTextSupplier())
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setSaveConsumer(entry::setValue),
                    entry -> !entry.isCheckbox(), boolean.class, Boolean.class),
            GuiProvider.create((Entry<Integer> entry) -> ConfigEntryBuilder.create()
                    .startIntField(entry.getText(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setSaveConsumer(entry::setValue),
                    int.class, Integer.class),
            GuiProvider.create(BoundedEntry.class, (BoundedEntry<Integer> entry) -> ConfigEntryBuilder.create()
                    .startIntField(entry.getText(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setMin(entry.getMin())
                    .setMax(entry.getMax())
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setSaveConsumer(entry::setValue),
                    int.class, Integer.class),
            GuiProvider.create(SliderEntry.class, (SliderEntry<Integer> entry) -> ConfigEntryBuilder.create()
                    .startIntSlider(entry.getText(), entry.getValue(), entry.getMin(), entry.getMax())
                    .setDefaultValue(entry.getDefaultValue())
                    .setTextGetter(entry.getValueTextSupplier())
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setSaveConsumer(entry::setValue),
                    int.class, Integer.class),
            GuiProvider.create(ColorEntry.class, (ColorEntry<Integer> entry) -> ConfigEntryBuilder.create()
                    .startColorField(entry.getText(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setAlphaMode(entry.isAlphaMode())
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setSaveConsumer(entry::setValue),
                    int.class, Integer.class),
            GuiProvider.create((Entry<Long> entry) -> ConfigEntryBuilder.create()
                    .startLongField(entry.getText(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setSaveConsumer(entry::setValue),
                    long.class, Long.class),
            GuiProvider.create(BoundedEntry.class, (BoundedEntry<Long> entry) -> ConfigEntryBuilder.create()
                    .startLongField(entry.getText(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setMin(entry.getMin())
                    .setMax(entry.getMax())
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setSaveConsumer(entry::setValue),
                    long.class, Long.class),
            GuiProvider.create(SliderEntry.class, (SliderEntry<Long> entry) -> ConfigEntryBuilder.create()
                    .startLongSlider(entry.getText(), entry.getValue(), entry.getMin(), entry.getMax())
                    .setDefaultValue(entry.getDefaultValue())
                    .setTextGetter(entry.getValueTextSupplier())
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setSaveConsumer(entry::setValue),
                    long.class, Long.class),
            GuiProvider.create((Entry<Float> entry) -> ConfigEntryBuilder.create()
                    .startFloatField(entry.getText(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setSaveConsumer(entry::setValue),
                    float.class, Float.class),
            GuiProvider.create(BoundedEntry.class, (BoundedEntry<Float> entry) -> ConfigEntryBuilder.create()
                    .startFloatField(entry.getText(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setMin(entry.getMin())
                    .setMax(entry.getMax())
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setSaveConsumer(entry::setValue),
                    float.class, Float.class),
            GuiProvider.create((Entry<Double> entry) -> ConfigEntryBuilder.create()
                    .startDoubleField(entry.getText(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setSaveConsumer(entry::setValue),
                    double.class, Double.class),
            GuiProvider.create(BoundedEntry.class, (BoundedEntry<Double> entry) -> ConfigEntryBuilder.create()
                    .startDoubleField(entry.getText(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setMin(entry.getMin())
                    .setMax(entry.getMax())
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setSaveConsumer(entry::setValue),
                    double.class, Double.class),
            GuiProvider.create((Entry<String> entry) -> ConfigEntryBuilder.create()
                    .startStrField(entry.getText(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setSaveConsumer(entry::setValue),
                    String.class),
            GuiProvider.create(EnumEntry.class, (EnumEntry<Enum<?>> entry) -> ConfigEntryBuilder.create()
                    .startEnumSelector(entry.getText(), entry.getTypeClass(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setEnumNameProvider(value -> entry.getValueTextSupplier().apply(value))
                    .setSaveConsumer(entry::setValue)),
            GuiProvider.create(DropdownEntry.class, (DropdownEntry<Enum<?>> entry) -> {
                List<Enum<?>> enumValues = Arrays.asList(entry.getEnumConstants());
                return ConfigEntryBuilder.create()
                        .startDropdownMenu(entry.getText(), DropdownMenuBuilder.TopCellElementBuilder.of(
                                entry.getValue(),
                                enumTranslation -> enumValues.stream().filter(enumValue -> entry.getValueTextSupplier().apply(enumValue).getString().equals(enumTranslation)).collect(MoreCollectors.toOptional()).orElse(null),
                                entry.getValueTextSupplier()
                        ), DropdownMenuBuilder.CellCreatorBuilder.of(entry.getValueTextSupplier()))
                        .setSelections(enumValues)
                        .setSuggestionMode(entry.isSuggestionMode())
                        .setDefaultValue(entry.getDefaultValue())
                        .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                        .setSaveConsumer(entry::setValue);
            }),
            GuiProvider.create((Entry<List<Integer>> entry) -> ConfigEntryBuilder.create()
                    .startIntList(entry.getText(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setSaveConsumer(entry::setValue),
                    new TypeToken<List<Integer>>() {}.getType()),
            GuiProvider.create((Entry<Integer[]> entry) -> ConfigEntryBuilder.create()
                    .startIntList(entry.getText(), Arrays.asList(entry.getValue()))
                    .setDefaultValue(Arrays.asList(entry.getDefaultValue()))
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setSaveConsumer(list -> entry.setValue(list.toArray(new Integer[0]))),
                    int[].class, Integer[].class),
            GuiProvider.create((Entry<List<Long>> entry) -> ConfigEntryBuilder.create()
                    .startLongList(entry.getText(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setSaveConsumer(entry::setValue),
                    new TypeToken<List<Long>>() {}.getType()),
            GuiProvider.create((Entry<Long[]> entry) -> ConfigEntryBuilder.create()
                    .startLongList(entry.getText(), Arrays.asList(entry.getValue()))
                    .setDefaultValue(Arrays.asList(entry.getDefaultValue()))
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setSaveConsumer(list -> entry.setValue(list.toArray(new Long[0]))),
                    long[].class, Long[].class),
            GuiProvider.create((Entry<List<Float>> entry) -> ConfigEntryBuilder.create()
                    .startFloatList(entry.getText(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setSaveConsumer(entry::setValue),
                    new TypeToken<List<Float>>() {}.getType()),
            GuiProvider.create((Entry<Float[]> entry) -> ConfigEntryBuilder.create()
                    .startFloatList(entry.getText(), Arrays.asList(entry.getValue()))
                    .setDefaultValue(Arrays.asList(entry.getDefaultValue()))
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setSaveConsumer(list -> entry.setValue(list.toArray(new Float[0]))),
                    float[].class, Float[].class),
            GuiProvider.create((Entry<List<Double>> entry) -> ConfigEntryBuilder.create()
                    .startDoubleList(entry.getText(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setSaveConsumer(entry::setValue),
                    new TypeToken<List<Double>>() {}.getType()),
            GuiProvider.create((Entry<Double[]> entry) -> ConfigEntryBuilder.create()
                    .startDoubleList(entry.getText(), Arrays.asList(entry.getValue()))
                    .setDefaultValue(Arrays.asList(entry.getDefaultValue()))
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setSaveConsumer(list -> entry.setValue(list.toArray(new Double[0]))),
                    double[].class, Double[].class),
            GuiProvider.create((Entry<List<String>> entry) -> ConfigEntryBuilder.create()
                    .startStrList(entry.getText(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setSaveConsumer(entry::setValue),
                    new TypeToken<List<String>>() {}.getType()),
            GuiProvider.create((Entry<String[]> entry) -> ConfigEntryBuilder.create()
                    .startStrList(entry.getText(), Arrays.asList(entry.getValue()))
                    .setDefaultValue(Arrays.asList(entry.getDefaultValue()))
                    .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                    .setSaveConsumer(list -> entry.setValue(list.toArray(new String[0]))),
                    String[].class)
    );

    static {
        for (List<GuiProvider<FieldBuilder<?, ?>>> providers : CompleteConfig.collectExtensions(ClothConfigGuiExtension.class, ClothConfigGuiExtension::getProviders)) {
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
                .setSavingRunnable(config::save);
        builder.setTitle(getTitle(config));
        if (!config.getEntries().isEmpty()) {
            ConfigCategory category = builder.getOrCreateCategory(config.getText());
            for (Entry<?> entry : config.getEntries()) {
                category.addEntry(buildEntry(entry));
            }
        }
        for(Cluster cluster : config.getClusters()) {
            ConfigCategory category = builder.getOrCreateCategory(cluster.getText());
            category.setDescription(() -> cluster.getDescription().map(description -> new StringVisitable[]{description}));
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
                    .startSubCategory(subCluster.getText())
                    .setTooltip(subCluster.getDescription().map(description -> new Text[]{description}));
            builder.addAll(buildCategoryList(subCluster));
            list.add(builder.build());
        }
        return list;
    }

    private AbstractConfigListEntry<?> buildEntry(Entry<?> entry) {
        FieldBuilder<?, ?> builder = createEntry(entry);
        builder.requireRestart(entry.requiresRestart());
        return  builder.build();
    }

}
