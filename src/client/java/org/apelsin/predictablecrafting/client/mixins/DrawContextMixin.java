package org.apelsin.predictablecrafting.client.mixins;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;
//import org.apelsin.predictablecrafting.config.ModConfig;
import org.joml.Matrix3x2fStack;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(GuiGraphicsExtractor.class)
public class DrawContextMixin {

    //@Unique
    //private ModConfig modConfig = ModConfig.get();

    @Inject(method = "itemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", at = @At("TAIL"), cancellable = true)
    private void customStackCount(Font font, ItemStack itemStack, int x, int y, @Nullable String countText, CallbackInfo ci) {
        //if (!modConfig.isModEnabled) return;
        Minecraft minecraftClient = Minecraft.getInstance();
        if (itemStack.getCount() != 1 || countText != null) {
            GuiGraphicsExtractor context = (GuiGraphicsExtractor) (Object)this;
            //&& modConfig.isAmountOfStacksShown
            if (minecraftClient.hasShiftDown() && itemStack.getCount() > 64 ) {
                Matrix3x2fStack matrices = context.pose();
                matrices.pushMatrix();

                float scale = 0.8f;
                matrices.scale(scale, scale);
                String stacksString = String.valueOf(itemStack.getCount() / 64);

                int scaledX = (int)((x + 16) / scale) - font.width(stacksString);
                int scaledY = (int)((y + 1) / scale);

                //int colorCode = (modConfig.stacksOpacity << 24) | (0x00FFFFFF);
                int colorCode = 0xD0FFFFFF;

                context.text(font, stacksString, scaledX, scaledY, colorCode, true);

                matrices.popMatrix();
            }

        }

        ci.cancel();
    }
}
