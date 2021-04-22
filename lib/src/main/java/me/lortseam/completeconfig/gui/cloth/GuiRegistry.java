package me.lortseam.completeconfig.gui.cloth;

import com.google.common.collect.Lists;
import com.google.common.collect.MoreCollectors;
import com.google.common.reflect.TypeToken;
import me.lortseam.completeconfig.CompleteConfig;
import me.lortseam.completeconfig.data.*;
import me.lortseam.completeconfig.extensions.GuiExtension;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TextColor;

import java.util.*;
import java.util.Collection;
import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public final class GuiRegistry {

    private static final List<Provider> globalProviders = Lists.newArrayList(
            Provider.create(BooleanEntry.class, entry -> ConfigEntryBuilder.create()
                            .startBooleanToggle(entry.getText(), entry.getValue())
                            .setDefaultValue(entry.getDefaultValue())
                            .setYesNoTextSupplier(entry.getValueTextSupplier())
                            .setTooltip(entry.getTooltip())
                            .setSaveConsumer(entry::setValue),
                    boolean.class, Boolean.class),
            Provider.create((Entry<Integer> entry) -> ConfigEntryBuilder.create()
                            .startIntField(entry.getText(), entry.getValue())
                            .setDefaultValue(entry.getDefaultValue())
                            .setTooltip(entry.getTooltip())
                            .setSaveConsumer(entry::setValue),
                    int.class, Integer.class),
            Provider.create(BoundedEntry.class, (BoundedEntry<Integer> entry) -> ConfigEntryBuilder.create()
                            .startIntField(entry.getText(), entry.getValue())
                            .setDefaultValue(entry.getDefaultValue())
                            .setMin(entry.getMin())
                            .setMax(entry.getMax())
                            .setTooltip(entry.getTooltip())
                            .setSaveConsumer(entry::setValue),
                    int.class, Integer.class),
            Provider.create(SliderEntry.class, (SliderEntry<Integer> entry) -> ConfigEntryBuilder.create()
                            .startIntSlider(entry.getText(), entry.getValue(), entry.getMin(), entry.getMax())
                            .setDefaultValue(entry.getDefaultValue())
                            .setTextGetter(entry.getValueTextSupplier())
                            .setTooltip(entry.getTooltip())
                            .setSaveConsumer(entry::setValue),
                    int.class, Integer.class),
            Provider.create(ColorEntry.class, (ColorEntry<Integer> entry) -> ConfigEntryBuilder.create()
                            .startColorField(entry.getText(), entry.getValue())
                            .setDefaultValue(entry.getDefaultValue())
                            .setAlphaMode(entry.isAlphaMode())
                            .setTooltip(entry.getTooltip())
                            .setSaveConsumer(entry::setValue),
                    int.class, Integer.class),
            Provider.create((Entry<Long> entry) -> ConfigEntryBuilder.create()
                            .startLongField(entry.getText(), entry.getValue())
                            .setDefaultValue(entry.getDefaultValue())
                            .setTooltip(entry.getTooltip())
                            .setSaveConsumer(entry::setValue),
                    long.class, Long.class),
            Provider.create(BoundedEntry.class, (BoundedEntry<Long> entry) -> ConfigEntryBuilder.create()
                            .startLongField(entry.getText(), entry.getValue())
                            .setDefaultValue(entry.getDefaultValue())
                            .setMin(entry.getMin())
                            .setMax(entry.getMax())
                            .setTooltip(entry.getTooltip())
                            .setSaveConsumer(entry::setValue),
                    long.class, Long.class),
            Provider.create(SliderEntry.class, (SliderEntry<Long> entry) -> ConfigEntryBuilder.create()
                            .startLongSlider(entry.getText(), entry.getValue(), entry.getMin(), entry.getMax())
                            .setDefaultValue(entry.getDefaultValue())
                            .setTextGetter(entry.getValueTextSupplier())
                            .setTooltip(entry.getTooltip())
                            .setSaveConsumer(entry::setValue),
                    long.class, Long.class),
            Provider.create((Entry<Float> entry) -> ConfigEntryBuilder.create()
                            .startFloatField(entry.getText(), entry.getValue())
                            .setDefaultValue(entry.getDefaultValue())
                            .setTooltip(entry.getTooltip())
                            .setSaveConsumer(entry::setValue),
                    float.class, Float.class),
            Provider.create(BoundedEntry.class, (BoundedEntry<Float> entry) -> ConfigEntryBuilder.create()
                            .startFloatField(entry.getText(), entry.getValue())
                            .setDefaultValue(entry.getDefaultValue())
                            .setMin(entry.getMin())
                            .setMax(entry.getMax())
                            .setTooltip(entry.getTooltip())
                            .setSaveConsumer(entry::setValue),
                    float.class, Float.class),
            Provider.create((Entry<Double> entry) -> ConfigEntryBuilder.create()
                            .startDoubleField(entry.getText(), entry.getValue())
                            .setDefaultValue(entry.getDefaultValue())
                            .setTooltip(entry.getTooltip())
                            .setSaveConsumer(entry::setValue),
                    double.class, Double.class),
            Provider.create(BoundedEntry.class, (BoundedEntry<Double> entry) -> ConfigEntryBuilder.create()
                            .startDoubleField(entry.getText(), entry.getValue())
                            .setDefaultValue(entry.getDefaultValue())
                            .setMin(entry.getMin())
                            .setMax(entry.getMax())
                            .setTooltip(entry.getTooltip())
                            .setSaveConsumer(entry::setValue),
                    double.class, Double.class),
            Provider.create((Entry<String> entry) -> ConfigEntryBuilder.create()
                            .startStrField(entry.getText(), entry.getValue())
                            .setDefaultValue(entry.getDefaultValue())
                            .setTooltip(entry.getTooltip())
                            .setSaveConsumer(entry::setValue),
                    String.class),
            Provider.create(EnumEntry.class, (EnumEntry<Enum<?>> entry) -> ConfigEntryBuilder.create()
                            .startEnumSelector(entry.getText(), entry.getTypeClass(), entry.getValue())
                            .setDefaultValue(entry.getDefaultValue())
                            .setTooltip(entry.getTooltip())
                            .setEnumNameProvider(entry.getEnumNameProvider())
                            .setSaveConsumer(entry::setValue),
                    entry -> entry.getDisplayType() == EnumEntry.DisplayType.BUTTON),
            Provider.create(EnumEntry.class, (EnumEntry<Enum<?>> entry) -> {
                List<Enum> enumValues = Arrays.asList(((Class<? extends Enum<?>>) entry.getTypeClass()).getEnumConstants());
                return ConfigEntryBuilder.create()
                        .startDropdownMenu(entry.getText(), DropdownMenuBuilder.TopCellElementBuilder.of(
                                entry.getValue(),
                                enumTranslation -> enumValues.stream().filter(enumValue -> entry.getEnumNameProvider().apply(enumValue).getString().equals(enumTranslation)).collect(MoreCollectors.toOptional()).orElse(null),
                                entry.getEnumNameProvider()
                        ), DropdownMenuBuilder.CellCreatorBuilder.of(entry.getEnumNameProvider()))
                        .setSelections(enumValues)
                        .setDefaultValue(entry.getDefaultValue())
                        .setSaveConsumer(entry::setValue);
            }, entry -> entry.getDisplayType() == EnumEntry.DisplayType.DROPDOWN),
            Provider.create((Entry<List<Integer>> entry) -> ConfigEntryBuilder.create()
                            .startIntList(entry.getText(), entry.getValue())
                            .setDefaultValue(entry.getDefaultValue())
                            .setTooltip(entry.getTooltip())
                            .setSaveConsumer(entry::setValue),
                    new TypeToken<List<Integer>>() {}.getType()),
            Provider.create((Entry<List<Long>> entry) -> ConfigEntryBuilder.create()
                            .startLongList(entry.getText(), entry.getValue())
                            .setDefaultValue(entry.getDefaultValue())
                            .setTooltip(entry.getTooltip())
                            .setSaveConsumer(entry::setValue),
                    new TypeToken<List<Long>>() {}.getType()),
            Provider.create((Entry<List<Float>> entry) -> ConfigEntryBuilder.create()
                            .startFloatList(entry.getText(), entry.getValue())
                            .setDefaultValue(entry.getDefaultValue())
                            .setTooltip(entry.getTooltip())
                            .setSaveConsumer(entry::setValue),
                    new TypeToken<List<Float>>() {}.getType()),
            Provider.create((Entry<List<Double>> entry) -> ConfigEntryBuilder.create()
                            .startDoubleList(entry.getText(), entry.getValue())
                            .setDefaultValue(entry.getDefaultValue())
                            .setTooltip(entry.getTooltip())
                            .setSaveConsumer(entry::setValue),
                    new TypeToken<List<Double>>() {}.getType()),
            Provider.create((Entry<List<String>> entry) -> ConfigEntryBuilder.create()
                            .startStrList(entry.getText(), entry.getValue())
                            .setDefaultValue(entry.getDefaultValue())
                            .setTooltip(entry.getTooltip())
                            .setSaveConsumer(entry::setValue),
                    new TypeToken<List<String>>() {}.getType()),
            Provider.create(ColorEntry.class, (ColorEntry<TextColor> entry) -> ConfigEntryBuilder.create()
                        .startColorField(entry.getText(), entry.getValue())
                        .setDefaultValue(entry.getDefaultValue())
                        .setTooltip(entry.getTooltip())
                        .setSaveConsumer3(entry::setValue),
                    entry -> !entry.isAlphaMode(), TextColor.class)
    );

    static {
        for (Collection<Provider> providers : CompleteConfig.collectExtensions(GuiExtension.class, GuiExtension::getProviders)) {
            globalProviders.addAll(providers);
        }
    }

    private final List<Provider> providers = new ArrayList<>();

    public void add(Provider... providers) {
        Collections.addAll(this.providers, providers);
    }

    Optional<EntryBuilder<Entry<?>>> findBuilder(Entry<?> entry) {
        return Stream.of(providers, globalProviders).flatMap(List::stream).filter(provider -> provider.test(entry)).findFirst().map(provider -> {
            return (EntryBuilder<Entry<?>>) provider.getBuilder();
        });
    }

}
