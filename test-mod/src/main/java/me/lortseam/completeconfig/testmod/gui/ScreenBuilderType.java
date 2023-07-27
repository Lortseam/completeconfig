package me.lortseam.completeconfig.testmod.gui;

import me.lortseam.completeconfig.gui.ConfigScreenBuilder;
import me.lortseam.completeconfig.gui.cloth.ClothConfigScreenBuilder;
import me.lortseam.completeconfig.gui.yacl.YaclScreenBuilder;

public enum ScreenBuilderType {

    CLOTH_CONFIG() {
        @Override
        public ConfigScreenBuilder<?> create() {
            return new ClothConfigScreenBuilder();
        }
    },
    YACL() {
        @Override
        public ConfigScreenBuilder<?> create() {
            return new YaclScreenBuilder();
        }
    };

    public abstract ConfigScreenBuilder<?> create();

}
