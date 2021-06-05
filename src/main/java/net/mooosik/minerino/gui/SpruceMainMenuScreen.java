package net.mooosik.minerino.gui;


import io.micrometer.core.lang.Nullable;
import me.lambdaurora.spruceui.Position;
import me.lambdaurora.spruceui.SpruceTexts;
import me.lambdaurora.spruceui.option.SpruceBooleanOption;
import me.lambdaurora.spruceui.option.SpruceOption;
import me.lambdaurora.spruceui.screen.SpruceScreen;
import me.lambdaurora.spruceui.widget.SpruceButtonWidget;
import me.lambdaurora.spruceui.widget.SpruceLabelWidget;
import me.lambdaurora.spruceui.widget.SpruceWidget;
import me.lambdaurora.spruceui.widget.container.SpruceContainerWidget;
import me.lambdaurora.spruceui.widget.container.SpruceEntryListWidget;
import me.lambdaurora.spruceui.widget.container.SpruceOptionListWidget;
import me.lambdaurora.spruceui.widget.container.tabbed.SpruceTabbedWidget;
import me.lambdaurora.spruceui.widget.text.SpruceTextFieldWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public class SpruceMainMenuScreen extends SpruceScreen {
    private final Screen parent;
    private SpruceTabbedWidget tabbedWidget;
    public SpruceMainMenuScreen(@Nullable Screen parent) {
        super(new LiteralText("SpruceUI Test Main Menu"));
        this.parent = parent;
    }

    boolean aBoolean;
    @Override
    protected void init() {
        super.init();

        int startY = this.height / 4 + 48;

        SpruceTextFieldWidget widget = new SpruceTextFieldWidget(Position.of(this, this.width / 2 - 75, this.height - 58),150, 20, new LiteralText("OAUTH"));
        widget.setText("oc cute peepoBlush");

        super.init();
        this.tabbedWidget = new SpruceTabbedWidget(Position.of(this, 0, 4), this.width, this.height - 35 - 4, this.title);
        this.tabbedWidget.addTabEntry(new LiteralText("Hello World"), null, (width, height) -> {
            SpruceContainerWidget container = new SpruceContainerWidget(Position.origin(), width, height);
            container.addChildren((containerWidth, containerHeight, widgetAdder) -> {
                widgetAdder.accept(new SpruceLabelWidget(Position.of(0, 16),
                        new LiteralText("Hello World!").formatted(Formatting.WHITE),
                        containerWidth, true));
                widgetAdder.accept(new SpruceLabelWidget(Position.of(0, 48),
                        new LiteralText("This is a tabbed widget. You can switch tabs by using the list on the left.\n" +
                                "It also allows quite a good controller support and arrow key navigation.")
                                .formatted(Formatting.WHITE),
                        containerWidth, true));
            });
            return container;
        });

        this.tabbedWidget.addTabEntry(new LiteralText("tab 2"), null, (width, height) -> {
            SpruceContainerWidget container = new SpruceContainerWidget(Position.origin(), width, height);
            container.addChildren((containerWidth, containerHeight, widgetAdder) -> {
                widgetAdder.accept(new SpruceLabelWidget(Position.of(0, 16),
                        new LiteralText("Hello World!").formatted(Formatting.WHITE),
                        containerWidth, true));
                widgetAdder.accept(new SpruceLabelWidget(Position.of(0, 48),
                        new LiteralText("This is a tabbed widget. You can switch tabs by using the list on the left.\n" +
                                "It also allows quite a good controller support and arrow key navigation.")
                                .formatted(Formatting.WHITE),
                        containerWidth, true));
            });
            return container;
        });


        this.addChild(this.tabbedWidget);
        SpruceOption option =  new SpruceBooleanOption("spruceui_test.option.boolean",
                () -> this.aBoolean,
                newValue -> this.aBoolean = newValue,
                new LiteralText("Represents a boolean option, can either be true or false.\n" +
                        "The option value can be colored."),
                true);
        SpruceOptionListWidget optionListWidget = new SpruceOptionListWidget(Position.of(this, this.width / 2 - 75,  29),150, 50);
        optionListWidget.addSmallSingleOptionEntry(option);

        this.addChild(optionListWidget);

        // Add done button.
        this.addChild(new SpruceButtonWidget(Position.of(this, this.width / 2 - 75, this.height - 29), 150, 20, SpruceTexts.GUI_DONE,
                btn -> this.client.openScreen(this.parent)));

        this.addChild(widget);
    }

    @Override
    public void renderTitle(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 16777215);
    }
}