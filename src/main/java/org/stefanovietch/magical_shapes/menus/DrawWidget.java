package org.stefanovietch.magical_shapes.menus;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class DrawWidget extends AbstractWidget {
    List<int[]> points;
    boolean draw = true;

    public DrawWidget(int x, int y, int width, int height, Component p_93633_) {
        super(x, y, width, height, p_93633_);
        points = new ArrayList<>();
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int i1, float v) {
        int f = this.isFocused() ? -1 : -6250336;
        guiGraphics.fill(this.getX() - 1, this.getY() - 1, this.getX() + this.width + 1, this.getY() + this.height + 1, f);
        guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, -16777216);
        for (int[] entry : points){
            guiGraphics.fill(this.getX() + entry[0]-1,this.getY() + entry[1]-1,this.getX() + entry[0]+1,this.getY() + entry[1]+1, -1);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public boolean mouseDragged(double x, double y, int button, double dragX, double dragY) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && this.isFocused()) {
            int local_x = Mth.floor(x) - getX();
            int local_y = Mth.floor(y) - getY();
            if (0 < local_x && local_x < this.width && 0 < local_y && local_y < this.height) {
                if (draw) {
                    points.add(new int[]{local_x, local_y});
                } else {
                    points.removeIf(p ->
                            Math.abs(p[0] - local_x) <= 2 &&
                            Math.abs(p[1] - local_y) <= 2);
                }
            }
        }
        return super.mouseDragged(x, y, button, dragX, dragY);
    }

    public List<int[]> getDrawing() {
        return points;
    }
}
