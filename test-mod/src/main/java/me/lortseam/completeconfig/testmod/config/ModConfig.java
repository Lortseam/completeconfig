package me.lortseam.completeconfig.testmod.config;

import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigEntries;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigGroup;
import me.lortseam.completeconfig.data.Config;
import me.lortseam.completeconfig.data.ConfigOptions;
import me.lortseam.completeconfig.testmod.TestMod;
import me.lortseam.completeconfig.testmod.TestModClient;
import me.shedaniel.math.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.util.Arrays;
import java.util.List;

@ConfigEntries
public class ModConfig extends Config {

    public ModConfig() {
        super(ConfigOptions
                .mod(TestMod.MOD_ID)
                .fileHeader("This is a test config")
        );
    }

    @ConfigEntry(comment = "This is a test comment")
    private boolean comment;
    @ConfigEntry(requiresRestart = true)
    private boolean requiresRestart;

    @Transitive
    @ConfigEntries
    private static class DataTypes implements ConfigGroup {

        private boolean bool;
        private int anInt;
        @ConfigEntry.BoundedInteger(min = 0, max = 10)
        private int boundedInt;
        @ConfigEntry.BoundedInteger(min = 0, max = 10)
        @ConfigEntry.Slider
        private int intSlider;
        private long aLong;
        @ConfigEntry.BoundedLong(min = -10, max = 10)
        private long boundedLong;
        @ConfigEntry.BoundedLong(min = -10, max = 10)
        @ConfigEntry.Slider
        private long longSlider;
        private float aFloat;
        @ConfigEntry.BoundedFloat(min = 0, max = 10)
        private float boundedFloat;
        private double aDouble;
        @ConfigEntry.BoundedDouble(min = -10, max = 10)
        private double boundedDouble;
        private String string = "";
        private AnEnum anEnum = AnEnum.FOO;

        @Override
        public ConfigContainer[] getTransitives() {
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                return new ConfigContainer[] {
                        switch (TestModClient.getScreenBuilderType()) {
                            case CLOTH_CONFIG -> new ClothConfigDataTypes();
                            case COAT -> new CoatDataTypes();
                        }
                };
            }
            return new ConfigContainer[] {new ClothConfigDataTypes(), new CoatDataTypes()};
        }

        @ConfigEntries
        private static class ClothConfigDataTypes implements ConfigContainer {

            @ConfigEntry.Dropdown
            private AnEnum enumDropdown = AnEnum.FOO;
            private List<String> list = Arrays.asList("First entry", "Second entry");
            private String[] array = new String[0];
            private Color color = Color.ofRGB(0, 255, 0);

        }

        @ConfigEntries
        private static class CoatDataTypes implements ConfigContainer {

            @ConfigEntry.Checkbox
            private boolean checkboxBoolean;
            @ConfigEntry.BoundedFloat(min = 0, max = 10)
            @ConfigEntry.Slider
            private float floatSlider;
            @ConfigEntry.BoundedDouble(min = -10, max = 10)
            @ConfigEntry.Slider
            private double doubleSlider;

        }

    }

    public enum AnEnum {
        FOO, BAR, BAZ
    }

}
