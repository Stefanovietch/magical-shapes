package org.stefanovietch.magical_shapes.menus;


import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.stefanovietch.magical_shapes.Magical_shapes;

import static org.stefanovietch.magical_shapes.menus.ProjectStorage.PROJECTS;

public class MainScreen extends Screen {
    PlainTextButton newProjectButton;

    public MainScreen() {
        super(Component.literal("Main Menu"));
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(
                Button.builder(Component.literal("Create Project"), b -> {
                            SpellProject new_project = new SpellProject("");
                            PROJECTS.add(new_project);
                            this.minecraft.setScreen(new SpellProjectScreen(new_project));
                        })
                        .bounds(20, 20, 120, 20) // x, y, width, height
                        .build()
        );
        for (int i = 0; i < ProjectStorage.PROJECTS.size(); i++) {
            SpellProject project = ProjectStorage.PROJECTS.get(i);
            this.addRenderableWidget(
                    Button.builder(Component.literal("Open Project: " + project.getName()), b -> {
                                this.minecraft.setScreen(new SpellProjectScreen(project));
                            })
                            .bounds(20, 40 + i * 25, 120, 20) // x, y, width, height
                            .build()
            );
        }
    }

    @Override
    public void tick() {
        super.tick();

        // Add ticking logic for EditBox in editBox
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Background is typically rendered first
        this.renderBackground(graphics);

        // Render things here before widgets (background textures)

        // Then the widgets if this is a direct child of the Screen
        super.render(graphics, mouseX, mouseY, partialTick);

        // Render things after widgets (tooltips)
    }

    @Override
    public void onClose() {
        // Stop any handlers here
        // Call last in case it interferes with the override
        super.onClose();
    }

    @Override
    public void removed() {
        // Reset initial states here
        Magical_shapes.save();
        // Call last in case it interferes with the override
        super.removed();
    }
}
