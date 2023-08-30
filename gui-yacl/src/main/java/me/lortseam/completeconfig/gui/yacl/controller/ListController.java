package me.lortseam.completeconfig.gui.yacl.controller;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lortseam.completeconfig.gui.yacl.ControllerFunction;
import net.minecraft.text.Text;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ListController<T> implements Controller<List<T>> {

    public static <T> ControllerBuilder<List<T>> createBuilder(ControllerFunction<T> elementControllerBuilder, T initialValue) {
        return () -> new ListController<>(elementControllerBuilder, initialValue);
    }

    public static <T> ControllerBuilder<List<T>> createBuilder(ControllerFunction<T> elementControllerBuilder) {
        return createBuilder(elementControllerBuilder, null);
    }

    @Getter
    private final ControllerFunction<T> elementControllerBuilder;
    @Getter
    private final T initialValue;

    @Override
    public Option<List<T>> option() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Text formatValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
        throw new UnsupportedOperationException();
    }

}
