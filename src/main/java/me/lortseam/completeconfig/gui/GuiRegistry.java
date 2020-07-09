package me.lortseam.completeconfig.gui;

import com.google.common.base.CaseFormat;
import me.lortseam.completeconfig.entry.Entry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.resource.language.I18n;
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
        registerProvider((GuiProvider<Boolean>) (translationKey, field, value, defaultValue, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startBooleanToggle(new TranslatableText(translationKey), value)
                .setDefaultValue(defaultValue)
                .setSaveConsumer(saveConsumer)
                .build(),
                boolean.class, Boolean.class
        );
        registerProvider((GuiProvider<Integer>) (translationKey, field, value, defaultValue, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startIntField(new TranslatableText(translationKey), value)
                .setDefaultValue(defaultValue)
                .setSaveConsumer(saveConsumer)
                .build(),
                int.class, Integer.class
        );
        registerBoundedProvider((GuiProvider<Integer>) (translationKey, field, value, defaultValue, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startIntSlider(new TranslatableText(translationKey), value, extras.getBounds().getMin(), extras.getBounds().getMax())
                .setDefaultValue(defaultValue)
                .setSaveConsumer(saveConsumer)
                .build(),
                int.class, Integer.class
        );
        registerProvider((GuiProvider<Long>) (translationKey, field, value, defaultValue, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startLongField(new TranslatableText(translationKey), value)
                .setDefaultValue(defaultValue)
                .setSaveConsumer(saveConsumer)
                .build(),
                long.class, Long.class
        );
        registerBoundedProvider((GuiProvider<Long>) (translationKey, field, value, defaultValue, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startLongSlider(new TranslatableText(translationKey), value, extras.getBounds().getMin(), extras.getBounds().getMax())
                .setDefaultValue(defaultValue)
                .setSaveConsumer(saveConsumer)
                .build(),
                long.class, Long.class
        );
        registerProvider((GuiProvider<Float>) (translationKey, field, value, defaultValue, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startFloatField(new TranslatableText(translationKey), value)
                .setDefaultValue(defaultValue)
                .setSaveConsumer(saveConsumer)
                .build(),
                float.class, Float.class
        );
        registerBoundedProvider((GuiProvider<Float>) (translationKey, field, value, defaultValue, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startFloatField(new TranslatableText(translationKey), value)
                .setDefaultValue(defaultValue)
                .setMin(extras.getBounds().getMin())
                .setMax(extras.getBounds().getMax())
                .setSaveConsumer(saveConsumer)
                .build(),
                float.class, Float.class
        );
        registerProvider((GuiProvider<Double>) (translationKey, field, value, defaultValue, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startDoubleField(new TranslatableText(translationKey), value)
                .setDefaultValue(defaultValue)
                .setSaveConsumer(saveConsumer)
                .build(),
                double.class, Double.class
        );
        registerBoundedProvider((GuiProvider<Double>) (translationKey, field, value, defaultValue, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startDoubleField(new TranslatableText(translationKey), value)
                .setDefaultValue(defaultValue)
                .setMin(extras.getBounds().getMin())
                .setMax(extras.getBounds().getMax())
                .setSaveConsumer(saveConsumer)
                .build(),
                double.class, Double.class
        );
        registerProvider((GuiProvider<String>) (translationKey, field, value, defaultValue, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startStrField(new TranslatableText(translationKey), value)
                .setDefaultValue(defaultValue)
                .setSaveConsumer(saveConsumer)
                .build(),
                String.class
        );
        registerProvider((GuiProvider<? extends Enum>) (translationKey, field, value, defaultValue, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startEnumSelector(new TranslatableText(translationKey), (Class<Enum>) field.getType(), value)
                .setDefaultValue(defaultValue)
                .setEnumNameProvider(e -> new TranslatableText(translationKey + "." + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, e.name())))
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