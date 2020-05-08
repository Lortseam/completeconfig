package me.lortseam.completeconfig.entry;

import com.google.common.base.CaseFormat;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.resource.language.I18n;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

public class GuiRegistry {

    private final LinkedHashMap<GuiProviderPredicate, GuiProvider> guiProviders = new LinkedHashMap<>();

    public GuiRegistry() {
        registerDefaultProviders();
    }

    public void registerProvider(GuiProviderPredicate predicate, GuiProvider provider) {
        guiProviders.put(predicate, provider);
    }

    public <T> void registerTypeProvider(Class<T> type, GuiProvider<T> provider) {
        registerProvider((field, fieldType, extras) -> fieldType == type, provider);
    }

    public <T> void registerBoundedTypeProvider(Class<T> type, GuiProvider<T> provider) {
        registerProvider((field, fieldType, extras) -> fieldType == type && extras.getBounds() != null, provider);
    }

    private void registerDefaultProviders() {
        registerTypeProvider(Boolean.TYPE, (translationKey, type, value, defaultValue, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startBooleanToggle(translationKey, value)
                .setDefaultValue(defaultValue)
                .setSaveConsumer(saveConsumer)
                .build()
        );
        registerTypeProvider(Integer.TYPE, (translationKey, type, value, defaultValue, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startIntField(translationKey, value)
                .setDefaultValue(defaultValue)
                .setSaveConsumer(saveConsumer)
                .build()
        );
        registerBoundedTypeProvider(Integer.TYPE, (translationKey, type, value, defaultValue, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startIntSlider(translationKey, value, extras.getBounds().getMin(), extras.getBounds().getMax())
                .setDefaultValue(defaultValue)
                .setSaveConsumer(saveConsumer)
                .build()
        );
        registerTypeProvider(Long.TYPE, (translationKey, type, value, defaultValue, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startLongField(translationKey, value)
                .setDefaultValue(defaultValue)
                .setSaveConsumer(saveConsumer)
                .build()
        );
        registerBoundedTypeProvider(Long.TYPE, (translationKey, type, value, defaultValue, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startLongSlider(translationKey, value, extras.getBounds().getMin(), extras.getBounds().getMax())
                .setDefaultValue(defaultValue)
                .setSaveConsumer(saveConsumer)
                .build()
        );
        registerTypeProvider(Float.TYPE, (translationKey, type, value, defaultValue, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startFloatField(translationKey, value)
                .setDefaultValue(defaultValue)
                .setSaveConsumer(saveConsumer)
                .build()
        );
        registerBoundedTypeProvider(Float.TYPE, (translationKey, type, value, defaultValue, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startFloatField(translationKey, value)
                .setDefaultValue(defaultValue)
                .setMin(extras.getBounds().getMin())
                .setMax(extras.getBounds().getMax())
                .setSaveConsumer(saveConsumer)
                .build()
        );
        registerTypeProvider(Double.TYPE, (translationKey, type, value, defaultValue, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startDoubleField(translationKey, value)
                .setDefaultValue(defaultValue)
                .setSaveConsumer(saveConsumer)
                .build()
        );
        registerBoundedTypeProvider(Double.TYPE, (translationKey, type, value, defaultValue, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startDoubleField(translationKey, value)
                .setDefaultValue(defaultValue)
                .setMin(extras.getBounds().getMin())
                .setMax(extras.getBounds().getMax())
                .setSaveConsumer(saveConsumer)
                .build()
        );
        registerProvider((field, type, extras) -> Enum.class.isAssignableFrom(type), (GuiProvider<? extends Enum>) (translationKey, type, value, defaultValue, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startEnumSelector(translationKey, type, value)
                .setDefaultValue(defaultValue)
                .setEnumNameProvider(e -> I18n.translate(translationKey + "." + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, e.name())))
                .setSaveConsumer(saveConsumer)
                .build());
    }

    public <T> GuiProvider<T> getProvider(Entry<T> entry) {
        GuiProvider<T> guiProvider = null;
        for (Map.Entry<GuiProviderPredicate, GuiProvider> mapEntry : guiProviders.entrySet()) {
            Field field = entry.getField();
            if (mapEntry.getKey().test(field, field.getType(), entry.getExtras())) {
                guiProvider = mapEntry.getValue();
            }
        }
        return guiProvider;
    }

}