package me.lortseam.completeconfig.gui.yacl.controller;

import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.utils.Dimension;
import dev.isxander.yacl.gui.AbstractWidget;
import dev.isxander.yacl.gui.YACLScreen;
import dev.isxander.yacl.gui.controllers.string.IStringController;
import dev.isxander.yacl.gui.controllers.string.StringControllerElement;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NumberController<T extends Number> implements IStringController<T> {

    private final Option<T> option;
    private String pendingString;

    @Override
    public Option<T> option() {
        return option;
    }

    @Override
    public String getString() {
        if (pendingString == null) {
            pendingString = option.pendingValue().toString();
        }
        return pendingString;
    }

    @Override
    public void setFromString(String string) {
        pendingString = string;
        var type = option.typeClass();
        Number value;
        try {
            if (type == int.class || type == Integer.class) {
                value = Integer.parseInt(string);
            } else if(type == long.class || type == Long.class) {
                value = Long.parseLong(string);
            } else if(type == float.class || type == Float.class) {
                value = Float.parseFloat(string);
            } else if(type == double.class || type == Double.class) {
                value = Double.parseDouble(string);
            } else {
                throw new RuntimeException("Number class " + type.getSimpleName() + " is not supported");
            }
            option.requestSet((T) value);
        } catch (NumberFormatException ignore) {}
    }

    @Override
    public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
        return new StringControllerElement(this, screen, widgetDimension);
    }

}
