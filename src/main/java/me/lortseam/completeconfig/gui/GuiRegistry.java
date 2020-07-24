package me.lortseam.completeconfig.gui;

import com.google.common.base.CaseFormat;
import me.lortseam.completeconfig.entry.Entry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.TranslatableText;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class GuiRegistry {

    private final LinkedHashMap<GuiProviderPredicate, GuiProvider> guiProviders = new LinkedHashMap<>();

    public GuiRegistry() {
        registerDefaultProviders();
    }

    public <T> void registerProvider(GuiProvider<T> provider, GuiProviderPredicate<T> predicate, Class... types) {
        guiProviders.put(predicate.and((field, extras) -> types.length == 0 || ArrayUtils.contains(types, field.getType())), provider);
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
    }

    public <T> GuiProvider<T> getProvider(Entry<T> entry) {
        Iterator<Map.Entry<GuiProviderPredicate, GuiProvider>> iter = new LinkedList<>(guiProviders.entrySet()).descendingIterator();
        while (iter.hasNext()) {
            Map.Entry<GuiProviderPredicate, GuiProvider> mapEntry = iter.next();
            if (mapEntry.getKey().test(entry.getField(), entry.getExtras())) {
                return mapEntry.getValue();
            }
        }
        return null;
    }

}