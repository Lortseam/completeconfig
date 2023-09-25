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
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@ConfigEntries(includeAll = true)
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

    @Override
    public @Nullable Collection<ConfigContainer> getTransitives() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            return List.of(new ClientConfigEntries());
        }
        return null;
    }

    @Transitive
    @ConfigEntries(includeAll = true)
    private static class DataTypes implements ConfigGroup {

        private boolean bool;

        private int anInt;
        private long aLong;
        private float aFloat;
        private double aDouble;

        @ConfigEntry.BoundedInteger(min = 0, max = 10)
        private int boundedInt;
        @ConfigEntry.BoundedLong(min = -10, max = 10)
        private long boundedLong;
        @ConfigEntry.BoundedFloat(min = 0, max = 10)
        private float boundedFloat;
        @ConfigEntry.BoundedDouble(min = -10, max = 10)
        private double boundedDouble;

        @ConfigEntry.BoundedInteger(min = 0, max = 10)
        @ConfigEntry.Slider
        private int intSlider;
        @ConfigEntry.BoundedLong(min = -10, max = 10)
        @ConfigEntry.Slider
        private long longSlider;

        private String string = "";
        private AnEnum anEnum = AnEnum.FOO;
        private java.awt.Color awtColor = new java.awt.Color(255, 0, 0);

        private List<String> list = Arrays.asList("First entry", "Second entry");

        @Override
        public Collection<ConfigContainer> getTransitives() {
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                return List.of(
                        switch (TestModClient.getScreenBuilderType()) {
                            case CLOTH_CONFIG -> new ClothConfigDataTypes();
                            case YACL -> new YaclDataTypes();
                        }
                );
            }
            return List.of(new ClothConfigDataTypes(), new YaclDataTypes());
        }

        @ConfigEntries(includeAll = true)
        private static class ClothConfigDataTypes implements ConfigContainer {

            @ConfigEntry.Dropdown
            private AnEnum enumDropdown = AnEnum.FOO;
            private String[] array = new String[0];
            private Color color = Color.ofRGB(0, 255, 0);

        }

        @ConfigEntries(includeAll = true)
        private static class YaclDataTypes implements ConfigContainer {

            @ConfigEntry.Checkbox
            private boolean checkboxBoolean;

            @ConfigEntry.BoundedFloat(min = 0, max = 10)
            @ConfigEntry.Slider
            private float floatSlider;
            @ConfigEntry.BoundedDouble(min = -10, max = 10)
            @ConfigEntry.Slider
            private double doubleSlider;

            @ConfigEntry.BoundedInteger(min = 0, max = 10)
            @ConfigEntry.Slider
            @ConfigEntry.IntegerSliderInterval(2)
            private int intIntervalSlider;
            @ConfigEntry.BoundedLong(min = -10, max = 10)
            @ConfigEntry.Slider
            @ConfigEntry.LongSliderInterval(2)
            private long longIntervalSlider;
            @ConfigEntry.BoundedFloat(min = 0, max = 10)
            @ConfigEntry.Slider
            @ConfigEntry.FloatSliderInterval(0.5f)
            private float floatIntervalSlider;
            @ConfigEntry.BoundedDouble(min = -10, max = 10)
            @ConfigEntry.Slider
            @ConfigEntry.DoubleSliderInterval(0.5)
            private double doubleIntervalSlider;

        }

    }

    public enum AnEnum {
        FOO, BAR, BAZ
    }

}
