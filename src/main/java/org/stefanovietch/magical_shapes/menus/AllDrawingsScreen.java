package org.stefanovietch.magical_shapes.menus;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.stefanovietch.magical_shapes.Magical_shapes;

import java.util.ArrayList;
import java.util.List;

public class AllDrawingsScreen extends Screen {
    private final List<Drawing> drawingsList;
    private final SpellDrawing spellDrawing;
    SpellProject currentProject;
    private int scrollOffset = 0;

    public AllDrawingsScreen(SpellDrawing spellDrawing, SpellProject spellProject) {
        super(Component.literal(spellDrawing.getName()));
        this.drawingsList = spellDrawing.getDrawings();
        this.spellDrawing = spellDrawing;
        this.currentProject = spellProject;
    }

    @Override
    protected void init() {
        super.init();
        assert this.minecraft != null;
        // Add widgets and precomputed values
        int y = 0;
        for (Drawing d : drawingsList) {
            d.setPosition(80, 40 + 140*y);
            this.addRenderableWidget(d.drawWidget);
            this.addRenderableWidget(d.eraseButton);
            this.addRenderableWidget(d.clearButton);
            y++;
        }

        this.addRenderableWidget(
                Button.builder(Component.literal("Back"), b -> {
                            this.minecraft.setScreen(new SpellDrawingScreen(spellDrawing, currentProject));
                        })
                        .bounds(20, 20, 50, 20) // x, y, width, height
                        .build()
        );
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        scrollOffset -= (int) (delta * 20); // adjust speed

        int maxScroll = Math.max(0, drawingsList.size() * 140 - (height - 60));
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));
        return true;
    }

    @Override
    public void tick() {
        super.tick();

        // Add ticking logic for EditBox in editBox
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Background is typically rendered first
        this.renderBackground(graphics);

        graphics.enableScissor(0, scrollOffset, width, height);
        int y = 0;
        for (Drawing d : drawingsList) {
            int drawY = 20 + 140 * y - scrollOffset; // apply scroll offset
            d.setPosition(80, drawY);
            d.drawWidget.render(graphics, mouseX, mouseY, partialTick);
            d.eraseButton.render(graphics, mouseX, mouseY, partialTick);
            d.clearButton.render(graphics, mouseX, mouseY, partialTick);
            y++;
        }

        graphics.disableScissor();
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
