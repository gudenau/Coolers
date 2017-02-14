package net.gudenau.coolers.api;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Created by gudenau on 12/16/2016.
 * <p>
 * coolers
 */
public interface FoodInfo<T extends Item> {
    @SuppressWarnings("SameReturnValue")
    default boolean isItemFood(ItemStack stack){ return true; }
    @SuppressWarnings("unchecked")
    default T getItem(ItemStack stack){ return (T)stack.getItem(); }
    float getSaturationLevel(ItemStack stack);
    int getHungerLevel(ItemStack stack);
    void applyEffects(ItemStack stack, EntityLivingBase entity, World world);
}
