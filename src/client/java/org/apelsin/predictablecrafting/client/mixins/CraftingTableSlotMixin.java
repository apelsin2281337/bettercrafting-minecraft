package org.apelsin.predictablecrafting.client.mixins;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
//import org.apelsin.predictablecrafting.config.ModConfig;
import org.apelsin.predictablecrafting.mixins.CraftingResultSlotAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Environment(EnvType.CLIENT)
@Mixin(Slot.class)
public class CraftingTableSlotMixin {

    //@Unique
    //private ModConfig modConfig = ModConfig.get();

    @Inject(
            method = "getItem",
            at = @At("RETURN"),
            cancellable = true
    )
    private void ShowMaximumCraftableAmount(CallbackInfoReturnable<ItemStack> cir){

        //if (!modConfig.isModEnabled || !modConfig.isTrueAmountEnabled) return;

        Minecraft minecraftClient = Minecraft.getInstance();

        if (!minecraftClient.isSameThread()) return;
        if (!((Object)this instanceof ResultSlot)) {
            return;
        }

        ResultSlot craftingSlot = (ResultSlot)(Object)this;

        try {
            if (!minecraftClient.hasShiftDown()) {
                return;
            }
        }
        catch (Exception e) {
            return;
        }

        ItemStack initialResult = cir.getReturnValue();

        if (initialResult.isEmpty()) {
            return;
        }

        CraftingContainer input = ((CraftingResultSlotAccessor)craftingSlot).getInput();

        int minAmountOfItems = Integer.MAX_VALUE;
        for (ItemStack stack : input) {
            if (!stack.isEmpty()) {
                minAmountOfItems = Math.min(stack.getCount(), minAmountOfItems);
            }
        }


        if (minAmountOfItems == Integer.MAX_VALUE || minAmountOfItems <= 1) {
            return;
        }

        ItemStack modifiedResult = initialResult.copy();
        modifiedResult.setCount((initialResult.getCount() * minAmountOfItems));

        cir.setReturnValue(modifiedResult);
    }
}