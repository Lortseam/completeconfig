package me.lortseam.completeconfig.gui.coat.input;

import de.siphalor.coat.input.ConfigInput;
import de.siphalor.coat.input.InputChangeListener;
import me.lortseam.completeconfig.mixin.ClickableWidgetAccess;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.function.Function;

public class ButtonConfigInput<T> implements ConfigInput<T> {

    private final CyclingButtonWidget<T> button;
    private InputChangeListener<T> changeListener;

    public ButtonConfigInput(T[] values, T initialValue, Function<T, Text> valueTextSupplier) {
        button = CyclingButtonWidget.builder(valueTextSupplier)
                .values(values)
                .initially(initialValue)
                .omitKeyText()
                .build(0, 0, 100, 20, null, (button, value) -> {
                    if(changeListener == null) return;
                    changeListener.inputChanged(value);
                });
    }

    @Override
    public int getHeight() {
        return button.getHeight();
    }

    @Override
    public T getValue() {
        return button.getValue();
    }

    @Override
    public void setValue(T value) {
        button.setValue(value);
        if (changeListener != null) {
            changeListener.inputChanged(value);
        }
    }

    @Override
    public void setChangeListener(InputChangeListener<T> changeListener) {
        this.changeListener = changeListener;
    }

    @Override
    public void setFocused(boolean focused) {
        ((ClickableWidgetAccess) button).invokeSetFocused(focused);
    }

    @Override
    public void render(MatrixStack matrices, int x, int y, int width, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        button.x = x;
        button.y = y;
        button.setWidth(width);
        button.render(matrices, mouseX, mouseY, tickDelta);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {

    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return button.isMouseOver(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        return button.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return button.mouseScrolled(mouseX, mouseY, amount);
    }

}
