package org.stefanovietch.magical_shapes.menus;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class DrawWidget extends AbstractWidget {
    boolean draw = true;
    Drawing drawing;

    public DrawWidget(int x, int y, int width, int height, Component p_93633_, Drawing drawing) {
        super(x, y, width, height, p_93633_);
        this.drawing = drawing;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int i1, float v) {
        int f = this.isFocused() ? -1 : -6250336;
        guiGraphics.fill(this.getX() - 1, this.getY() - 1, this.getX() + this.width + 1, this.getY() + this.height + 1, f);
        guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, -16777216);
        for (Point entry : drawing.getPoints()){
            guiGraphics.fill(this.getX() + entry.x-1,this.getY() + entry.y-1,this.getX() + entry.x+1,this.getY() + entry.y+1, -1);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public boolean mouseDragged(double x, double y, int button, double dragX, double dragY) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && this.isFocused() && drawing.length() < 200) {
            int local_x = Mth.floor(x) - getX();
            int local_y = Mth.floor(y) - getY();
            if (0 < local_x && local_x < this.width && 0 < local_y && local_y < this.height) {
                if (draw) {
                    drawing.addPoint(local_x, local_y);
                } else {
                    drawing.removePoints(local_x, local_y);
                }
            }
        }
        return super.mouseDragged(x, y, button, dragX, dragY);
    }
}
