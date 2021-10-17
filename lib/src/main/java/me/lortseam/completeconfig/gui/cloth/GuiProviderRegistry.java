package me.lortseam.completeconfig.gui.cloth;

import com.google.common.collect.Lists;
import com.google.common.collect.MoreCollectors;
import com.google.common.reflect.TypeToken;
import me.lortseam.completeconfig.CompleteConfig;
import me.lortseam.completeconfig.data.*;
import me.lortseam.completeconfig.extension.clothconfig.GuiExtension;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.*;
import java.util.stream.Stream;

/**
 * Stores global and screen builder specific GUI providers.
 */
@Environment(EnvType.CLIENT)
public final class GuiProviderRegistry {

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

    private final List<GuiProvider> providers = new ArrayList<>();

    /**
     * Registers one or more custom GUI providers.
     *
     * @param providers the custom GUI providers
     *
     * @see GuiExtension#getProviders()
     */
    public void add(GuiProvider... providers) {
        Collections.addAll(this.providers, providers);
    }

    Optional<EntryBuilder<Entry<?>>> findBuilder(Entry<?> entry) {
        return Stream.of(providers, globalProviders).flatMap(List::stream).filter(provider -> provider.test(entry)).findFirst().map(provider -> {
            return (EntryBuilder<Entry<?>>) provider.getBuilder();
        });
    }

}
