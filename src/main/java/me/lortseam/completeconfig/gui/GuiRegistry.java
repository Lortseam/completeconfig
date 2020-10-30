package me.lortseam.completeconfig.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.MoreCollectors;
import me.lortseam.completeconfig.entry.Entry;
import me.lortseam.completeconfig.entry.EnumOptions;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import me.shedaniel.clothconfig2.impl.builders.FieldBuilder;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class GuiRegistry {

    //TODO: Only used for setting requireRestart cause that method returns void, should be done differently
    public static <T, A extends AbstractConfigListEntry> A build(Consumer<FieldBuilder<T, A>> fieldBuilderModifier, Function<ConfigEntryBuilder, FieldBuilder<T, A>> builder) {
        FieldBuilder<T, A> fieldBuilder = builder.apply(ConfigEntryBuilder.create());
        fieldBuilderModifier.accept(fieldBuilder);
        return fieldBuilder.build();
    }

    private final List<GuiProviderRegistration> registrations = new ArrayList<>();

    GuiRegistry() {
        registerDefaultProviders();
    }

    public <T> void registerProvider(GuiProvider<T> provider, GuiProviderPredicate<T> predicate, Class... types) {
        registrations.add(new GuiProviderRegistration<>(predicate.and((field, extras) -> {
            if (types.length == 0) {
                return true;
            }
            Type fieldType = field.getGenericType();
            return ArrayUtils.contains(types, fieldType instanceof ParameterizedType ? ((ParameterizedType) fieldType).getRawType() : fieldType);
        }), provider));
    }

    public void registerProvider(GuiProvider<?> provider, Class... types) {
        if (types.length == 0) {
            throw new IllegalArgumentException("Types must not be empty");
        }
        registerProvider(provider, (field, extras) -> true, types);
    }

    private void registerBoundedProvider(GuiProvider<?> provider, boolean slider, Class... types) {
        registerProvider(provider, (field, extras) -> extras.getBounds() != null && extras.getBounds().isSlider() == slider, types);
    }

    private void registerEnumProvider(GuiProvider<? extends Enum> provider, EnumOptions.DisplayType enumDisplayType) {
        registerProvider(provider, (field, extras) -> Enum.class.isAssignableFrom(field.getType()) && extras.getEnumOptions().getDisplayType() == enumDisplayType);
    }

    public <T> void registerGenericProvider(GuiProvider<T> provider, Class<?> type, Class... genericTypes) {
        registerProvider(provider, (field, extras) -> {
            Type fieldType = field.getGenericType();
            if (!(fieldType instanceof ParameterizedType)) {
                return false;
            }
            return Arrays.equals(((ParameterizedType) fieldType).getActualTypeArguments(), genericTypes);
        }, type);
    }

    private void registerDefaultProviders() {
       registerProvider((GuiProvider<Boolean>) (text, field, value, defaultValue, tooltip, extras, saveConsumer, requiresRestart) -> build(
               builder -> builder.requireRestart(requiresRestart),
               builder -> builder
                       .startBooleanToggle(text, value)
                       .setDefaultValue(defaultValue)
                       .setTooltip(tooltip)
                       .setSaveConsumer(saveConsumer)
       ), boolean.class, Boolean.class);
       registerProvider((GuiProvider<Integer>) (text, field, value, defaultValue, tooltip, extras, saveConsumer, requiresRestart) -> build(
               builder -> builder.requireRestart(requiresRestart),
               builder -> builder
                       .startIntField(text, value)
                       .setDefaultValue(defaultValue)
                       .setTooltip(tooltip)
                       .setSaveConsumer(saveConsumer)
       ), int.class, Integer.class);
       registerBoundedProvider((GuiProvider<Integer>) (text, field, value, defaultValue, tooltip, extras, saveConsumer, requiresRestart) -> build(
               builder -> builder.requireRestart(requiresRestart),
               builder -> builder
                       .startIntField(text, value)
                       .setDefaultValue(defaultValue)
                       .setMin(extras.getBounds().getMin())
                       .setMax(extras.getBounds().getMax())
                       .setTooltip(tooltip)
                       .setSaveConsumer(saveConsumer)
       ), false, int.class, Integer.class);
       registerBoundedProvider((GuiProvider<Integer>) (text, field, value, defaultValue, tooltip, extras, saveConsumer, requiresRestart) -> build(
               builder -> builder.requireRestart(requiresRestart),
               builder -> builder
                       .startIntSlider(text, value, extras.getBounds().getMin(), extras.getBounds().getMax())
                       .setDefaultValue(defaultValue)
                       .setTooltip(tooltip)
                       .setSaveConsumer(saveConsumer)
       ), true, int.class, Integer.class);
       registerProvider((GuiProvider<Long>) (text, field, value, defaultValue, tooltip, extras, saveConsumer, requiresRestart) -> build(
               builder -> builder.requireRestart(requiresRestart),
               builder -> builder
                       .startLongField(text, value)
                       .setDefaultValue(defaultValue)
                       .setTooltip(tooltip)
                       .setSaveConsumer(saveConsumer)
       ), long.class, Long.class);
       registerBoundedProvider((GuiProvider<Long>) (text, field, value, defaultValue, tooltip, extras, saveConsumer, requiresRestart) -> build(
               builder -> builder.requireRestart(requiresRestart),
               builder -> builder
                       .startLongField(text, value)
                       .setDefaultValue(defaultValue)
                       .setMin(extras.getBounds().getMin())
                       .setMax(extras.getBounds().getMax())
                       .setTooltip(tooltip)
                       .setSaveConsumer(saveConsumer)
       ), false, long.class, Long.class);
       registerBoundedProvider((GuiProvider<Long>) (text, field, value, defaultValue, tooltip, extras, saveConsumer, requiresRestart) -> build(
               builder -> builder.requireRestart(requiresRestart),
               builder -> builder
                       .startLongSlider(text, value, extras.getBounds().getMin(), extras.getBounds().getMax())
                       .setDefaultValue(defaultValue)
                       .setTooltip(tooltip)
                       .setSaveConsumer(saveConsumer)
       ), true, long.class, Long.class);
       registerProvider((GuiProvider<Float>) (text, field, value, defaultValue, tooltip, extras, saveConsumer, requiresRestart) -> build(
               builder -> builder.requireRestart(requiresRestart),
               builder -> builder
                       .startFloatField(text, value)
                       .setDefaultValue(defaultValue)
                       .setTooltip(tooltip)
                       .setSaveConsumer(saveConsumer)
       ), float.class, Float.class);
       registerBoundedProvider((GuiProvider<Float>) (text, field, value, defaultValue, tooltip, extras, saveConsumer, requiresRestart) -> build(
               builder -> builder.requireRestart(requiresRestart),
               builder -> builder
                       .startFloatField(text, value)
                       .setDefaultValue(defaultValue)
                       .setMin(extras.getBounds().getMin())
                       .setMax(extras.getBounds().getMax())
                       .setTooltip(tooltip)
                       .setSaveConsumer(saveConsumer)
       ), false, float.class, Float.class);
       registerProvider((GuiProvider<Double>) (text, field, value, defaultValue, tooltip, extras, saveConsumer, requiresRestart) -> build(
               builder -> builder.requireRestart(requiresRestart),
               builder -> builder
                       .startDoubleField(text, value)
                       .setDefaultValue(defaultValue)
                       .setTooltip(tooltip)
                       .setSaveConsumer(saveConsumer)
       ), double.class, Double.class);
       registerBoundedProvider((GuiProvider<Double>) (text, field, value, defaultValue, tooltip, extras, saveConsumer, requiresRestart) -> build(
               builder -> builder.requireRestart(requiresRestart),
               builder -> builder
                       .startDoubleField(text, value)
                       .setDefaultValue(defaultValue)
                       .setMin(extras.getBounds().getMin())
                       .setMax(extras.getBounds().getMax())
                       .setTooltip(tooltip)
                       .setSaveConsumer(saveConsumer)
       ), false, double.class, Double.class);
       registerProvider((GuiProvider<String>) (text, field, value, defaultValue, tooltip, extras, saveConsumer, requiresRestart) -> build(
               builder -> builder.requireRestart(requiresRestart),
               builder -> builder
                       .startStrField(text, value)
                       .setDefaultValue(defaultValue)
                       .setTooltip(tooltip)
                       .setSaveConsumer(saveConsumer)
       ), String.class);
       registerEnumProvider((GuiProvider<? extends Enum<?>>) (text, field, value, defaultValue, tooltip, extras, saveConsumer, requiresRestart) -> build(
               builder -> builder.requireRestart(requiresRestart),
               builder -> builder
                       .startEnumSelector(text, (Class<Enum<?>>) field.getType(), value)
                       .setDefaultValue(defaultValue)
                       .setTooltip(tooltip)
                       .setEnumNameProvider(extras.getEnumOptions().getNameProvider())
                       .setSaveConsumer(saveConsumer)
       ), EnumOptions.DisplayType.BUTTON);
       registerEnumProvider((GuiProvider<? extends Enum>) (text, field, value, defaultValue, tooltip, extras, saveConsumer, requiresRestart) -> {
           List<Enum> enumValues = Arrays.asList(((Class<? extends Enum>) field.getType()).getEnumConstants());
           return build(
                   builder -> builder.requireRestart(requiresRestart),
                   builder -> builder
                           .startDropdownMenu(text, DropdownMenuBuilder.TopCellElementBuilder.of(
                                   value,
                                   enumTranslation -> enumValues.stream().filter(enumValue -> extras.getEnumOptions().getNameProvider().apply(enumValue).getString().equals(enumTranslation)).collect(MoreCollectors.toOptional()).orElse(null),
                                   extras.getEnumOptions().getNameProvider()
                           ), DropdownMenuBuilder.CellCreatorBuilder.of(extras.getEnumOptions().getNameProvider()))
                           .setSelections(enumValues)
                           .setDefaultValue(defaultValue)
                           .setSaveConsumer(saveConsumer)
           );
       }, EnumOptions.DisplayType.DROPDOWN);
       registerGenericProvider((GuiProvider<List<Integer>>) (text, field, value, defaultValue, tooltip, extras, saveConsumer, requiresRestart) -> build(
               builder -> builder.requireRestart(requiresRestart),
               builder -> builder
                       .startIntList(text, value)
                       .setDefaultValue(defaultValue)
                       .setTooltip(tooltip)
                       .setSaveConsumer(saveConsumer)
       ), List.class, Integer.class);
       registerGenericProvider((GuiProvider<List<Long>>) (text, field, value, defaultValue, tooltip, extras, saveConsumer, requiresRestart) -> build(
               builder -> builder.requireRestart(requiresRestart),
               builder -> builder
                       .startLongList(text, value)
                       .setDefaultValue(defaultValue)
                       .setTooltip(tooltip)
                       .setSaveConsumer(saveConsumer)
       ), List.class, Long.class);
       registerGenericProvider((GuiProvider<List<Float>>) (text, field, value, defaultValue, tooltip, extras, saveConsumer, requiresRestart) -> build(
               builder -> builder.requireRestart(requiresRestart),
               builder -> builder
                       .startFloatList(text, value)
                       .setDefaultValue(defaultValue)
                       .setTooltip(tooltip)
                       .setSaveConsumer(saveConsumer)
       ), List.class, Float.class);
       registerGenericProvider((GuiProvider<List<Double>>) (text, field, value, defaultValue, tooltip, extras, saveConsumer, requiresRestart) -> build(
               builder -> builder.requireRestart(requiresRestart),
               builder -> builder
                       .startDoubleList(text, value)
                       .setDefaultValue(defaultValue)
                       .setTooltip(tooltip)
                       .setSaveConsumer(saveConsumer)
       ), List.class, Double.class);
       registerGenericProvider((GuiProvider<List<String>>) (text, field, value, defaultValue, tooltip, extras, saveConsumer, requiresRestart) -> build(
               builder -> builder.requireRestart(requiresRestart),
               builder -> builder
                       .startStrList(text, value)
                       .setDefaultValue(defaultValue)
                       .setTooltip(tooltip)
                       .setSaveConsumer(saveConsumer)
       ), List.class, String.class);
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