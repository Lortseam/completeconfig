package me.lortseam.completeconfig.gui;

import com.google.common.base.CaseFormat;
import me.lortseam.completeconfig.entry.Entry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.TranslatableText;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class GuiRegistry {

    //TODO: Create own class with predicate, provider and test method
    private final LinkedHashMap<GuiProviderPredicate, GuiProvider> guiProviders = new LinkedHashMap<>();

    GuiRegistry() {
        registerDefaultProviders();
    }

    public <T> void registerProvider(GuiProvider<T> provider, GuiProviderPredicate<T> predicate, Class... types) {
        guiProviders.put(predicate.and((field, extras) -> {
            if (types.length == 0) {
                return true;
            }
            Type fieldType = field.getGenericType();
            return ArrayUtils.contains(types, fieldType instanceof ParameterizedType ? ((ParameterizedType) fieldType).getRawType() : fieldType);
        }), provider);
    }

    public void registerProvider(GuiProvider<?> provider, Class... types) {
        if (types.length == 0) {
            throw new IllegalArgumentException("Types must not be empty");
        }
        registerProvider(provider, (field, extras) -> true, types);
    }

    public void registerBoundedProvider(GuiProvider<?> provider, Class... types) {
        registerProvider(provider, (field, extras) -> extras.getBounds() != null, types);
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
                .startIntSlider(text, value, extras.getBounds().getMin(), extras.getBounds().getMax())
                .setDefaultValue(defaultValue)
                .setTooltip(tooltip)
                .setSaveConsumer(saveConsumer)
                .build(),
                int.class, Integer.class
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
                .startLongSlider(text, value, extras.getBounds().getMin(), extras.getBounds().getMax())
                .setDefaultValue(defaultValue)
                .setTooltip(tooltip)
                .setSaveConsumer(saveConsumer)
                .build(),
                long.class, Long.class
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
                float.class, Float.class
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
                double.class, Double.class
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
        registerProvider((GuiProvider<? extends Enum>) (text, field, value, defaultValue, tooltip, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startEnumSelector(text, (Class<Enum>) field.getType(), value)
                .setDefaultValue(defaultValue)
                .setTooltip(tooltip)
                                                                                            //TODO: Bad solution; provide custom enum name provider
                .setEnumNameProvider(e -> new TranslatableText(((TranslatableText) text).getKey() + "." + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, e.name())))
                .setSaveConsumer(saveConsumer)
                .build(),
                (field, extras) -> Enum.class.isAssignableFrom(field.getType())
        );
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
        Iterator<Map.Entry<GuiProviderPredicate, GuiProvider>> iter = new LinkedList<>(guiProviders.entrySet()).descendingIterator();
        while (iter.hasNext()) {
            Map.Entry<GuiProviderPredicate, GuiProvider> mapEntry = iter.next();
            if (mapEntry.getKey().test(entry.getField(), entry.getExtras())) {
                return Optional.of(mapEntry.getValue());
            }
        }
        return Optional.empty();
    }

}