package org.stefanovietch.magical_shapes.menus;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.stefanovietch.magical_shapes.Magical_shapes;
import org.stefanovietch.magical_shapes.ml.Model;
import org.stefanovietch.magical_shapes.ml.Spline;

import static org.stefanovietch.magical_shapes.menus.ProjectStorage.PROJECTS;

public class SpellProjectScreen extends Screen {
    private final SpellProject project;
    private EditBox nameEditor;
    private final Drawing drawing = new Drawing();
    private StringWidget prediction;
    private Button train_button;

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

        prediction = new StringWidget(500, 50, Component.literal("Test"), Minecraft.getInstance().font);
        drawing.setPosition(150, 50);

        train_button = Button.builder(Component.literal("Train"), b -> {
                    project.model.trainAsync(501, () -> {
                                System.out.println("Training complete!");
                                });
                        })
                        .bounds(200, height - 30, 50, 20) // x, y, width, height
                        .build();

        if (project.model == null ) {
            drawing.drawWidget.visible = false;
            drawing.clearButton.visible = false;
            prediction.visible = false;
            train_button.active = false;
        }

        addRenderableWidget(drawing.drawWidget);
        addRenderableWidget(drawing.clearButton);
        addRenderableWidget(prediction);
        addRenderableWidget(train_button);

        this.addRenderableWidget(
                Button.builder(Component.literal(project.model == null ? "Create Model" : "Remake Model"), b -> {
                            this.project.model = new Model(this.project);
                            this.project.model.build();
                            drawing.drawWidget.visible = true;
                            prediction.visible = true;
                            train_button.active = true;
                            drawing.clearButton.visible = true;
                        })
                        .bounds(90, height - 30, 100, 20) // x, y, width, height
                        .build()
        );
        this.addRenderableWidget(
                Button.builder(Component.literal("Back"), b -> {
                            this.minecraft.setScreen(new MainScreen());
                        })
                        .bounds(20, height - 30, 50, 20) // x, y, width, height
                        .build()
        );
        this.addRenderableWidget(
                Button.builder(Component.literal("Delete"), b -> {
                            PROJECTS.remove(this.project);
                            this.minecraft.setScreen(new MainScreen());
                        })
                        .bounds(width - 60, height - 30, 50, 20) // x, y, width, height
                        .build()
        );
    }

    @Override
    public void tick() {
        super.tick();
        // Add ticking logic for EditBox in editBox
        this.nameEditor.tick();
        if (project.model != null && drawing.length() > 10 && !project.model.isTraining) {
            String p = this.project.model.predict(Spline.buildToMatrix(drawing.toAngleList().startZero().getAngleList(),100));
            prediction.setMessage(Component.literal(p));
        }

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