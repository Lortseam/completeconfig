package me.lortseam.completeconfig.testmod.gui;

import me.lortseam.completeconfig.gui.ConfigScreenBuilder;
import me.lortseam.completeconfig.gui.cloth.ClothConfigScreenBuilder;

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
            // TODO
            //return new CoatScreenBuilder();
            return null;
        }
    };

    public abstract ConfigScreenBuilder<?> create();

}
