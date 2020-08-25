package me.lortseam.completeconfig.gui;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import me.lortseam.completeconfig.entry.Entry;
import me.lortseam.completeconfig.entry.EnumOptions;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.TranslatableText;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class GuiRegistry {

    private final List<Registration> registrations = new ArrayList<>();

    GuiRegistry() {
        registerDefaultProviders();
    }

    public <T> void registerProvider(GuiProvider<T> provider, GuiProviderPredicate<T> predicate, Class... types) {
        registrations.add(new Registration<>(predicate.and((field, extras) -> {
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
        registerProvider((GuiProvider<Boolean>) (text, field, value, defaultValue, tooltip, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startBooleanToggle(text, value)
                .setDefaultValue(defaultValue)
                .setTooltip(tooltip)
                .setSaveConsumer(saveConsumer)
                .build(),
                boolean.class, Boolean.class
        );
        registerProvider((GuiProvider<Integer>) (text, field, value, defaultValue, tooltip, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startIntField(text, value)
                .setDefaultValue(defaultValue)
                .setTooltip(tooltip)
                .setSaveConsumer(saveConsumer)
                .build(),
                int.class, Integer.class
        );
        registerBoundedProvider((GuiProvider<Integer>) (text, field, value, defaultValue, tooltip, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startIntField(text, value)
                .setDefaultValue(defaultValue)
                .setMin(extras.getBounds().getMin())
                .setMax(extras.getBounds().getMax())
                .setTooltip(tooltip)
                .setSaveConsumer(saveConsumer)
                .build(),
                false, int.class, Integer.class
        );
        registerBoundedProvider((GuiProvider<Integer>) (text, field, value, defaultValue, tooltip, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startIntSlider(text, value, extras.getBounds().getMin(), extras.getBounds().getMax())
                .setDefaultValue(defaultValue)
                .setTooltip(tooltip)
                .setSaveConsumer(saveConsumer)
                .build(),
                true, int.class, Integer.class
        );
        registerProvider((GuiProvider<Long>) (text, field, value, defaultValue, tooltip, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startLongField(text, value)
                .setDefaultValue(defaultValue)
                .setTooltip(tooltip)
                .setSaveConsumer(saveConsumer)
                .build(),
                long.class, Long.class
        );
        registerBoundedProvider((GuiProvider<Long>) (text, field, value, defaultValue, tooltip, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startLongField(text, value)
                .setDefaultValue(defaultValue)
                .setMin(extras.getBounds().getMin())
                .setMax(extras.getBounds().getMax())
                .setTooltip(tooltip)
                .setSaveConsumer(saveConsumer)
                .build(),
                false, long.class, Long.class
        );
        registerBoundedProvider((GuiProvider<Long>) (text, field, value, defaultValue, tooltip, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startLongSlider(text, value, extras.getBounds().getMin(), extras.getBounds().getMax())
                .setDefaultValue(defaultValue)
                .setTooltip(tooltip)
                .setSaveConsumer(saveConsumer)
                .build(),
                true, long.class, Long.class
        );
        registerProvider((GuiProvider<Float>) (text, field, value, defaultValue, tooltip, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startFloatField(text, value)
                .setDefaultValue(defaultValue)
                .setTooltip(tooltip)
                .setSaveConsumer(saveConsumer)
                .build(),
                float.class, Float.class
        );
        registerBoundedProvider((GuiProvider<Float>) (text, field, value, defaultValue, tooltip, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startFloatField(text, value)
                .setDefaultValue(defaultValue)
                .setMin(extras.getBounds().getMin())
                .setMax(extras.getBounds().getMax())
                .setTooltip(tooltip)
                .setSaveConsumer(saveConsumer)
                .build(),
                false, float.class, Float.class
        );
        registerProvider((GuiProvider<Double>) (text, field, value, defaultValue, tooltip, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startDoubleField(text, value)
                .setDefaultValue(defaultValue)
                .setTooltip(tooltip)
                .setSaveConsumer(saveConsumer)
                .build(),
                double.class, Double.class
        );
        registerBoundedProvider((GuiProvider<Double>) (text, field, value, defaultValue, tooltip, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startDoubleField(text, value)
                .setDefaultValue(defaultValue)
                .setMin(extras.getBounds().getMin())
                .setMax(extras.getBounds().getMax())
                .setTooltip(tooltip)
                .setSaveConsumer(saveConsumer)
                .build(),
                false, double.class, Double.class
        );
        registerProvider((GuiProvider<String>) (text, field, value, defaultValue, tooltip, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startStrField(text, value)
                .setDefaultValue(defaultValue)
                .setTooltip(tooltip)
                .setSaveConsumer(saveConsumer)
                .build(),
                String.class
        );
        registerEnumProvider((GuiProvider<? extends Enum>) (text, field, value, defaultValue, tooltip, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startEnumSelector(text, (Class<Enum>) field.getType(), value)
                .setDefaultValue(defaultValue)
                .setTooltip(tooltip)
                                                                                            //TODO: Bad solution; provide custom enum name provider
                .setEnumNameProvider(e -> new TranslatableText(((TranslatableText) text).getKey() + "." + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, e.name())))
                .setSaveConsumer(saveConsumer)
                .build(),
                EnumOptions.DisplayType.BUTTON
        );
        //TODO: Enum as dropdown
        registerGenericProvider((GuiProvider<List<Integer>>) (text, field, value, defaultValue, tooltip, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startIntList(text, value)
                .setDefaultValue(defaultValue)
                .setTooltip(tooltip)
                .setSaveConsumer(saveConsumer)
                .build(),
                List.class, Integer.class
        );
        registerGenericProvider((GuiProvider<List<Long>>) (text, field, value, defaultValue, tooltip, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startLongList(text, value)
                .setDefaultValue(defaultValue)
                .setTooltip(tooltip)
                .setSaveConsumer(saveConsumer)
                .build(),
                List.class, Long.class
        );
        registerGenericProvider((GuiProvider<List<Float>>) (text, field, value, defaultValue, tooltip, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startFloatList(text, value)
                .setDefaultValue(defaultValue)
                .setTooltip(tooltip)
                .setSaveConsumer(saveConsumer)
                .build(),
                List.class, Float.class
        );
        registerGenericProvider((GuiProvider<List<Double>>) (text, field, value, defaultValue, tooltip, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startDoubleList(text, value)
                .setDefaultValue(defaultValue)
                .setTooltip(tooltip)
                .setSaveConsumer(saveConsumer)
                .build(),
                List.class, Double.class
        );
        registerGenericProvider((GuiProvider<List<String>>) (text, field, value, defaultValue, tooltip, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startStrList(text, value)
                .setDefaultValue(defaultValue)
                .setTooltip(tooltip)
                .setSaveConsumer(saveConsumer)
                .build(),
                List.class, String.class
        );
    }

    <T> Optional<GuiProvider<T>> getProvider(Entry<T> entry) {
        for (Registration<?> registration : Lists.reverse(registrations)) {
            if (registration.test(entry)) {
                return Optional.of((GuiProvider<T>) registration.getProvider());
            }
        }
        return Optional.empty();
    }

}