package org.stefanovietch.magical_shapes.menus;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.stefanovietch.magical_shapes.Magical_shapes;

import static org.stefanovietch.magical_shapes.menus.ProjectStorage.PROJECTS;

public class SpellProjectScreen extends Screen {
    private final SpellProject project;
    private EditBox nameEditor;

    public SpellProjectScreen(SpellProject project) {
        super(Component.literal(project.getName()));
        this.project = project;
    }

    @Override
    protected void init() {
        super.init();
        this.nameEditor = new EditBox(this.font, 10, 10, 150, 20, Component.literal("Name"));
        this.nameEditor.setValue(this.project.getName());
        this.addRenderableWidget(this.nameEditor);

        this.addRenderableWidget(
                Button.builder(Component.literal("Create Spell"), b -> {
                            SpellDrawing new_spelldrawing = new SpellDrawing("");
                            this.project.addSpellDrawing(new_spelldrawing);
                            this.minecraft.setScreen(new SpellDrawingScreen(new_spelldrawing, this.project));
                        })
                        .bounds(20, 40, 120, 20) // x, y, width, height
                        .build()
        );

        for (int i = 0; i < project.getSpellDrawings().size(); i++) {
            SpellDrawing spell = project.getSpellDrawings().get(i);
            this.addRenderableWidget(
                    Button.builder(Component.literal("Open Spell: " + spell.getName()), b -> {
                                this.minecraft.setScreen(new SpellDrawingScreen(spell, this.project));
                            })
                            .bounds(20, 70 + i * 25, 120, 20) // x, y, width, height
                            .build()
            );
        }
        this.addRenderableWidget(
                Button.builder(Component.literal("Back"), b -> {
                            this.minecraft.setScreen(new MainScreen());
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
            this.project.setName(this.nameEditor.getValue());
        }

        Magical_shapes.save();
        // Call last in case it interferes with the override
        super.removed();
    }
}