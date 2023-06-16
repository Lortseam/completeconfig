package me.lortseam.completeconfig.gui.yacl;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;

import java.util.function.Function;

@FunctionalInterface
public interface ControllerFunction<T> extends Function<Option<T>, ControllerBuilder<T>> {}
