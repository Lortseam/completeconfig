package me.lortseam.completeconfig.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.io.ConfigSource;
import me.lortseam.completeconfig.text.TranslationKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;

import java.util.Arrays;

@Log4j2(topic = "CompleteConfig")
public abstract class Config extends BaseCollection implements ConfigContainer {

    @Getter(AccessLevel.PACKAGE)
    private final ConfigSource source;
    @Environment(EnvType.CLIENT)
    private TranslationKey translation;

    protected Config(@NonNull String modId, @NonNull String[] branch, boolean saveOnExit) {
        if (!FabricLoader.getInstance().isModLoaded(modId)) {
            throw new IllegalArgumentException("Mod " + modId + " is not loaded");
        }
        source = new ConfigSource(modId, branch);
        ConfigRegistry.register(this);
        resolveContainer(this);
        if (isEmpty()) {
            logger.warn("Empty config: " + modId + " " + Arrays.toString(branch));
            return;
        }
        load();
        if (saveOnExit) {
            Runtime.getRuntime().addShutdownHook(new Thread(this::save));
        }
    }

    protected Config(String modId, boolean saveOnExit) {
        this(modId, new String[0], saveOnExit);
    }

    public ModMetadata getMod() {
        return FabricLoader.getInstance().getModContainer(source.getModId()).get().getMetadata();
    }

    @Override
    public TranslationKey getTranslation() {
        return getTranslation(false);
    }

    @Environment(EnvType.CLIENT)
    public TranslationKey getTranslation(boolean includeBranch) {
        if (translation == null) {
            translation = TranslationKey.from(this);
        }
        if (includeBranch) {
            return translation.append(source.getBranch());
        }
        return translation;
    }

    private void load() {
        source.load(this);
    }

    public void save() {
        source.save(this);
    }

}
