package me.lortseam.completeconfig.testmod.gui;

import me.lortseam.completeconfig.gui.ConfigScreenBuilder;
import me.lortseam.completeconfig.gui.cloth.ClothConfigScreenBuilder;
import me.lortseam.completeconfig.gui.coat.CoatScreenBuilder;

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
