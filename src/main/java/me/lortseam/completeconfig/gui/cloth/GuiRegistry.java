package me.lortseam.completeconfig.gui.cloth;

import com.google.common.collect.Lists;
import com.google.common.collect.MoreCollectors;
import com.google.common.reflect.TypeToken;
import me.lortseam.completeconfig.data.Entry;
import me.lortseam.completeconfig.data.EnumOptions;
import me.lortseam.completeconfig.util.TypeUtils;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import me.shedaniel.clothconfig2.impl.builders.FieldBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class GuiRegistry {

    public static <T, A extends AbstractConfigListEntry> A build(Function<ConfigEntryBuilder, FieldBuilder<T, A>> builder, boolean requiresRestart) {
        FieldBuilder<T, A> fieldBuilder = builder.apply(ConfigEntryBuilder.create());
        fieldBuilder.requireRestart(requiresRestart);
        return fieldBuilder.build();
    }

    private final List<GuiProviderRegistration> registrations = new ArrayList<>();

    GuiRegistry() {
        registerDefaultProviders();
    }

    public <T> void registerProvider(GuiProvider<T> provider, GuiProviderPredicate<T> predicate, Type... types) {
        registrations.add(new GuiProviderRegistration<>(predicate.and((field, extras) -> {
            if (types.length == 0) {
                return true;
            }
            return ArrayUtils.contains(types, TypeUtils.getFieldType(field));
        }), provider));
    }

    public void registerProvider(GuiProvider<?> provider, Type... types) {
        if (types.length == 0) {
            throw new IllegalArgumentException("Types must not be empty");
        }
        registerProvider(provider, (field, extras) -> true, types);
    }

    private void registerBoundedProvider(GuiProvider<?> provider, boolean slider, Type... types) {
        registerProvider(provider, (field, extras) -> extras.getBounds() != null && extras.getBounds().isSlider() == slider, types);
    }

    private void registerEnumProvider(GuiProvider<? extends Enum> provider, EnumOptions.DisplayType enumDisplayType) {
        registerProvider(provider, (field, extras) -> Enum.class.isAssignableFrom(field.getType()) && extras.getEnumOptions().getDisplayType() == enumDisplayType);
    }

    private void registerDefaultProviders() {
       registerProvider((GuiProvider<Boolean>) entry -> build(
               builder -> builder
                       .startBooleanToggle(entry.getText(), entry.getValue())
                       .setDefaultValue(entry.getDefaultValue())
                       .setTooltip(entry.getTooltip())
                       .setSaveConsumer(entry::setValue),
               entry.requiresRestart()
       ), boolean.class, Boolean.class);
       registerProvider((GuiProvider<Integer>) entry -> build(
               builder -> builder
                       .startIntField(entry.getText(), entry.getValue())
                       .setDefaultValue(entry.getDefaultValue())
                       .setTooltip(entry.getTooltip())
                       .setSaveConsumer(entry::setValue),
               entry.requiresRestart()
       ), int.class, Integer.class);
       registerBoundedProvider((GuiProvider<Integer>) entry -> build(
               builder -> builder
                       .startIntField(entry.getText(), entry.getValue())
                       .setDefaultValue(entry.getDefaultValue())
                       .setMin(entry.getExtras().getBounds().getMin())
                       .setMax(entry.getExtras().getBounds().getMax())
                       .setTooltip(entry.getTooltip())
                       .setSaveConsumer(entry::setValue),
               entry.requiresRestart()
       ), false, int.class, Integer.class);
       registerBoundedProvider((GuiProvider<Integer>) entry -> build(
               builder -> builder
                       .startIntSlider(entry.getText(), entry.getValue(), entry.getExtras().getBounds().getMin(), entry.getExtras().getBounds().getMax())
                       .setDefaultValue(entry.getDefaultValue())
                       .setTooltip(entry.getTooltip())
                       .setSaveConsumer(entry::setValue),
               entry.requiresRestart()
       ), true, int.class, Integer.class);
       registerProvider((GuiProvider<Long>) entry -> build(
               builder -> builder
                       .startLongField(entry.getText(), entry.getValue())
                       .setDefaultValue(entry.getDefaultValue())
                       .setTooltip(entry.getTooltip())
                       .setSaveConsumer(entry::setValue),
               entry.requiresRestart()
       ), long.class, Long.class);
       registerBoundedProvider((GuiProvider<Long>) entry -> build(
               builder -> builder
                       .startLongField(entry.getText(), entry.getValue())
                       .setDefaultValue(entry.getDefaultValue())
                       .setMin(entry.getExtras().getBounds().getMin())
                       .setMax(entry.getExtras().getBounds().getMax())
                       .setTooltip(entry.getTooltip())
                       .setSaveConsumer(entry::setValue),
               entry.requiresRestart()
       ), false, long.class, Long.class);
       registerBoundedProvider((GuiProvider<Long>) entry -> build(
               builder -> builder
                       .startLongSlider(entry.getText(), entry.getValue(), entry.getExtras().getBounds().getMin(), entry.getExtras().getBounds().getMax())
                       .setDefaultValue(entry.getDefaultValue())
                       .setTooltip(entry.getTooltip())
                       .setSaveConsumer(entry::setValue),
               entry.requiresRestart()
       ), true, long.class, Long.class);
       registerProvider((GuiProvider<Float>) entry -> build(
               builder -> builder
                       .startFloatField(entry.getText(), entry.getValue())
                       .setDefaultValue(entry.getDefaultValue())
                       .setTooltip(entry.getTooltip())
                       .setSaveConsumer(entry::setValue),
               entry.requiresRestart()
       ), float.class, Float.class);
       registerBoundedProvider((GuiProvider<Float>) entry -> build(
               builder -> builder
                       .startFloatField(entry.getText(), entry.getValue())
                       .setDefaultValue(entry.getDefaultValue())
                       .setMin(entry.getExtras().getBounds().getMin())
                       .setMax(entry.getExtras().getBounds().getMax())
                       .setTooltip(entry.getTooltip())
                       .setSaveConsumer(entry::setValue),
               entry.requiresRestart()
       ), false, float.class, Float.class);
       registerProvider((GuiProvider<Double>) entry -> build(
               builder -> builder
                       .startDoubleField(entry.getText(), entry.getValue())
                       .setDefaultValue(entry.getDefaultValue())
                       .setTooltip(entry.getTooltip())
                       .setSaveConsumer(entry::setValue),
               entry.requiresRestart()
       ), double.class, Double.class);
       registerBoundedProvider((GuiProvider<Double>) entry -> build(
               builder -> builder
                       .startDoubleField(entry.getText(), entry.getValue())
                       .setDefaultValue(entry.getDefaultValue())
                       .setMin(entry.getExtras().getBounds().getMin())
                       .setMax(entry.getExtras().getBounds().getMax())
                       .setTooltip(entry.getTooltip())
                       .setSaveConsumer(entry::setValue),
               entry.requiresRestart()
       ), false, double.class, Double.class);
       registerProvider((GuiProvider<String>) entry -> build(
               builder -> builder
                       .startStrField(entry.getText(), entry.getValue())
                       .setDefaultValue(entry.getDefaultValue())
                       .setTooltip(entry.getTooltip())
                       .setSaveConsumer(entry::setValue),
               entry.requiresRestart()
       ), String.class);
       registerEnumProvider((GuiProvider<? extends Enum<?>>) entry -> build(
               builder -> builder
                       .startEnumSelector(entry.getText(), entry.getTypeClass(), entry.getValue())
                       .setDefaultValue(entry.getDefaultValue())
                       .setTooltip(entry.getTooltip())
                       .setEnumNameProvider(entry.getExtras().getEnumOptions().getNameProvider())
                       .setSaveConsumer(entry::setValue),
               entry.requiresRestart()
       ), EnumOptions.DisplayType.BUTTON);
       registerEnumProvider((GuiProvider<? extends Enum>) entry -> {
           List<Enum> enumValues = Arrays.asList(((Class<? extends Enum>) entry.getTypeClass()).getEnumConstants());
           return build(
                   builder -> builder
                           .startDropdownMenu(entry.getText(), DropdownMenuBuilder.TopCellElementBuilder.of(
                                   entry.getValue(),
                                   enumTranslation -> enumValues.stream().filter(enumValue -> entry.getExtras().getEnumOptions().getNameProvider().apply(enumValue).getString().equals(enumTranslation)).collect(MoreCollectors.toOptional()).orElse(null),
                                   entry.getExtras().getEnumOptions().getNameProvider()
                           ), DropdownMenuBuilder.CellCreatorBuilder.of(entry.getExtras().getEnumOptions().getNameProvider()))
                           .setSelections(enumValues)
                           .setDefaultValue(entry.getDefaultValue())
                           .setSaveConsumer(entry::setValue),
                   entry.requiresRestart()
           );
       }, EnumOptions.DisplayType.DROPDOWN);
       registerProvider((GuiProvider<List<Integer>>) entry -> build(
               builder -> builder
                       .startIntList(entry.getText(), entry.getValue())
                       .setDefaultValue(entry.getDefaultValue())
                       .setTooltip(entry.getTooltip())
                       .setSaveConsumer(entry::setValue),
               entry.requiresRestart()
       ), new TypeToken<List<Integer>>() {}.getType());
       registerProvider((GuiProvider<List<Long>>) entry -> build(
               builder -> builder
                       .startLongList(entry.getText(), entry.getValue())
                       .setDefaultValue(entry.getDefaultValue())
                       .setTooltip(entry.getTooltip())
                       .setSaveConsumer(entry::setValue),
               entry.requiresRestart()
       ), new TypeToken<List<Long>>() {}.getType());
       registerProvider((GuiProvider<List<Float>>) entry -> build(
               builder -> builder
                       .startFloatList(entry.getText(), entry.getValue())
                       .setDefaultValue(entry.getDefaultValue())
                       .setTooltip(entry.getTooltip())
                       .setSaveConsumer(entry::setValue),
               entry.requiresRestart()
       ), new TypeToken<List<Float>>() {}.getType());
       registerProvider((GuiProvider<List<Double>>) entry -> build(
               builder -> builder
                       .startDoubleList(entry.getText(), entry.getValue())
                       .setDefaultValue(entry.getDefaultValue())
                       .setTooltip(entry.getTooltip())
                       .setSaveConsumer(entry::setValue),
               entry.requiresRestart()
       ), new TypeToken<List<Double>>() {}.getType());
       registerProvider((GuiProvider<List<String>>) entry -> build(
               builder -> builder
                       .startStrList(entry.getText(), entry.getValue())
                       .setDefaultValue(entry.getDefaultValue())
                       .setTooltip(entry.getTooltip())
                       .setSaveConsumer(entry::setValue),
               entry.requiresRestart()
       ), new TypeToken<List<String>>() {}.getType());
    }

    <T> Optional<GuiProvider<T>> getProvider(Entry<T> entry) {
        for (GuiProviderRegistration<?> registration : Lists.reverse(registrations)) {
            if (registration.test(entry)) {
                return Optional.of((GuiProvider<T>) registration.getProvider());
            }
        }
        return Optional.empty();
    }

}