package me.lortseam.completeconfig.gui.yacl.controller;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
public class ListController<T> implements Controller<List<T>> {

    public static <T> ControllerBuilder<List<T>> createBuilder(Function<Option<T>, ControllerBuilder<T>> elementControllerBuilder) {
        return new ControllerBuilder<List<T>>() {

            @Override
            public Controller<List<T>> build() {
                return new ListController<>(elementControllerBuilder);
            }

        };
    }

    @Getter
    private final Function<Option<T>, ControllerBuilder<T>> elementControllerBuilder;
    @Getter
    private final T initialValue;

    private ListController(Function<Option<T>, ControllerBuilder<T>> elementControllerBuilder) {
        this(elementControllerBuilder, null);
    }

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
