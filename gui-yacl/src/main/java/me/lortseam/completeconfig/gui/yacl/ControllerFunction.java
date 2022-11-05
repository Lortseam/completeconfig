package me.lortseam.completeconfig.gui.yacl;

import dev.isxander.yacl.api.Controller;
import dev.isxander.yacl.api.Option;

import java.util.function.Function;

@FunctionalInterface
public interface ControllerFunction<T> extends Function<Option<T>, Controller<T>> {}
