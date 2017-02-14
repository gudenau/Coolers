package net.gudenau.coolers.item;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.gudenau.coolers.api.CoolerType;
import net.gudenau.coolers.api.FoodRegistry;
import net.gudenau.coolers.render.TileEntityCoolerRenderer;
import net.gudenau.coolers.tiles.TileEntityCooler;
import net.gudenau.lib.api.item.IItemSpecialTile;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.FoodStats;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;

import java.util.List;

/**
 * Created by gudenau on 12/15/2016.
 * <p>
 * coolers
 */
public class ItemBlockCooler extends ItemBlock implements IItemSpecialTile{
    // TODO Enchantment to increase?
    private static final int MAX_FOOD = 14;

    public ItemBlockCooler(Block block){
        super(block);
        setMaxStackSize(1);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState){
        if (!world.setBlockState(pos, newState, 11)) return false;
        
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() == this.block){
            setTileEntityNBT(world, player, pos, stack);
            setCoolerNBT(world, pos, stack);
            this.block.onBlockPlacedBy(world, pos, state, player, stack);
        }

        return true;
    }
    
    private void setCoolerNBT(World world, BlockPos pos, ItemStack stack){
        TileEntity tile = world.getTileEntity(pos);
        
        if(tile instanceof TileEntityCooler && stack.hasTagCompound()){
            ((TileEntityCooler)tile).readFromItem(getSave(stack));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced){
        super.addInformation(stack, playerIn, tooltip, advanced);

        if(!stack.hasTagCompound()){
            return;
        }

        NBTTagCompound compound = getSave(stack);

        int food = compound.getInteger("food");
        float saturation = compound.getFloat("saturation");

        if(compound.hasKey("color") && compound.getInteger("color") != 0x00FFFFFF) {
            tooltip.add(advanced ?
                    String.format("Color: #%06X", compound.getInteger("color")) :
                    ChatFormatting.PREFIX_CODE + String.valueOf(ChatFormatting.ITALIC.getChar()) + "Dyed");
        }

        if(food != 0) {
            tooltip.add(String.format("Stored Food: %d", food));
        }

        if(saturation != 0) {
            if (saturation != (int) saturation) {
                tooltip.add(String.format("Stored Saturation: %s", String.valueOf(saturation)));
            } else {
                tooltip.add(String.format("Stored Saturation: %d", (int) saturation));
            }
        }

        ItemStackHandler inventory = new ItemStackHandler(6);
        inventory.deserializeNBT(compound.getCompoundTag("items"));
        Item air = Item.getItemFromBlock(Blocks.AIR);
        for(int i = 0; i < inventory.getSlots(); i++){
            ItemStack slot = inventory.getStackInSlot(i);
            Item item = slot.getItem();
            if(item != air){
                tooltip.add(String.format("%s x%d", slot.getDisplayName(), slot.getCount()));
            }
        }
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected){
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);

        if(!(entityIn instanceof EntityPlayer)){
            return;
        }

        if(stack.hasTagCompound()){
            NBTTagCompound compound = getSave(stack);

            EntityPlayer player = (EntityPlayer) entityIn;
            FoodStats foodStats = player.getFoodStats();

            int foodLevel = foodStats.getFoodLevel();
            float saturationLevel = foodStats.getSaturationLevel();

            int startingFoodLevel = foodLevel;
            float startingSaturationLevel = saturationLevel;
            boolean changed = false;

            int storedFood = compound.getInteger("food");
            float storedSaturation = compound.getFloat("saturation");

            if(storedFood < MAX_FOOD && storedFood == 0){
                ItemStackHandler inventory = new ItemStackHandler(6);
                inventory.deserializeNBT(compound.getCompoundTag("items"));

                for(int i = 0; i < inventory.getSlots(); i++){
                    ItemStack slot = inventory.getStackInSlot(i);
                    if(FoodRegistry.isItemFood(slot)){
                        storedFood = Math.min(FoodRegistry.getHungerLevel(slot), 20);
                        storedSaturation = Math.min(FoodRegistry.getSaturationLevel(slot), 20);
                        changed = true;
                        FoodRegistry.applyEffects(slot, player, worldIn);
                        slot.shrink(1);

                        worldIn.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);

                        break;
                    }
                }

                if(changed){
                    compound.setTag("items", inventory.serializeNBT());
                }
            }

            if(foodLevel < MAX_FOOD){
                int maxAmount = MAX_FOOD - foodLevel;

                if(storedFood > 0){
                    if(storedFood > maxAmount){
                        foodLevel = MAX_FOOD;
                        storedFood -= maxAmount;
                    }else{
                        foodLevel += storedFood;
                        storedFood = 0;
                    }
                    changed = true;
                }
            }
            if(saturationLevel < MAX_FOOD){
                float maxAmount = MAX_FOOD - saturationLevel;

                if(storedSaturation > 0){
                    if(storedSaturation > maxAmount){
                        storedSaturation = MAX_FOOD;
                        storedSaturation -= maxAmount;
                    }else{
                        saturationLevel += storedSaturation;
                        storedSaturation = 0;
                    }
                    changed = true;
                }
            }

            if(changed) {
                compound.setInteger("food", storedFood);
                compound.setFloat("saturation", storedSaturation);

                foodStats.addStats(
                        foodLevel - startingFoodLevel,
                        saturationLevel - startingSaturationLevel
                );
            }
        }
    }

    public void setColor(ItemStack stack, int color) {
        NBTTagCompound compound;
        if(!stack.hasTagCompound()){
            compound = new NBTTagCompound();
            stack.setTagCompound(compound);
        }

        getSave(stack).setInteger("color", color);
    }

    public static NBTTagCompound getSave(ItemStack stack) {
        NBTTagCompound save = stack.getSubCompound("save");
        if(save == null){
            save = new NBTTagCompound();

            NBTTagCompound stackCompound;
            if(stack.hasTagCompound()){
                stackCompound = stack.getTagCompound();
            }else{
                stackCompound = new NBTTagCompound();
                stack.setTagCompound(stackCompound);
            }

            //noinspection ConstantConditions
            stackCompound.setTag("save", save);
        }
        return save;
    }

    public boolean hasColor(ItemStack stack) {
        return stack.hasTagCompound() && getSave(stack).hasKey("color");
    }

    public int getColor(ItemStack stack) {
        if(stack.hasTagCompound()){
            return getSave(stack).getInteger("color");
        }else{
            return 0;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void preItemTileRender(ItemStack stack){
        TileEntityCoolerRenderer.nextStack = stack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void postItemTileRender(ItemStack stack){
        TileEntityCoolerRenderer.nextStack = null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems){
        ItemStack stack = new ItemStack(itemIn);

        NBTTagCompound compound = new NBTTagCompound();
        NBTTagCompound save = new NBTTagCompound();
        compound.setTag("save", save);
        stack.setTagCompound(compound);

        for(CoolerType type : CoolerType.values()) {
            save.setInteger("type", type.ordinal());
            subItems.add(stack.copy());
        }
    }
}
