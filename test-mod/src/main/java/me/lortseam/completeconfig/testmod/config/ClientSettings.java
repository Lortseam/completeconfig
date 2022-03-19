package me.lortseam.completeconfig.testmod.config;

import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigEntries;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigGroup;
import me.shedaniel.clothconfig2.api.ModifierKeyCode;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

@ConfigEntries
public class ClientSettings extends Settings {

    private boolean defaultDescription;
    @ConfigEntry(descriptionKey = "customDescriptionKey")
    private boolean customDescription;


    @Transitive
    @ConfigEntries
    private static final class ClientDataTypes implements ConfigGroup {

        @Override
        public ConfigContainer[] getTransitives() {
            if (Options.getScreenBuilderType() == Options.ScreenBuilderType.CLOTH_CONFIG) {
                return new ConfigContainer[] {new ClothConfigClientDataTypes()};
            }
            return new ConfigContainer[0];
        }

        @ConfigEntries
        public static class ClothConfigClientDataTypes implements ConfigContainer {

            private TextColor textColor = TextColor.fromFormatting(Formatting.GREEN);
            private InputUtil.Key key = InputUtil.UNKNOWN_KEY;
            private ModifierKeyCode modifierKeyCode = ModifierKeyCode.unknown();

        }

    }

}
