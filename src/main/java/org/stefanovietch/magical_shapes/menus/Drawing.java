package org.stefanovietch.magical_shapes.menus;

import net.minecraft.client.gui.components.Button;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import org.joml.Vector2f;
import org.stefanovietch.magical_shapes.ml.AngleList;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Drawing {
    private final Set<Point> points = new HashSet<>();

    public DrawWidget drawWidget = new DrawWidget(20, 20, 200, 100,Component.literal("draw"), this);
    public Button eraseButton = Button.builder(Component.literal(this.drawWidget.draw ? "Drawer" : "Eraser"), b -> {
                this.drawWidget.draw = !this.drawWidget.draw;
                b.setMessage(Component.literal(this.drawWidget.draw ? "Drawer" : "Eraser"));
            })
            .bounds(20,  20, 50, 20) // x, y, width, height
            .build();
    public Button clearButton = Button.builder(Component.literal("Clear"), b -> {
                points.clear();
            })
            .bounds(20,  20, 50, 20) // x, y, width, height
            .build();

    public void addPoint(int x, int y) {
        points.add(new Point(x, y));
    }
    public Set<Point> getPoints() { return points; }
    public int length() {return points.size();}

    public void removePoints(int x, int y) {
        points.removeIf(p ->
                Math.abs(p.x - x) <= 2 && Math.abs(p.y - y) <= 2);
    }

    public void setPosition(int x, int y) {
        drawWidget.setPosition(x, y);
        eraseButton.setPosition(x, y + 110);
        clearButton.setPosition(x + 60, y + 110);
    }

    // Save drawing to NBT
    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();

        for (Point point : points) {
            list.add(new IntArrayTag(new int[]{point.x, point.y}));
        }

        tag.put("Points", list);
        return tag;
    }

    // Load drawing from NBT
    public static Drawing load(CompoundTag tag) {
        Drawing drawing = new Drawing();
        ListTag list = tag.getList("Points", 11); // 11 = int array

        for (int i = 0; i < list.size(); i++) {
            int[] point = list.getIntArray(i);
            drawing.addPoint(point[0], point[1]);
        }

        return drawing;
    }

    public AngleList toAngleList() {
        return new AngleList(points.stream()
                .map(p -> new Vector2f(p.x, p.y))
                .toList());
    }
}
