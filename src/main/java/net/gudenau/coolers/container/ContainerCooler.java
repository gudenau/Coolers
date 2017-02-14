package net.gudenau.coolers.container;

import net.gudenau.coolers.tiles.TileEntityCooler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

/**
 * Created by gudenau on 12/16/2016.
 * <p>
 * coolers
 */
public class ContainerCooler extends Container {
    private final TileEntityCooler tile;
    
    public ContainerCooler(TileEntityCooler tile, EntityPlayer player){
        this.tile = tile;

        tile.openInventory(player);
        InventoryPlayer inventoryPlayer = player.inventory;

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new Slot(inventoryPlayer, x + y * 9 + 10, x * 18 + 8, y * 18 + 84));
            }
        }
    
        for (int x = 0; x < 9; x++) {
            this.addSlotToContainer(new Slot(inventoryPlayer, x, x * 18 + 8, 142));
        }
        
        IItemHandler itemHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        for(int i = 0; i < 6; i++){
            addSlotToContainer(new SlotItemHandler(itemHandler, i, (i % 3) * 18 + 62, (i / 3) * 18 + 18));
        }
    }
    
    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index){
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(index);
    
        if (slot != null && slot.getHasStack()){
            ItemStack stack = slot.getStack();
            itemstack = stack.copy();
        
            if (index < 6){
                if (mergeItemStack(stack, 6, this.inventorySlots.size(), true)){
                    return ItemStack.EMPTY;
                }
            }else if (!mergeItemStack(stack, 0, 6, false)){
                return ItemStack.EMPTY;
            }
        
            if (stack.isEmpty()){
                slot.putStack(ItemStack.EMPTY);
            }else{
                slot.onSlotChanged();
            }
        }
    
        return itemstack;
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn){
        super.onContainerClosed(playerIn);
        tile.closeInventory(playerIn);
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn){
        return tile.canInteractWith(playerIn);
    }
}
