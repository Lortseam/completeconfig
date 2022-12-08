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
    COAT() {
        @Override
        public ConfigScreenBuilder<?> create() {
//            return new CoatScreenBuilder();
            return null;
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
