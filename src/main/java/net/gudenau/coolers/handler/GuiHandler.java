package net.gudenau.coolers.handler;

import net.gudenau.coolers.container.ContainerCooler;
import net.gudenau.coolers.gui.GuiCooler;
import net.gudenau.coolers.tiles.TileEntityCooler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

/**
 * Created by gudenau on 12/16/2016.
 * <p>
 * coolers
 */
public class GuiHandler implements IGuiHandler {
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z){
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
        
        if(tileEntity instanceof TileEntityCooler){
            return new ContainerCooler((TileEntityCooler)tileEntity, player);
        }else{
            return null;
        }
    }
    
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z){
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
    
        if(tileEntity instanceof TileEntityCooler){
            return new GuiCooler((TileEntityCooler)tileEntity, player);
        }else{
            return null;
        }
    }
}
