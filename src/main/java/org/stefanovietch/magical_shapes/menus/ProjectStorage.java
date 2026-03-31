package org.stefanovietch.magical_shapes.menus;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.ArrayList;
import java.util.List;

public class ProjectStorage {
    public static final List<SpellProject> PROJECTS = new ArrayList<>();

    // Save all projects
    public static CompoundTag saveAll() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();

        for (SpellProject project : PROJECTS) {
            list.add(project.save());
        }

        tag.put("Projects", list);
        return tag;
    }

    public static void loadAll(CompoundTag tag) {
        PROJECTS.clear();
        ListTag list = tag.getList("Projects", 10);

        for (int i = 0; i < list.size(); i++) {
            PROJECTS.add(SpellProject.load(list.getCompound(i)));
        }
    }
}