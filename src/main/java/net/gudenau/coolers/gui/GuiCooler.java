package net.gudenau.coolers.gui;

import net.gudenau.coolers.container.ContainerCooler;
import net.gudenau.coolers.tiles.TileEntityCooler;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import static net.gudenau.coolers.Coolers.MOD_ID;

/**
 * Created by gudenau on 12/16/2016.
 * <p>
 * coolers
 */
public class GuiCooler extends GuiContainer {
    private static final ResourceLocation COOLER_TEXTURE = new ResourceLocation(MOD_ID, "textures/gui/container/cooler.png");
    
    private final TileEntityCooler tile;
    private final InventoryPlayer inventoryPlayer;
    
    public GuiCooler(TileEntityCooler tileEntity, EntityPlayer player){
        super(new ContainerCooler(tileEntity, player));
        
        tile = tileEntity;
        inventoryPlayer = player.inventory;
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
        fontRendererObj.drawString(inventoryPlayer.getDisplayName().getUnformattedText(), 8, 72, 0x404040);
        
        String text = tile.getDisplayName().getUnformattedText();
        int width = fontRendererObj.getStringWidth(text);
        fontRendererObj.drawString(text, 88 - width / 2, 6, 0x404040);
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(COOLER_TEXTURE);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x + 54, y, 54, 0, 68, 60);
        drawTexturedModalRect(x, y + 66, 0, 66, 176, 100);
    }
}
