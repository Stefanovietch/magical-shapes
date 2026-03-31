package org.stefanovietch.magical_shapes.menus;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.ArrayList;
import java.util.List;

public class SpellProject {
    private final List<SpellDrawing> spellDrawings = new ArrayList<>();
    private String name;

    public SpellProject(String name) { this.name = name; }

    public List<SpellDrawing> getSpellDrawings() { return spellDrawings; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public void addSpellDrawing(SpellDrawing spellDrawing) {
        spellDrawings.add(spellDrawing);
    }

    // Save project to NBT
    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Name", name);

        ListTag spellList = new ListTag();
        for (SpellDrawing spell : spellDrawings) {
            spellList.add(spell.save());
        }

        tag.put("Spells", spellList);
        return tag;
    }

    // Load project from NBT
    public static SpellProject load(CompoundTag tag) {
        SpellProject project = new SpellProject(tag.getString("Name"));
        ListTag spellList = tag.getList("Spells", 10);

        for (int i = 0; i < spellList.size(); i++) {
            project.spellDrawings.add(SpellDrawing.load(spellList.getCompound(i)));
        }

        return project;
    }
}