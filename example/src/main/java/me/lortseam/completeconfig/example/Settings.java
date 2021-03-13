package me.lortseam.completeconfig.example;

import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigEntries;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigGroup;

import java.util.Arrays;
import java.util.List;

public final class Settings implements ConfigContainer {

    @Transitive
    @ConfigEntries
    public static class DataTypes implements ConfigGroup {

        private boolean bool;
        private int integer;
        @ConfigEntry.BoundedInteger(min = 0, max = 10)
        private int boundedInt;
        private long aLong;
        @ConfigEntry.BoundedLong(min = 0, max = 100)
        private long boundedLong;
        private float aFloat;
        private double aDouble;
        private String string = "";
        private AnEnum anEnum = AnEnum.FOO;
        private List<String> list = Arrays.asList("First entry", "Second entry");

    }

    public enum AnEnum {
        FOO, BAR, BAZ
    }

    @Transitive
    @ConfigEntries
    public static class Tooltips implements ConfigGroup {

        @ConfigEntry
        private boolean defaultOneLine;

        @ConfigEntry
        private boolean defaultMultiLine;

        @ConfigEntry(tooltipTranslationKeys = {"customTooltipLine1", "customTooltipLine2"})
        private boolean custom;

    }

}
