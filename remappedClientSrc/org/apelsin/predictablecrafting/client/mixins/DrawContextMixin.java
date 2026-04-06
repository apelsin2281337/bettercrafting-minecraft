package org.apelsin.predictablecrafting.client.mixins;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.apelsin.predictablecrafting.config.ModConfig;
import org.joml.Matrix3x2fStack;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(GuiGraphics.class)
public class DrawContextMixin {

    @Unique
    private ModConfig modConfig = ModConfig.get();

    @Inject(method = "renderItemCount", at = @At("TAIL"), cancellable = true)
    private void customStackCount(Font textRenderer, ItemStack stack, int x, int y, @Nullable String stackCountText, CallbackInfo ci) {
        if (!modConfig.isModEnabled) return;
        Minecraft minecraftClient = Minecraft.getInstance();
        if (stack.getCount() != 1 || stackCountText != null) {
            GuiGraphics context = (GuiGraphics)(Object)this;

            if (minecraftClient.hasShiftDown() && stack.getCount() > 64 && modConfig.isAmountOfStacksShown) {
                Matrix3x2fStack matrices = context.pose();
                matrices.pushMatrix();

                float scale = 0.8f;
                matrices.scale(scale, scale);
                String stacksString = String.valueOf(stack.getCount() / 64);

                int scaledX = (int)((x + 16) / scale) - textRenderer.width(stacksString);
                int scaledY = (int)((y + 1) / scale);

                int colorCode = (modConfig.stacksOpacity << 24) | (0x00FFFFFF);

                context.drawString(textRenderer, stacksString, scaledX, scaledY, colorCode, true);

                matrices.popMatrix();
            }

        }

        ci.cancel();
    }
}
