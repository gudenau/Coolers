package net.gudenau.coolers.api;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Created by gudenau on 12/16/2016.
 * <p>
 * coolers
 */
public final class FoodRegistry {
    private static final Map<Class<? extends Item>, FoodInfo<? extends Item>> registry = new HashMap<>();

    public static <T extends Item> void addFoodType(Class<T> food, FoodInfo<T> info){
        if(!registry.containsKey(food)){
            registry.put(food, info);
        }
    }
    
    private static <T extends Item> FoodInfo<T> getFoodInfo(ItemStack stack){
        if(stack.isEmpty()){
            return null;
        }

        Item item = stack.getItem();
        Class<? extends Item> itemClass = item.getClass();
        while(itemClass != Item.class && Item.class.isAssignableFrom(itemClass)){
            FoodInfo foodInfo = registry.get(itemClass);
            if(foodInfo != null){
                //noinspection unchecked
                return foodInfo;
            }
            //noinspection unchecked
            itemClass = (Class<? extends Item>) itemClass.getSuperclass();
        }
        return null;
    }
    
    public static boolean isItemFood(ItemStack stack){
        FoodInfo<? extends Item> foodInfo = getFoodInfo(stack);
        return foodInfo != null && foodInfo.isItemFood(stack);
    }
    
    public static int getHungerLevel(ItemStack stack){
        FoodInfo<? extends Item> foodInfo = getFoodInfo(stack);
        return foodInfo != null ? foodInfo.getHungerLevel(stack) : 0;
    }
    
    public static float getSaturationLevel(ItemStack stack){
        FoodInfo<? extends Item> foodInfo = getFoodInfo(stack);
        return foodInfo != null ? foodInfo.getSaturationLevel(stack) : 0;
    }

    public static void applyEffects(ItemStack stack, EntityLivingBase entity, World world) {
        FoodInfo<? extends Item> foodInfo = getFoodInfo(stack);
        if (foodInfo != null) {
            foodInfo.applyEffects(stack, entity, world);
        }
    }
}
