package net.gudenau.coolers.compatibility;

import net.gudenau.coolers.api.FoodInfo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.AchievementList;
import net.minecraft.world.World;

import java.lang.reflect.Field;

/**
 * Created by gudenau on 12/16/2016.
 * <p>
 * coolers
 */
public class VanillaFoodInfo implements FoodInfo<ItemFood> {
    @Override
    public float getSaturationLevel(ItemStack stack){
        return getItem(stack).getSaturationModifier(stack);
    }
    
    @Override
    public int getHungerLevel(ItemStack stack){
        return getItem(stack).getHealAmount(stack);
    }

    @Override
    public void applyEffects(ItemStack stack, EntityLivingBase entity, World world) {
        ItemFood item = getItem(stack);

        if(item instanceof ItemAppleGold){
            if (stack.getMetadata() > 0) {
                if (entity instanceof EntityPlayer){
                    ((EntityPlayer)entity).addStat(AchievementList.OVERPOWERED);
                }
                entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 400, 1));
                entity.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 6000, 0));
                entity.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 6000, 0));
                entity.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 2400, 3));
            }else{
                entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 100, 1));
                entity.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 2400, 0));
            }
        }else {
            PotionEffect potionEffect = getField(5, item, PotionEffect.class);
            //noinspection ConstantConditions
            if (!world.isRemote && potionEffect != null && world.rand.nextFloat() < getField(6, item, Float.class)) {
                entity.addPotionEffect(new PotionEffect(potionEffect));
            }
        }
    }

    @SuppressWarnings("unused")
    private <T> T getField(int index, Object object, Class<T> type) {
        Field field = ItemFood.class.getDeclaredFields()[index];
        field.setAccessible(true);
        try {
            //noinspection unchecked
            return (T)field.get(object);
        } catch (IllegalAccessException e) {
            return null;
        }
    }
}
