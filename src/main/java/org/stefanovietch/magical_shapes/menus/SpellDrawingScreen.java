package org.stefanovietch.magical_shapes.menus;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.stefanovietch.magical_shapes.Magical_shapes;

public class SpellDrawingScreen extends Screen {
    private final SpellDrawing spellDrawing;
    private final SpellProject currentProject;
    private EditBox nameEditor;


    public SpellDrawingScreen(SpellDrawing spellDrawing, SpellProject spellProject) {
        super(Component.literal(spellDrawing.getName()));
        this.spellDrawing = spellDrawing;
        this.currentProject = spellProject;
    }

    @Override
    protected void init() {
        super.init();
        assert this.minecraft != null;
        // Add widgets and precomputed values
        this.nameEditor = new EditBox(this.font, 20, 11, 150, 20, Component.literal("Name"));
        this.nameEditor.setValue(this.spellDrawing.getName());
        this.addRenderableWidget(this.nameEditor);
        this.addRenderableWidget(
                Button.builder(Component.literal("Edit Drawings"), b -> {
                            this.minecraft.setScreen(new AllDrawingsScreen(spellDrawing, currentProject));
                        })
                        .bounds(20, 40, 100, 20) // x, y, width, height
                        .build()
        );

        this.addRenderableWidget(
                Button.builder(Component.literal("Back"), b -> {
                            this.minecraft.setScreen(new SpellProjectScreen(currentProject));
                        })
                        .bounds(20, height - 30, 50, 20) // x, y, width, height
                        .build()
        );
    }

    @Override
    public void tick() {
        super.tick();

        // Add ticking logic for EditBox in editBox
        this.nameEditor.tick();
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
        if (this.nameEditor != null) {
            this.spellDrawing.setName(this.nameEditor.getValue());
        }

        Magical_shapes.save();
        // Call last in case it interferes with the override
        super.removed();
    }
}
