package org.stefanovietch.magical_shapes.menus;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.Rotations;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.awt.SystemColor.window;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;

public class DrawWidget extends AbstractWidget {
    List<int[]> points;

    public DrawWidget(int x, int y, int width, int height, Component p_93633_) {
        super(x, y, width, height, p_93633_);
        points = new ArrayList<>();
        points.add(new int[]{4,5});
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
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            int local_x = Mth.floor(x) - getX();
            int local_y = Mth.floor(y) - getY();
            points.add(new int[]{local_x, local_y});
        }
        return super.mouseDragged(x, y, button, dragX, dragY);
    }

    public List<int[]> getDrawing() {
        return points;
    }
}
