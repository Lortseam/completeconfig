package me.lortseam.completeconfig.example.config;

import me.lortseam.completeconfig.api.ConfigEntries;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigGroup;
import me.lortseam.completeconfig.data.Config;
import me.lortseam.completeconfig.example.ExampleMod;
import me.shedaniel.math.Color;

import java.util.Arrays;
import java.util.List;

public class Settings extends Config {

    public Settings() {
        super(ExampleMod.MOD_ID);
    }

    @Transitive
    @ConfigEntries
    public static class DataTypes implements ConfigGroup {

        private boolean bool;
        @ConfigEntry.BoundedInteger(min = 0, max = 10)
        @ConfigEntry.Slider
        private int boundedInt;
        private long aLong;
        @ConfigEntry.BoundedLong(min = 0, max = 100)
        @ConfigEntry.Slider
        private long boundedLong;
        private float aFloat;
        private double aDouble;
        private String string = "";
        @ConfigEntry.Dropdown
        private AnEnum anEnum = AnEnum.FOO;
        private List<String> list = Arrays.asList("First entry", "Second entry");
        private String[] array = new String[0];
        private Color color = Color.ofRGB(0, 255, 0);

    }

    public enum AnEnum {
        FOO, BAR, BAZ
    }

}
