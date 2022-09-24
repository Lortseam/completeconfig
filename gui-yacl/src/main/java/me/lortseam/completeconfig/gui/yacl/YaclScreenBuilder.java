package me.lortseam.completeconfig.gui.yacl;

import com.google.common.collect.Lists;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.gui.controllers.BooleanController;
import me.lortseam.completeconfig.CompleteConfig;
import me.lortseam.completeconfig.data.BooleanEntry;
import me.lortseam.completeconfig.data.Config;
import me.lortseam.completeconfig.gui.ConfigScreenBuilder;
import me.lortseam.completeconfig.gui.GuiProvider;
import net.minecraft.client.gui.screen.Screen;

import java.util.Collection;
import java.util.List;

/**
 * A screen builder based on the YetAnotherConfigLib library.
 */
public final class YaclScreenBuilder extends ConfigScreenBuilder<ControllerFunction<?>> {

    private static final List<GuiProvider<ControllerFunction<?>>> globalProviders = Lists.newArrayList(
            GuiProvider.create(BooleanEntry.class, entry -> (Option<Boolean> option) -> new BooleanController(
                    option,
                    entry.getValueTextSupplier(),
                    false
            ), boolean.class, Boolean.class)
    );

    static {
        for (Collection<GuiProvider<ControllerFunction<?>>> providers : CompleteConfig.collectExtensions(YaclGuiExtension.class, YaclGuiExtension::getProviders)) {
            globalProviders.addAll(providers);
        }
    }

    public YaclScreenBuilder() {
        super(globalProviders);
    }

    @Override
    public Screen build(Screen parentScreen, Config config) {

    }

}
