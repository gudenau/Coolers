package net.gudenau.coolers;

import net.gudenau.coolers.item.ItemBlockCooler;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

/**
 * Created by gudenau on 12/22/2016.
 *
 * coolers
 */
public class RecipeCoolerColor implements IRecipe {

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn){
        ItemStack cooler = ItemStack.EMPTY;
        boolean foundDye = false;

        for (int i = 0; i < inv.getSizeInventory(); ++i){
            ItemStack stack = inv.getStackInSlot(i);

            if (!stack.isEmpty()){
                if (stack.getItem() instanceof ItemBlockCooler){
                    if (!cooler.isEmpty()){
                        return false;
                    }

                    cooler = stack;
                }else{
                    if (stack.getItem() != Items.DYE){
                        return false;
                    }

                    foundDye = true;
                }
            }
        }

        return !cooler.isEmpty() && foundDye;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack getCraftingResult(InventoryCrafting inv){
        ItemStack cooler = ItemStack.EMPTY;
        int[] color = new int[3];
        int i = 0;
        int j = 0;
        ItemBlockCooler itemCooler = null;

        for (int k = 0; k < inv.getSizeInventory(); ++k){
            ItemStack stack = inv.getStackInSlot(k);

            if (!stack.isEmpty()){
                if (stack.getItem() instanceof ItemBlockCooler){
                    itemCooler = (ItemBlockCooler)stack.getItem();

                    cooler = stack.copy();
                    cooler.setCount(1);

                    if (itemCooler.hasColor(stack)){
                        int l = itemCooler.getColor(cooler);
                        float f = (float)(l >> 16 & 255) / 255.0F;
                        float f1 = (float)(l >> 8 & 255) / 255.0F;
                        float f2 = (float)(l & 255) / 255.0F;
                        i = (int)((float)i + Math.max(f, Math.max(f1, f2)) * 255.0F);
                        color[0] = (int)((float)color[0] + f * 255.0F);
                        color[1] = (int)((float)color[1] + f1 * 255.0F);
                        color[2] = (int)((float)color[2] + f2 * 255.0F);
                        ++j;
                    }
                }else{
                    if (stack.getItem() != Items.DYE){
                        return ItemStack.EMPTY;
                    }

                    float[] afloat = EntitySheep.getDyeRgb(EnumDyeColor.byDyeDamage(stack.getMetadata()));
                    int l1 = (int)(afloat[0] * 255.0F);
                    int i2 = (int)(afloat[1] * 255.0F);
                    int j2 = (int)(afloat[2] * 255.0F);
                    i += Math.max(l1, Math.max(i2, j2));
                    color[0] += l1;
                    color[1] += i2;
                    color[2] += j2;
                    ++j;
                }
            }
        }

        if (itemCooler == null){
            return ItemStack.EMPTY;
        }else{
            int i1 = color[0] / j;
            int j1 = color[1] / j;
            int k1 = color[2] / j;
            float f3 = (float)i / (float)j;
            float f4 = (float)Math.max(i1, Math.max(j1, k1));
            i1 = (int)((float)i1 * f3 / f4);
            j1 = (int)((float)j1 * f3 / f4);
            k1 = (int)((float)k1 * f3 / f4);
            int lvt_12_3_ = (i1 << 8) + j1;
            lvt_12_3_ = (lvt_12_3_ << 8) + k1;
            itemCooler.setColor(cooler, lvt_12_3_);
            return cooler;
        }
    }

    /**
     * Returns the size of the recipe area
     */
    public int getRecipeSize()
    {
        return 10;
    }

    public ItemStack getRecipeOutput()
    {
        return ItemStack.EMPTY;
    }

    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv){
        NonNullList<ItemStack> items = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

        for (int i = 0; i < items.size(); ++i){
            ItemStack itemstack = inv.getStackInSlot(i);
            if(itemstack.getItem() instanceof ItemBlockCooler){
                items.set(i, net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack));
            }else{
                items.set(i, new ItemStack(itemstack.getItem(), 1, itemstack.getItemDamage()));
            }
        }

        return items;
    }
}
