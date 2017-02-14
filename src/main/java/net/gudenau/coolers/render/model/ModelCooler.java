package net.gudenau.coolers.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by gudenau on 12/15/2016.
 * <p>
 * coolers
 */
@SideOnly(Side.CLIENT)
public class ModelCooler extends ModelBase {
    private final ModelRenderer body;
    private final ModelRenderer lid;
    
    private final ModelRenderer handle;
    private final ModelRenderer handleLeft;
    private final ModelRenderer handleRight;
    
    public float handleAngle;
    public float lidAngle;
    
    public ModelCooler(){
        body = new ModelRenderer(this, 0, 0).setTextureSize(64, 32);
        body.addBox(1.0F, 8.0F, 4.5F, 14, 8, 7);
    
        lid = new ModelRenderer(this, 0, 15).setTextureSize(64, 32);
        lid.addBox(1.0F, -3.0F, -7.0F, 14, 3, 7);
        lid.rotationPointY = 8.0F;
        lid.rotationPointZ = 11.5F;
    
        handle = new ModelRenderer(this, 0, 25).setTextureSize(64, 32);
        handle.addBox(0.0F, -6.5F, -0.5F, 16, 1, 1);
        handle.rotationPointY = 8.5F;
        handle.rotationPointZ = 8.0F;
    
        handleRight = new ModelRenderer(this, 38, 25).setTextureSize(64, 32);
        handleRight.addBox(0.0F, -5.5F, -0.5F, 1, 6, 1);
        handleRight.rotationPointX = 15F;
        handleRight.rotationPointY = 8.5F;
        handleRight.rotationPointZ = 8.0F;
    
        handleLeft = new ModelRenderer(this, 34, 25).setTextureSize(64, 32);
        handleLeft.addBox(0.0F, -5.5F, -0.5F, 1, 6, 1);
        handleLeft.rotationPointY = 8.5F;
        handleLeft.rotationPointZ = 8.0F;
    }
    
    public void renderAll(){
        lid.rotateAngleX = lidAngle;
        handle.rotateAngleX = handleRight.rotateAngleX = handleLeft.rotateAngleX = handleAngle;
    
        body.render(0.0625F);
        lid.render(0.0625F);
        handle.render(0.0625F);
        handleLeft.render(0.0625F);
        handleRight.render(0.0625F);
    }
}