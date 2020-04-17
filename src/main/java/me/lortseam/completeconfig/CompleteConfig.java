package me.lortseam.completeconfig;

import com.google.common.base.CaseFormat;
import me.lortseam.completeconfig.entry.Entry;
import me.lortseam.completeconfig.entry.GuiProvider;
import me.lortseam.completeconfig.entry.GuiRegistry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.resource.language.I18n;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class CompleteConfig {

    private static final Set<ConfigManager> managers = new HashSet<>();

    public static ConfigManager register(String modID) {
        if (getManager(modID).isPresent()) {
            throw new RuntimeException("There is already registered a manager for this mod ID!");
        }
        ConfigManager manager = new ConfigManager(modID);
        registerDefaultGuiProviders(manager.getGuiRegistry());
        managers.add(manager);
        return manager;
    }

    private static void registerDefaultGuiProviders(GuiRegistry registry) {
        registry.registerTypeProvider(Boolean.TYPE, (translationKey, type, value, defaultValue, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startBooleanToggle(translationKey, value)
                .setDefaultValue(defaultValue)
                .setSaveConsumer(saveConsumer)
                .build()
        );
        registry.registerTypeProvider(Integer.TYPE, (translationKey, type, value, defaultValue, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startIntField(translationKey, value)
                .setDefaultValue(defaultValue)
                .setSaveConsumer(saveConsumer)
                .build()
        );
        registry.registerBoundedTypeProvider(Integer.TYPE, (translationKey, type, value, defaultValue, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startIntSlider(translationKey, value, extras.getBounds().getMin(), extras.getBounds().getMax())
                .setDefaultValue(defaultValue)
                .setSaveConsumer(saveConsumer)
                .build()
        );
        registry.registerTypeProvider(Long.TYPE, (translationKey, type, value, defaultValue, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startLongField(translationKey, value)
                .setDefaultValue(defaultValue)
                .setSaveConsumer(saveConsumer)
                .build()
        );
        registry.registerBoundedTypeProvider(Long.TYPE, (translationKey, type, value, defaultValue, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startLongSlider(translationKey, value, extras.getBounds().getMin(), extras.getBounds().getMax())
                .setDefaultValue(defaultValue)
                .setSaveConsumer(saveConsumer)
                .build()
        );
        registry.registerTypeProvider(Float.TYPE, (translationKey, type, value, defaultValue, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startFloatField(translationKey, value)
                .setDefaultValue(defaultValue)
                .setSaveConsumer(saveConsumer)
                .build()
        );
        registry.registerTypeProvider(Double.TYPE, (translationKey, type, value, defaultValue, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startDoubleField(translationKey, value)
                .setDefaultValue(defaultValue)
                .setSaveConsumer(saveConsumer)
                .build()
        );
        registry.registerProvider((field, type, extras) -> Enum.class.isAssignableFrom(type), (GuiProvider<? extends Enum>) (translationKey, type, value, defaultValue, extras, saveConsumer) -> ConfigEntryBuilder
                .create()
                .startEnumSelector(translationKey, type, value)
                .setDefaultValue(defaultValue)
                .setEnumNameProvider(e -> I18n.translate(translationKey + "." + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, e.name())))
                .setSaveConsumer(saveConsumer)
                .build());
    }

    public static Optional<ConfigManager> getManager(String modID) {
        return managers.stream().filter(manager -> manager.getModID().equals(modID)).findAny();
    }

}
