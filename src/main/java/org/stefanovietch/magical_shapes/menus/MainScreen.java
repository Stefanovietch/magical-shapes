package org.stefanovietch.magical_shapes.menus;


import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class MainScreen extends Screen {
    EditBox editBox;
    DrawWidget draw;
    public MainScreen(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();

        // Add widgets and precomputed values
        this.draw = new DrawWidget(20,40,200,100,Component.literal("draw"));
        this.editBox = new EditBox(this.font, 20, 11, 150, 20, Component.literal("hello"));
        this.addRenderableWidget(this.editBox);
        this.addRenderableWidget(this.draw);
    }

    @Override
    public void tick() {
        super.tick();

        // Add ticking logic for EditBox in editBox
        this.editBox.tick();
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

        // Call last in case it interferes with the override
        super.removed();
    }
}
