package me.lortseam.completeconfig.example.config;

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

        private TextColor textColor = TextColor.fromFormatting(Formatting.GREEN);
        private InputUtil.Key key = InputUtil.UNKNOWN_KEY;
        private ModifierKeyCode modifierKeyCode = ModifierKeyCode.unknown();

    }

}
