package net.gudenau.coolers.proxy;

import net.gudenau.coolers.Coolers;
import net.gudenau.coolers.render.TileEntityCoolerRenderer;
import net.gudenau.coolers.tiles.TileEntityCooler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import static net.gudenau.coolers.Coolers.MOD_ID;

/**
 * Created by gudenau on 12/26/2016.
 * <p>
 * coolers
 */
public class ClientProxy extends Proxy{
    @Override
    public void preInit(){
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCooler.class, new TileEntityCoolerRenderer());
        ForgeHooksClient.registerTESRItemStack(Coolers.INSTANCE.itemCooler, 0, TileEntityCooler.class);
    }

    @Override
    public void postInit(){
        Minecraft
                .getMinecraft()
                .getRenderItem()
                .getItemModelMesher()
                .register(Coolers.INSTANCE.itemCooler, 0, new ModelResourceLocation(new ResourceLocation(MOD_ID, "cooler"), "inventory"));
    }
}
