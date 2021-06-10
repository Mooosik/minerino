package net.mooosik.minerino.mixin;


import me.lambdaurora.spruceui.Position;
import me.lambdaurora.spruceui.widget.SpruceButtonWidget;
import me.lambdaurora.spruceui.wrapper.VanillaButtonWrapper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.mooosik.minerino.gui.SpruceMainMenuScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Currently unused. WIP
 */
@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {
    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        this.addButton(new SpruceButtonWidget(Position.of(0, 100), 100, 20, new LiteralText("Minerino"),
                btn -> this.client.openScreen(new SpruceMainMenuScreen(this))).asVanilla());

    }
}
