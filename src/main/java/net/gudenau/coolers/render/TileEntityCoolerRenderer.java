package net.gudenau.coolers.render;

import net.gudenau.coolers.api.CoolerType;
import net.gudenau.coolers.block.BlockCooler;
import net.gudenau.coolers.item.ItemBlockCooler;
import net.gudenau.coolers.tiles.TileEntityCooler;
import net.gudenau.coolers.render.model.ModelCooler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.ItemStackHandler;

import static net.gudenau.coolers.Coolers.MOD_ID;

/**
 * Created by gudenau on 12/15/2016.
 * <p>
 * coolers
 */
public class TileEntityCoolerRenderer extends TileEntitySpecialRenderer<TileEntityCooler> {
    private static final ResourceLocation TEXTURE_PLAIN = new ResourceLocation(MOD_ID, "textures/block/model/cooler.png");
    private static final ResourceLocation TEXTURE_TRANSPARENT = new ResourceLocation(MOD_ID, "textures/block/model/cooler_clear.png");
    public static ItemStack nextStack;

    private final ModelCooler cooler = new ModelCooler();
    
    @Override
    public void renderTileEntityAt(TileEntityCooler tile, double x, double y, double z, float partialTicks, int destroyStage){
        GlStateManager.pushMatrix();
        if(tile == null){
            if(nextStack != null && nextStack.getItem() instanceof ItemBlockCooler) {
                renderAsItem(nextStack);
            }
        }else{
            renderAsBlock(tile, x, y, z, partialTicks);
        }
        GlStateManager.popMatrix();
    }

    private void renderAsItem(ItemStack stack) {
        NBTTagCompound save = ItemBlockCooler.getSave(stack);
        CoolerType type = CoolerType.getType(save.getInteger("type"));
        bindTexture(type);
        if (save.hasKey("color")) {
            int color = save.getInteger("color");
            GlStateManager.color(((color >> 16) & 0xFF) / 255F, ((color >> 8) & 0xFF) / 255F, (color & 0xFF) / 255F);
        }

        GlStateManager.translate(.5, .5, .5);
        GlStateManager.rotate(180, 1, 0, 0);
        GlStateManager.translate(-.5, -.5, -.5);
        cooler.handleAngle = -2.3561945F;
        cooler.lidAngle = 0;
        cooler.renderAll();

        GlStateManager.color(1, 1, 1);

        if(type == CoolerType.TRANSPARENT) {
            ItemStackHandler inventory = new ItemStackHandler(6);
            inventory.deserializeNBT(save.getCompoundTag("items"));

            renderInventory(inventory, 1);

            GlStateManager.enableBlend();
        }
    }

    private void renderInventory(ItemStackHandler inventory, float partialTicks) {
        int limit = Math.min(6, inventory.getSlots());
        RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();

        GlStateManager.translate(0.5, 0.42, 0.5);
        GlStateManager.translate(-(1/3D)+(.2*0.225), 0, 0);
        GlStateManager.scale(0.225, 0.225, 0.225);
        GlStateManager.rotate(180, 1, 0, 0);

        float tick = Minecraft.getMinecraft().world.getTotalWorldTime();
        tick = (tick - 1) + (tick - (tick - 1)) * partialTicks;

        for(int i = 0; i < limit; i++){
            ItemStack stack = inventory.getStackInSlot(i);
            if(!stack.isEmpty()){
                Item item = stack.getItem();
                Block block = Block.getBlockFromItem(item);
                GlStateManager.pushMatrix();
                boolean flag = itemRenderer.shouldRenderItemIn3D(stack) && block.getBlockLayer() == BlockRenderLayer.TRANSLUCENT;

                if(i >= 3) {
                    GlStateManager.translate(0, -1, 0);
                }
                GlStateManager.translate(i % 3 * 1.2, -1, 0);

                GlStateManager.rotate((((((i % 2) == 1) ? 1 : -1) * tick)) + (i / 2 * 60), 0, 1, 0);

                if (flag){
                    GlStateManager.depthMask(false);
                }

                itemRenderer.renderItem(stack, ItemCameraTransforms.TransformType.NONE);

                if (flag){
                    GlStateManager.depthMask(true);
                }

                GlStateManager.popMatrix();
            }
        }
    }

    private void renderAsBlock(TileEntityCooler tile, double x, double y, double z, float partialTicks) {
        IBlockState state = tile.getWorld().getBlockState(tile.getPos());
        // Apparently it can render the tile without the block there...
        if(!(state.getBlock() instanceof BlockCooler)){
            return;
        }

        bindTexture(tile.getCoolerType());

        int color = tile.getColor();
        GlStateManager.color(((color >> 16) & 0xFF) / 255F, ((color >> 8) & 0xFF) / 255F, (color & 0xFF) / 255F);
        //System.out.println(Integer.toHexString(color));

        GlStateManager.translate(x, y + 1, z + 1);
        GlStateManager.scale(1.0F, -1.0F, -1.0F);

        //GlStateManager.translate(0.5625, 1.0, 0.78125);
        GlStateManager.translate(0.5, 0.5, 0.5);

        // Rotation
        EnumFacing facing = state.getValue(BlockHorizontal.FACING);
        int angle = 0;

        switch(facing){
            case NORTH:
                break;
            case SOUTH:
                angle = 180;
                break;
            case EAST:
                angle = 90;
                break;
            case WEST:
                angle = 270;
                break;
        }

        GlStateManager.rotate(angle, 0, 1, 0);

        GlStateManager.translate(-0.5F, -0.5F, -0.5F);

        float lidAngle = tile.prevLidAngle + (tile.lidAngle - tile.prevLidAngle) * partialTicks;
        lidAngle = 1.0F - lidAngle;
        lidAngle = 1.0F - lidAngle * lidAngle * lidAngle;
        cooler.lidAngle = -(lidAngle * ((float)Math.PI / 2F));
        cooler.handleAngle = (float) Math.toRadians(-(tile.prevHandleAngle + (tile.handleAngle - tile.prevHandleAngle) * partialTicks));
        cooler.renderAll();

        GlStateManager.color(1, 1, 1);

        if(tile.getCoolerType() == CoolerType.TRANSPARENT) {
            renderInventory(tile.getInventory(), partialTicks);
        }
    }

    private void bindTexture(CoolerType coolerType) {
        switch (coolerType){
            case TRANSPARENT:
                bindTexture(TEXTURE_TRANSPARENT);
                break;
            default:
                bindTexture(TEXTURE_PLAIN);
        }
    }
}
