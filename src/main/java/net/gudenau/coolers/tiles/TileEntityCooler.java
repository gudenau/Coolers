package net.gudenau.coolers.tiles;

import javax.annotation.Nullable;

import net.gudenau.coolers.api.CoolerType;
import net.gudenau.coolers.block.BlockCooler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import static net.gudenau.coolers.Coolers.MOD_ID;

/**
 * Created by gudenau on 12/15/2016.
 * <p>
 * coolers
 */
public class TileEntityCooler extends TileEntity implements ITickable{
    private final ItemStackHandler inventory = new ItemStackHandler(6);
    private String customName;
    /** RRGGBB */
    private int color = 0xFFFFFF;
    private int numPlayersUsing;

    private CoolerType type = CoolerType.NORMAL;

    public float prevLidAngle;
    public float lidAngle;

    public float handleAngle = 1;
    public float prevHandleAngle;

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing){
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    @Nullable
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? (T)inventory : super.getCapability(capability, facing);
    }

    public boolean canInteractWith(EntityPlayer player){
        return world.getTileEntity(pos) == this && player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound){
        super.readFromNBT(compound);
        inventory.deserializeNBT(compound.getCompoundTag("items"));
        color = compound.hasKey("color") ? compound.getInteger("color") : 0xFFFFFF;
        handleAngle = prevHandleAngle = compound.getFloat("handle");
        type = CoolerType.getType(compound.getInteger("type"));
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound){
        compound = super.writeToNBT(compound);
        compound.setTag("items", inventory.serializeNBT());
        compound.setInteger("color", color);
        compound.setFloat("handle", handleAngle);
        compound.setInteger("type", type.ordinal());
        return compound;
    }
    
    @Override
    public ITextComponent getDisplayName(){
        return (hasCustomName() ? new TextComponentString(getName()) : new TextComponentTranslation(getName()));
    }

    public void openInventory(EntityPlayer player){
        if (!player.isSpectator()){
            if (numPlayersUsing < 0){
                numPlayersUsing = 0;
            }

            numPlayersUsing++;
            world.addBlockEvent(pos, getBlockType(), 1, numPlayersUsing);
            world.notifyNeighborsOfStateChange(pos, getBlockType(), false);
        }
    }

    public void closeInventory(EntityPlayer player){
        if (!player.isSpectator() && getBlockType() instanceof BlockCooler){
            --numPlayersUsing;
            world.addBlockEvent(pos, getBlockType(), 1, numPlayersUsing);
            world.notifyNeighborsOfStateChange(pos, getBlockType(), false);
        }
    }
    
    private String getName(){
        return hasCustomName() ? customName : "container." + MOD_ID + ".cooler.inventory";
    }
    
    private boolean hasCustomName(){
        return customName != null && !customName.isEmpty();
    }
    
    public int getColor(){
        return color;
    }
    
    public NBTTagCompound saveForItem(){
        NBTTagCompound compound = new NBTTagCompound();
        
        compound.setTag("items", inventory.serializeNBT());
        compound.setInteger("color", color);
        compound.setInteger("type", type.ordinal());
        
        return compound;
    }
    
    public void readFromItem(NBTTagCompound compound){
        inventory.deserializeNBT(compound.getCompoundTag("items"));
        color = compound.hasKey("color") ? compound.getInteger("color") : 0xFFFFFF;
        type = CoolerType.getType(compound.getInteger("type"));
    }

    @Override
    public void update() {
        prevLidAngle = lidAngle;

        int x = this.pos.getX();
        int y = this.pos.getY();
        int z = this.pos.getZ();

        if (numPlayersUsing > 0 && lidAngle == 0.0F){
            world.playSound(null, x + .5, y + .5, z + .5, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
        }

        if (numPlayersUsing == 0 && lidAngle > 0.0F || numPlayersUsing > 0 && lidAngle < 1.0F){
            float oldAngle = lidAngle;

            if (numPlayersUsing > 0){
                lidAngle += 0.1F;
            }else{
                lidAngle -= 0.1F;
            }

            if (lidAngle > 1.0F){
                lidAngle = 1.0F;
            }

            if (lidAngle < 0.5F && oldAngle >= 0.5F){
                world.playSound(null, x + .5, y + .5, z + .5, SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
            }

            if (lidAngle < 0.0F){
                lidAngle = 0.0F;
            }
        }

        prevHandleAngle = handleAngle;
        if(handleAngle < 135){
            if(handleAngle < 20) {
                handleAngle *= 2;
            }else{
                handleAngle += 20;
            }
            if(handleAngle > 135){
                handleAngle = 135;
            }
        }
    }

    @Override
    public NBTTagCompound getUpdateTag(){
        return writeToNBT(new NBTTagCompound());
    }

    public CoolerType getCoolerType() {
        return type;
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }
}
