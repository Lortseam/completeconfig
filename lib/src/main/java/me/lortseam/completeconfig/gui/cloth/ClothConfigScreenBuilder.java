package me.lortseam.completeconfig.gui.cloth;

import com.google.common.collect.Lists;
import com.google.common.collect.MoreCollectors;
import com.google.common.reflect.TypeToken;
import lombok.NonNull;
import me.lortseam.completeconfig.CompleteConfig;
import me.lortseam.completeconfig.data.*;
import me.lortseam.completeconfig.gui.ConfigScreenBuilder;
import me.lortseam.completeconfig.text.TranslationKey;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.TranslatableText;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A screen builder based on the Cloth Config API.
 */
public final class ClothConfigScreenBuilder extends ConfigScreenBuilder {

    private static final List<GuiProvider> globalProviders = Lists.newArrayList(
            GuiProvider.create(BooleanEntry.class, entry -> ConfigEntryBuilder.create()
                    .startBooleanToggle(entry.getText(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setYesNoTextSupplier(entry.getValueTextSupplier())
                    .setTooltip(entry.getTooltip())
                    .setSaveConsumer(entry::setValue),
                    boolean.class, Boolean.class),
            GuiProvider.create((Entry<Integer> entry) -> ConfigEntryBuilder.create()
                    .startIntField(entry.getText(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setTooltip(entry.getTooltip())
                    .setSaveConsumer(entry::setValue),
                    int.class, Integer.class),
            GuiProvider.create(BoundedEntry.class, (BoundedEntry<Integer> entry) -> ConfigEntryBuilder.create()
                    .startIntField(entry.getText(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setMin(entry.getMin())
                    .setMax(entry.getMax())
                    .setTooltip(entry.getTooltip())
                    .setSaveConsumer(entry::setValue),
                    int.class, Integer.class),
            GuiProvider.create(SliderEntry.class, (SliderEntry<Integer> entry) -> ConfigEntryBuilder.create()
                    .startIntSlider(entry.getText(), entry.getValue(), entry.getMin(), entry.getMax())
                    .setDefaultValue(entry.getDefaultValue())
                    .setTextGetter(entry.getValueTextSupplier())
                    .setTooltip(entry.getTooltip())
                    .setSaveConsumer(entry::setValue),
                    int.class, Integer.class),
            GuiProvider.create(ColorEntry.class, (ColorEntry<Integer> entry) -> ConfigEntryBuilder.create()
                    .startColorField(entry.getText(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setAlphaMode(entry.isAlphaMode())
                    .setTooltip(entry.getTooltip())
                    .setSaveConsumer(entry::setValue),
                    int.class, Integer.class),
            GuiProvider.create((Entry<Long> entry) -> ConfigEntryBuilder.create()
                    .startLongField(entry.getText(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setTooltip(entry.getTooltip())
                    .setSaveConsumer(entry::setValue),
                    long.class, Long.class),
            GuiProvider.create(BoundedEntry.class, (BoundedEntry<Long> entry) -> ConfigEntryBuilder.create()
                    .startLongField(entry.getText(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setMin(entry.getMin())
                    .setMax(entry.getMax())
                    .setTooltip(entry.getTooltip())
                    .setSaveConsumer(entry::setValue),
                    long.class, Long.class),
            GuiProvider.create(SliderEntry.class, (SliderEntry<Long> entry) -> ConfigEntryBuilder.create()
                    .startLongSlider(entry.getText(), entry.getValue(), entry.getMin(), entry.getMax())
                    .setDefaultValue(entry.getDefaultValue())
                    .setTextGetter(entry.getValueTextSupplier())
                    .setTooltip(entry.getTooltip())
                    .setSaveConsumer(entry::setValue),
                    long.class, Long.class),
            GuiProvider.create((Entry<Float> entry) -> ConfigEntryBuilder.create()
                    .startFloatField(entry.getText(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setTooltip(entry.getTooltip())
                    .setSaveConsumer(entry::setValue),
                    float.class, Float.class),
            GuiProvider.create(BoundedEntry.class, (BoundedEntry<Float> entry) -> ConfigEntryBuilder.create()
                    .startFloatField(entry.getText(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setMin(entry.getMin())
                    .setMax(entry.getMax())
                    .setTooltip(entry.getTooltip())
                    .setSaveConsumer(entry::setValue),
                    float.class, Float.class),
            GuiProvider.create((Entry<Double> entry) -> ConfigEntryBuilder.create()
                    .startDoubleField(entry.getText(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setTooltip(entry.getTooltip())
                    .setSaveConsumer(entry::setValue),
                    double.class, Double.class),
            GuiProvider.create(BoundedEntry.class, (BoundedEntry<Double> entry) -> ConfigEntryBuilder.create()
                    .startDoubleField(entry.getText(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setMin(entry.getMin())
                    .setMax(entry.getMax())
                    .setTooltip(entry.getTooltip())
                    .setSaveConsumer(entry::setValue),
                    double.class, Double.class),
            GuiProvider.create((Entry<String> entry) -> ConfigEntryBuilder.create()
                    .startStrField(entry.getText(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setTooltip(entry.getTooltip())
                    .setSaveConsumer(entry::setValue),
                    String.class),
            GuiProvider.create(EnumEntry.class, (EnumEntry<Enum<?>> entry) -> ConfigEntryBuilder.create()
                    .startEnumSelector(entry.getText(), entry.getTypeClass(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setTooltip(entry.getTooltip())
                    .setEnumNameProvider(entry.getValueTextSupplier())
                    .setSaveConsumer(entry::setValue)),
            GuiProvider.create(DropdownEntry.class, (DropdownEntry<Enum<?>> entry) -> {
                List<Enum> enumValues = Arrays.asList(((Class<? extends Enum<?>>) entry.getTypeClass()).getEnumConstants());
                return ConfigEntryBuilder.create()
                        .startDropdownMenu(entry.getText(), DropdownMenuBuilder.TopCellElementBuilder.of(
                                entry.getValue(),
                                enumTranslation -> enumValues.stream().filter(enumValue -> entry.getValueTextSupplier().apply(enumValue).getString().equals(enumTranslation)).collect(MoreCollectors.toOptional()).orElse(null),
                                entry.getValueTextSupplier()
                        ), DropdownMenuBuilder.CellCreatorBuilder.of(entry.getValueTextSupplier()))
                        .setSelections(enumValues)
                        .setSuggestionMode(entry.isSuggestionMode())
                        .setDefaultValue(entry.getDefaultValue())
                        .setSaveConsumer(entry::setValue);
            }),
            GuiProvider.create((Entry<List<Integer>> entry) -> ConfigEntryBuilder.create()
                    .startIntList(entry.getText(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setTooltip(entry.getTooltip())
                    .setSaveConsumer(entry::setValue),
                    new TypeToken<List<Integer>>() {}.getType()),
            GuiProvider.create((Entry<Integer[]> entry) -> ConfigEntryBuilder.create()
                    .startIntList(entry.getText(), Arrays.asList(entry.getValue()))
                    .setDefaultValue(Arrays.asList(entry.getDefaultValue()))
                    .setTooltip(entry.getTooltip())
                    .setSaveConsumer(list -> entry.setValue(list.toArray(new Integer[0]))),
                    int[].class, Integer[].class),
            GuiProvider.create((Entry<List<Long>> entry) -> ConfigEntryBuilder.create()
                    .startLongList(entry.getText(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setTooltip(entry.getTooltip())
                    .setSaveConsumer(entry::setValue),
                    new TypeToken<List<Long>>() {}.getType()),
            GuiProvider.create((Entry<Long[]> entry) -> ConfigEntryBuilder.create()
                    .startLongList(entry.getText(), Arrays.asList(entry.getValue()))
                    .setDefaultValue(Arrays.asList(entry.getDefaultValue()))
                    .setTooltip(entry.getTooltip())
                    .setSaveConsumer(list -> entry.setValue(list.toArray(new Long[0]))),
                    long[].class, Long[].class),
            GuiProvider.create((Entry<List<Float>> entry) -> ConfigEntryBuilder.create()
                    .startFloatList(entry.getText(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setTooltip(entry.getTooltip())
                    .setSaveConsumer(entry::setValue),
                    new TypeToken<List<Float>>() {}.getType()),
            GuiProvider.create((Entry<Float[]> entry) -> ConfigEntryBuilder.create()
                    .startFloatList(entry.getText(), Arrays.asList(entry.getValue()))
                    .setDefaultValue(Arrays.asList(entry.getDefaultValue()))
                    .setTooltip(entry.getTooltip())
                    .setSaveConsumer(list -> entry.setValue(list.toArray(new Float[0]))),
                    float[].class, Float[].class),
            GuiProvider.create((Entry<List<Double>> entry) -> ConfigEntryBuilder.create()
                    .startDoubleList(entry.getText(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setTooltip(entry.getTooltip())
                    .setSaveConsumer(entry::setValue),
                    new TypeToken<List<Double>>() {}.getType()),
            GuiProvider.create((Entry<Double[]> entry) -> ConfigEntryBuilder.create()
                    .startDoubleList(entry.getText(), Arrays.asList(entry.getValue()))
                    .setDefaultValue(Arrays.asList(entry.getDefaultValue()))
                    .setTooltip(entry.getTooltip())
                    .setSaveConsumer(list -> entry.setValue(list.toArray(new Double[0]))),
                    double[].class, Double[].class),
            GuiProvider.create((Entry<List<String>> entry) -> ConfigEntryBuilder.create()
                    .startStrList(entry.getText(), entry.getValue())
                    .setDefaultValue(entry.getDefaultValue())
                    .setTooltip(entry.getTooltip())
                    .setSaveConsumer(entry::setValue),
                    new TypeToken<List<String>>() {}.getType()),
            GuiProvider.create((Entry<String[]> entry) -> ConfigEntryBuilder.create()
                    .startStrList(entry.getText(), Arrays.asList(entry.getValue()))
                    .setDefaultValue(Arrays.asList(entry.getDefaultValue()))
                    .setTooltip(entry.getTooltip())
                    .setSaveConsumer(list -> entry.setValue(list.toArray(new String[0]))),
                    String[].class)
    );

    static {
        for (GuiProvider[] providers : CompleteConfig.collectExtensions(GuiExtension.class, GuiExtension::getProviders)) {
            globalProviders.addAll(Arrays.asList(providers));
        }
    }

    private final Supplier<ConfigBuilder> supplier;
    private final GuiProviderRegistry registry = new GuiProviderRegistry(this);
    private final List<GuiProvider> providers = new ArrayList<>();

    public ClothConfigScreenBuilder(@NonNull Supplier<ConfigBuilder> supplier) {
        this.supplier = supplier;
    }

    public ClothConfigScreenBuilder() {
        this(ConfigBuilder::create);
    }

    /**
     * Registers one or more custom GUI providers.
     *
     * @param providers the custom GUI providers
     *
     * @see GuiExtension#getProviders()
     */
    public void register(GuiProvider... providers) {
        Collections.addAll(this.providers, providers);
    }

    @Override
    public Screen build(Screen parentScreen, Config config) {
        ConfigBuilder builder = supplier.get()
                .setParentScreen(parentScreen)
                .setSavingRunnable(config::save);
        TranslationKey customTitle = config.getTranslation(true).append("title");
        builder.setTitle(customTitle.exists() ? customTitle.toText() : new TranslatableText("completeconfig.gui.defaultTitle", config.getMod().getName()));
        if (!config.getEntries().isEmpty()) {
            ConfigCategory category = builder.getOrCreateCategory(config.getText());
            for (Entry<?> entry : config.getEntries()) {
                category.addEntry(buildEntry(entry));
            }
        }
        for(Cluster cluster : config.getClusters()) {
            ConfigCategory category = builder.getOrCreateCategory(cluster.getText());
            category.setDescription(() -> cluster.getTooltip().map(lines -> Arrays.stream(lines).map(line -> (StringVisitable) line).toArray(StringVisitable[]::new)));
            for (AbstractConfigListEntry<?> entry : buildCluster(cluster)) {
                category.addEntry(entry);
            }
        }
        return builder.build();
    }

    private AbstractConfigListEntry<?> buildEntry(Entry<?> entry) {
        return Stream.of(providers, globalProviders).flatMap(List::stream).filter(provider -> provider.test(entry)).findFirst().map(provider -> {
            return (EntryBuilder<Entry<?>>) provider.getBuilder();
        }).orElseThrow(() -> {
            return new UnsupportedOperationException("Could not generate GUI for entry " + entry);
        }).build(entry);
    }

    private List<AbstractConfigListEntry> buildCluster(Cluster cluster) {
        List<AbstractConfigListEntry> clusterGui = new ArrayList<>();
        for (Entry<?> entry : cluster.getEntries()) {
            clusterGui.add(buildEntry(entry));
        }
        for (Cluster subCluster : cluster.getClusters()) {
            SubCategoryBuilder subBuilder = ConfigEntryBuilder.create()
                    .startSubCategory(subCluster.getText())
                    .setTooltip(subCluster.getTooltip());
            subBuilder.addAll(buildCluster(subCluster));
            clusterGui.add(subBuilder.build());
        }
        return clusterGui;
    }

    /**
     * @deprecated GUI provider registration is now directly handled by the {@link ClothConfigScreenBuilder}.
     */
    @Deprecated
    public GuiProviderRegistry getRegistry() {
        return this.registry;
    }

}
