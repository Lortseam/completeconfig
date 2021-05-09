package me.lortseam.completeconfig.data;

import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.data.text.TranslationKey;
import net.minecraft.text.TextColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class EntryTest implements ConfigContainer {

    private boolean booleanWithoutAnnotation;
    @ConfigEntry.Boolean
    private boolean booleanWithAnnotation;
    @ConfigEntry.BoundedInteger
    private int boundedInt;
    @ConfigEntry.BoundedInteger
    @ConfigEntry.Slider
    private int sliderInt;
    @ConfigEntry.BoundedLong
    private long boundedLong;
    @ConfigEntry.BoundedLong
    @ConfigEntry.Slider
    private long sliderLong;
    @ConfigEntry.BoundedFloat
    private float boundedFloat;
    @ConfigEntry.BoundedDouble
    private double boundedDouble;
    private AnEnum anEnum = AnEnum.FOO;
    @ConfigEntry.Dropdown
    private AnEnum dropdown = AnEnum.FOO;
    @ConfigEntry.Color(alphaMode = true)
    private int color;
    private TextColor textColor = TextColor.fromRgb(0);

    private <E extends Entry<?>> void assertTransformation(String fieldName, Class<E> entryType) {
        try {
            assertTrue(entryType.isInstance(Entry.of(getClass().getDeclaredField((fieldName)), this, mock(TranslationKey.class))));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void of_transformDefaults() {
        assertTransformation("booleanWithoutAnnotation", BooleanEntry.class);
        assertTransformation("booleanWithAnnotation", BooleanEntry.class);
        assertTransformation("boundedInt", BoundedEntry.class);
        assertTransformation("sliderInt", SliderEntry.class);
        assertTransformation("boundedLong", BoundedEntry.class);
        assertTransformation("sliderLong", SliderEntry.class);
        assertTransformation("boundedFloat", BoundedEntry.class);
        assertTransformation("boundedDouble", BoundedEntry.class);
        assertTransformation("anEnum", EnumEntry.class);
        assertTransformation("dropdown", DropdownEntry.class);
        assertTransformation("color", ColorEntry.class);
        assertTransformation("textColor", ColorEntry.class);
    }

    private enum AnEnum {
        FOO
    }

}
