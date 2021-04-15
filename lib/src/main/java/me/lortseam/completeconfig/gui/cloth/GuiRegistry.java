package me.lortseam.completeconfig.gui.cloth;

import com.google.common.collect.Lists;
import com.google.common.collect.MoreCollectors;
import com.google.common.reflect.TypeToken;
import me.lortseam.completeconfig.data.*;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import me.shedaniel.clothconfig2.impl.builders.FieldBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TextColor;

import java.util.*;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public final class GuiRegistry {

    private static final List<GuiProviderRegistration> globalRegistrations = Lists.newArrayList(
            new GuiProviderRegistration<>(BooleanEntry.class, entry -> build(
                    builder -> builder
                            .startBooleanToggle(entry.getText(), entry.getValue())
                            .setDefaultValue(entry.getDefaultValue())
                            .setYesNoTextSupplier(entry.getValueTextSupplier())
                            .setTooltip(entry.getTooltip())
                            .setSaveConsumer(entry::setValue),
                    entry.requiresRestart()
            ), boolean.class, Boolean.class),
            new GuiProviderRegistration<>((Entry<Integer> entry) -> build(
                    builder -> builder
                            .startIntField(entry.getText(), entry.getValue())
                            .setDefaultValue(entry.getDefaultValue())
                            .setTooltip(entry.getTooltip())
                            .setSaveConsumer(entry::setValue),
                    entry.requiresRestart()
            ), int.class, Integer.class),
            new GuiProviderRegistration<>(BoundedEntry.class, (BoundedEntry<Integer> entry) -> build(
                    builder -> builder
                            .startIntField(entry.getText(), entry.getValue())
                            .setDefaultValue(entry.getDefaultValue())
                            .setMin(entry.getMin())
                            .setMax(entry.getMax())
                            .setTooltip(entry.getTooltip())
                            .setSaveConsumer(entry::setValue),
                    entry.requiresRestart()
            ), int.class, Integer.class),
            new GuiProviderRegistration<>(SliderEntry.class, (SliderEntry<Integer> entry) -> build(
                    builder -> builder
                            .startIntSlider(entry.getText(), entry.getValue(), entry.getMin(), entry.getMax())
                            .setDefaultValue(entry.getDefaultValue())
                            .setTextGetter(entry.getValueTextSupplier())
                            .setTooltip(entry.getTooltip())
                            .setSaveConsumer(entry::setValue),
                    entry.requiresRestart()
            ), int.class, Integer.class),
            new GuiProviderRegistration<>(ColorEntry.class, (ColorEntry<Integer> entry) -> build(
                    builder -> builder
                            .startColorField(entry.getText(), entry.getValue())
                            .setDefaultValue(entry.getDefaultValue())
                            .setAlphaMode(entry.isAlphaMode())
                            .setTooltip(entry.getTooltip())
                            .setSaveConsumer(entry::setValue),
                    entry.requiresRestart()
            ), int.class, Integer.class),
            new GuiProviderRegistration<>((Entry<Long> entry) -> build(
                    builder -> builder
                            .startLongField(entry.getText(), entry.getValue())
                            .setDefaultValue(entry.getDefaultValue())
                            .setTooltip(entry.getTooltip())
                            .setSaveConsumer(entry::setValue),
                    entry.requiresRestart()
            ), long.class, Long.class),
            new GuiProviderRegistration<>(BoundedEntry.class, (BoundedEntry<Long> entry) -> build(
                    builder -> builder
                            .startLongField(entry.getText(), entry.getValue())
                            .setDefaultValue(entry.getDefaultValue())
                            .setMin(entry.getMin())
                            .setMax(entry.getMax())
                            .setTooltip(entry.getTooltip())
                            .setSaveConsumer(entry::setValue),
                    entry.requiresRestart()
            ), long.class, Long.class),
            new GuiProviderRegistration<>(SliderEntry.class, (SliderEntry<Long> entry) -> build(
                    builder -> builder
                            .startLongSlider(entry.getText(), entry.getValue(), entry.getMin(), entry.getMax())
                            .setDefaultValue(entry.getDefaultValue())
                            .setTextGetter(entry.getValueTextSupplier())
                            .setTooltip(entry.getTooltip())
                            .setSaveConsumer(entry::setValue),
                    entry.requiresRestart()
            ), long.class, Long.class),
            new GuiProviderRegistration<>((Entry<Float> entry) -> build(
                    builder -> builder
                            .startFloatField(entry.getText(), entry.getValue())
                            .setDefaultValue(entry.getDefaultValue())
                            .setTooltip(entry.getTooltip())
                            .setSaveConsumer(entry::setValue),
                    entry.requiresRestart()
            ), float.class, Float.class),
            new GuiProviderRegistration<>(BoundedEntry.class, (BoundedEntry<Float> entry) -> build(
                    builder -> builder
                            .startFloatField(entry.getText(), entry.getValue())
                            .setDefaultValue(entry.getDefaultValue())
                            .setMin(entry.getMin())
                            .setMax(entry.getMax())
                            .setTooltip(entry.getTooltip())
                            .setSaveConsumer(entry::setValue),
                    entry.requiresRestart()
            ), float.class, Float.class),
            new GuiProviderRegistration<>((Entry<Double> entry) -> build(
                    builder -> builder
                            .startDoubleField(entry.getText(), entry.getValue())
                            .setDefaultValue(entry.getDefaultValue())
                            .setTooltip(entry.getTooltip())
                            .setSaveConsumer(entry::setValue),
                    entry.requiresRestart()
            ), double.class, Double.class),
            new GuiProviderRegistration<>(BoundedEntry.class, (BoundedEntry<Double> entry) -> build(
                    builder -> builder
                            .startDoubleField(entry.getText(), entry.getValue())
                            .setDefaultValue(entry.getDefaultValue())
                            .setMin(entry.getMin())
                            .setMax(entry.getMax())
                            .setTooltip(entry.getTooltip())
                            .setSaveConsumer(entry::setValue),
                    entry.requiresRestart()
            ), double.class, Double.class),
            new GuiProviderRegistration<>((Entry<String> entry) -> build(
                    builder -> builder
                            .startStrField(entry.getText(), entry.getValue())
                            .setDefaultValue(entry.getDefaultValue())
                            .setTooltip(entry.getTooltip())
                            .setSaveConsumer(entry::setValue),
                    entry.requiresRestart()
            ), String.class),
            new GuiProviderRegistration<>(EnumEntry.class, (EnumEntry<Enum<?>> entry) -> build(
                    builder -> builder
                            .startEnumSelector(entry.getText(), entry.getTypeClass(), entry.getValue())
                            .setDefaultValue(entry.getDefaultValue())
                            .setTooltip(entry.getTooltip())
                            .setEnumNameProvider(entry.getEnumNameProvider())
                            .setSaveConsumer(entry::setValue),
                    entry.requiresRestart()
            ), entry -> entry.getDisplayType() == EnumEntry.DisplayType.BUTTON),
            new GuiProviderRegistration<>(EnumEntry.class, (EnumEntry<Enum<?>> entry) -> {
                List<Enum> enumValues = Arrays.asList(((Class<? extends Enum<?>>) entry.getTypeClass()).getEnumConstants());
                return build(
                        builder -> builder
                                .startDropdownMenu(entry.getText(), DropdownMenuBuilder.TopCellElementBuilder.of(
                                        entry.getValue(),
                                        enumTranslation -> enumValues.stream().filter(enumValue -> entry.getEnumNameProvider().apply(enumValue).getString().equals(enumTranslation)).collect(MoreCollectors.toOptional()).orElse(null),
                                        entry.getEnumNameProvider()
                                ), DropdownMenuBuilder.CellCreatorBuilder.of(entry.getEnumNameProvider()))
                                .setSelections(enumValues)
                                .setDefaultValue(entry.getDefaultValue())
                                .setSaveConsumer(entry::setValue),
                        entry.requiresRestart()
                );
            }, entry -> entry.getDisplayType() == EnumEntry.DisplayType.DROPDOWN),
            new GuiProviderRegistration<>((Entry<List<Integer>> entry) -> build(
                    builder -> builder
                            .startIntList(entry.getText(), entry.getValue())
                            .setDefaultValue(entry.getDefaultValue())
                            .setTooltip(entry.getTooltip())
                            .setSaveConsumer(entry::setValue),
                    entry.requiresRestart()
            ), new TypeToken<List<Integer>>() {}.getType()),
            new GuiProviderRegistration<>((Entry<List<Long>> entry) -> build(
                    builder -> builder
                            .startLongList(entry.getText(), entry.getValue())
                            .setDefaultValue(entry.getDefaultValue())
                            .setTooltip(entry.getTooltip())
                            .setSaveConsumer(entry::setValue),
                    entry.requiresRestart()
            ), new TypeToken<List<Long>>() {}.getType()),
            new GuiProviderRegistration<>((Entry<List<Float>> entry) -> build(
                    builder -> builder
                            .startFloatList(entry.getText(), entry.getValue())
                            .setDefaultValue(entry.getDefaultValue())
                            .setTooltip(entry.getTooltip())
                            .setSaveConsumer(entry::setValue),
                    entry.requiresRestart()
            ), new TypeToken<List<Float>>() {}.getType()),
            new GuiProviderRegistration<>((Entry<List<Double>> entry) -> build(
                    builder -> builder
                            .startDoubleList(entry.getText(), entry.getValue())
                            .setDefaultValue(entry.getDefaultValue())
                            .setTooltip(entry.getTooltip())
                            .setSaveConsumer(entry::setValue),
                    entry.requiresRestart()
            ), new TypeToken<List<Double>>() {}.getType()),
            new GuiProviderRegistration<>((Entry<List<String>> entry) -> build(
                    builder -> builder
                            .startStrList(entry.getText(), entry.getValue())
                            .setDefaultValue(entry.getDefaultValue())
                            .setTooltip(entry.getTooltip())
                            .setSaveConsumer(entry::setValue),
                    entry.requiresRestart()
            ), new TypeToken<List<String>>() {}.getType()),
            new GuiProviderRegistration<>(ColorEntry.class, (ColorEntry<TextColor> entry) -> build(
                    builder -> builder
                        .startColorField(entry.getText(), entry.getValue())
                        .setDefaultValue(entry.getDefaultValue())
                        .setTooltip(entry.getTooltip())
                        .setSaveConsumer3(entry::setValue),
                    entry.requiresRestart()
            ), entry -> !entry.isAlphaMode(), TextColor.class)
    );

    public static void addGlobal(GuiProviderRegistration... registrations) {
        Collections.addAll(globalRegistrations, registrations);
    }

    public static AbstractConfigListEntry<?> build(Function<ConfigEntryBuilder, FieldBuilder<?, ?>> builder, boolean requiresRestart) {
        FieldBuilder<?, ?> fieldBuilder = builder.apply(ConfigEntryBuilder.create());
        fieldBuilder.requireRestart(requiresRestart);
        return fieldBuilder.build();
    }

    private final List<GuiProviderRegistration> registrations = new ArrayList<>(globalRegistrations);

    public void add(GuiProviderRegistration... registrations) {
        Collections.addAll(this.registrations, registrations);
    }

    Optional<GuiProvider<Entry<?>>> getProvider(Entry<?> entry) {
        return registrations.stream().filter(registration -> registration.test(entry)).findFirst().map(registration -> {
            return (GuiProvider<Entry<?>>) registration.getProvider();
        });
    }

}
