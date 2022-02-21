package me.lortseam.completeconfig.example.config;

import lombok.Getter;
import me.lortseam.completeconfig.api.ConfigEntries;
import me.lortseam.completeconfig.data.Config;
import me.lortseam.completeconfig.example.ExampleMod;
import me.lortseam.completeconfig.gui.ConfigScreenBuilder;
import me.lortseam.completeconfig.gui.cloth.ClothConfigScreenBuilder;
import me.lortseam.completeconfig.gui.coat.CoatScreenBuilder;

@ConfigEntries
public class Options extends Config {

    @Getter
    private static ScreenBuilderType screenBuilderType = ScreenBuilderType.CLOTH_CONFIG;

    public Options() {
        super(ExampleMod.MOD_ID, new String[]{"options"});
    }

    public enum ScreenBuilderType {

        CLOTH_CONFIG() {
            @Override
            public ConfigScreenBuilder<?> create() {
                return new ClothConfigScreenBuilder();
            }
        },
        COAT() {
            @Override
            public ConfigScreenBuilder<?> create() {
                return new CoatScreenBuilder();
            }
        };

        public abstract ConfigScreenBuilder<?> create();

    }

}
