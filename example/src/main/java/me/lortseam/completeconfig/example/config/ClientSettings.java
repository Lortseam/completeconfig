package me.lortseam.completeconfig.example.config;

import me.lortseam.completeconfig.api.ConfigEntries;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigGroup;
import me.shedaniel.clothconfig2.api.ModifierKeyCode;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

public class ClientSettings extends Settings {

    @Transitive
    @ConfigEntries
    private static final class ClientDataTypes implements ConfigGroup {

        private TextColor textColor = TextColor.fromFormatting(Formatting.GREEN);
        private InputUtil.Key key = InputUtil.UNKNOWN_KEY;
        private ModifierKeyCode modifierKeyCode = ModifierKeyCode.unknown();

    }

    @Transitive
    @ConfigEntries
    public static class Tooltips implements ConfigGroup {

        private boolean defaultOneLine;

        private boolean defaultMultiLine;

        @ConfigEntry(tooltipTranslationKeys = {"customTooltipLine1", "customTooltipLine2"})
        private boolean custom;

    }

}
