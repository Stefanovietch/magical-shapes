package org.stefanovietch.magical_shapes.menus;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.ArrayList;
import java.util.List;

public class SpellDrawing {
    private final List<Drawing> drawings = new ArrayList<>();
    private String name;
    private int ID;

    public SpellDrawing(String name) {
        this.name = name;
        this.ID = name.hashCode();
        for (int i = 0; i < 10; i++) {
            drawings.add(new Drawing());
        }
    }

    public List<Drawing> getDrawings() {
        return drawings;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        this.ID = name.hashCode();
    }

    public int getID() {
        return ID;
    }

    public Drawing getDrawing(int index) {
        return drawings.get(index); // 0..9
    }

    // Save spell to NBT
    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Name", name);

        ListTag list = new ListTag();
        for (Drawing drawing : drawings) {
            list.add(drawing.save());
        }

        tag.put("Drawings", list);
        return tag;
    }

    // Load spell from NBT
    public static SpellDrawing load(CompoundTag tag) {
        SpellDrawing spell = new SpellDrawing(tag.getString("Name"));
        ListTag list = tag.getList("Drawings", 10); // 10 = CompoundTag

        spell.drawings.clear();
        for (int i = 0; i < list.size(); i++) {
            spell.drawings.add(Drawing.load(list.getCompound(i)));
        }

        return spell;
    }
}