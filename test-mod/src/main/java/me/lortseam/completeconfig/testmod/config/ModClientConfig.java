package me.lortseam.completeconfig.testmod.config;

import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigEntries;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigGroup;
import me.lortseam.completeconfig.testmod.TestModClient;
import me.lortseam.completeconfig.testmod.gui.ScreenBuilderType;
import me.shedaniel.clothconfig2.api.ModifierKeyCode;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@ConfigEntries
public class ModClientConfig extends ModConfig {

    private boolean defaultDescription;
    @ConfigEntry(descriptionKey = "customDescriptionKey")
    private boolean customDescription;

    @Transitive
    @ConfigEntries
    private static final class ClientDataTypes implements ConfigGroup {

        @Override
        public Collection<ConfigContainer> getTransitives() {
            if (TestModClient.getScreenBuilderType() == ScreenBuilderType.CLOTH_CONFIG) {
                return List.of(new ClothConfigClientDataTypes());
            }
            return null;
        }

        @ConfigEntries
        public static class ClothConfigClientDataTypes implements ConfigContainer {

            private TextColor textColor = TextColor.fromFormatting(Formatting.GREEN);
            private InputUtil.Key key = InputUtil.UNKNOWN_KEY;
            private ModifierKeyCode modifierKeyCode = ModifierKeyCode.unknown();

        }

    }

}
