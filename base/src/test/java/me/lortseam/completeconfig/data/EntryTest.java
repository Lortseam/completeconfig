package me.lortseam.completeconfig.data;

import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.text.TranslationKey;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TextColor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.spongepowered.configurate.CommentedConfigurationNode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class EntryTest implements ConfigContainer {

    private static final String MOD_ID = "test",
            SUB_KEY = "subKey",
            CUSTOM_ID = "customId",
            CUSTOM_KEY = "customKey",
            CUSTOM_DESCRIPTION_KEY = "customDescriptionKey",
            COMMENT = "Comment";
    private static final Config CONFIG;
    private static final Parent PARENT;
    private static final boolean REQUIRES_RESTART = true;

    static {
        ModMetadata modMetadata = mock(ModMetadata.class);
        when(modMetadata.getId()).thenReturn(MOD_ID);
        CONFIG = mock(Config.class);
        when(CONFIG.getMod()).thenReturn(modMetadata);
        var rootTranslation = new TranslationKey(CONFIG);
        when(CONFIG.getTranslation(false)).thenReturn(rootTranslation);
        var parentTranslation = rootTranslation.append(SUB_KEY);
        PARENT = new Parent() {
            @Override
            public TranslationKey getNameTranslation() {
                return parentTranslation;
            }

            @Override
            Config getRoot() {
                return CONFIG;
            }
        };
    }

    private static <E extends Entry<?>> void assertEntryType(Entry<?> entry, Class<E> entryType) {
        assertTrue(entryType.isInstance(entry));
    }

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
    @ConfigEntry.BoundedFloat
    @ConfigEntry.Slider
    private float sliderFloat;
    @ConfigEntry.BoundedDouble
    private double boundedDouble;
    @ConfigEntry.BoundedDouble
    @ConfigEntry.Slider
    private double sliderDouble;
    private AnEnum anEnum = AnEnum.FOO;
    @ConfigEntry.Dropdown
    private AnEnum dropdown = AnEnum.FOO;
    @ConfigEntry.Color(alphaMode = true)
    private int color;
    private TextColor textColor = TextColor.fromRgb(0);

    @ConfigEntry(requiresRestart = REQUIRES_RESTART, comment = COMMENT)
    private int field = 123456789;
    private Entry<?> entry = of("field");
    @ConfigEntry(CUSTOM_ID)
    private boolean customIdField;
    private Entry<?> customIdEntry = of("customIdField");
    @ConfigEntry(nameKey = CUSTOM_KEY, descriptionKey = CUSTOM_DESCRIPTION_KEY)
    private boolean customKeyField;
    private Entry<?> customKeyEntry = of("customKeyField");

    private Entry<?> of(String fieldName) {
        try {
            return Entry.create(CONFIG, PARENT, getClass().getDeclaredField((fieldName)), this);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void of_transformTypes() {
        assertEntryType(of("booleanWithoutAnnotation"), BooleanEntry.class);
        assertEntryType(of("booleanWithAnnotation"), BooleanEntry.class);
        assertEntryType(of("boundedInt"), BoundedEntry.class);
        assertEntryType(of("sliderInt"), SliderEntry.class);
        assertEntryType(of("boundedLong"), BoundedEntry.class);
        assertEntryType(of("sliderLong"), SliderEntry.class);
        assertEntryType(of("boundedFloat"), BoundedEntry.class);
        assertEntryType(of("sliderFloat"), SliderEntry.class);
        assertEntryType(of("boundedDouble"), BoundedEntry.class);
        assertEntryType(of("sliderDouble"), SliderEntry.class);
        assertEntryType(of("anEnum"), EnumEntry.class);
        assertEntryType(of("dropdown"), DropdownEntry.class);
        assertEntryType(of("color"), ColorEntry.class);
    }

    @Test
    @EnabledIfSystemProperty(named = "fabric.dli.env", matches = "client")
    public void of_transformClientTypes() {
        assertEntryType(of("textColor"), ColorEntry.class);
    }

    @Test
    public void of_transformProperties() {
        assertEquals("field", entry.getId());
        assertEquals(CUSTOM_ID, customIdEntry.getId());
        assertEquals(field, entry.getDefaultValue());
        assertTrue(entry.requiresRestart());
        CommentedConfigurationNode node = CommentedConfigurationNode.root();
        entry.fetch(node);
        assertEquals(COMMENT, node.comment());
    }

    @Test
    @EnabledIfSystemProperty(named = "fabric.dli.env", matches = "client")
    public void of_transformClientProperties() {
        // Key
        assertEquals("config." + MOD_ID + "." + SUB_KEY + ".field", entry.getNameTranslation().toString());
        assertEquals("config." + MOD_ID + "." + SUB_KEY + "." + CUSTOM_ID, customIdEntry.getNameTranslation().toString());
        assertEquals("config." + MOD_ID + "." + CUSTOM_KEY, customKeyEntry.getNameTranslation().toString());

        // Description key
        try (var i18n = mockStatic(I18n.class)) {
            var defaultDescriptionKey = "config." + MOD_ID + "." + SUB_KEY + ".field.description";
            i18n.when(() -> I18n.hasTranslation(defaultDescriptionKey)).thenReturn(true);
            assertEquals(defaultDescriptionKey, entry.getDescriptionTranslation().get().toString());

            var customDescriptionKey = "config." + MOD_ID + "." + CUSTOM_DESCRIPTION_KEY;
            i18n.when(() -> I18n.hasTranslation(customDescriptionKey)).thenReturn(true);
            assertEquals(customDescriptionKey, customKeyEntry.getDescriptionTranslation().get().toString());
        }
    }

    private enum AnEnum {
        FOO
    }

}
