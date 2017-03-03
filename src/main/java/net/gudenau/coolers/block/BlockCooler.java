package net.gudenau.coolers.block;

import java.util.Random;
import net.gudenau.coolers.Coolers;
import net.gudenau.coolers.tiles.TileEntityCooler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by gudenau on 12/15/2016.
 * <p>
 * coolers
 */
public class BlockCooler extends Block {
    @SuppressWarnings("SpellCheckingInspection")
    private static final AxisAlignedBB NORTH_SOUTH_AABB = new AxisAlignedBB(0.0625, 0.0, 0.28125, 0.9375, 0.6875, 0.71875);
    @SuppressWarnings("SpellCheckingInspection")
    private static final AxisAlignedBB EAST_WEST_AABB = new AxisAlignedBB(0.28125, 0.0, 0.0625, 0.71875, 0.6875, 0.9375);

    private static final PropertyDirection FACING = BlockHorizontal.FACING;
    
    public BlockCooler(){
        super(Material.GLASS);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateFromMeta(int meta){
        EnumFacing enumfacing = EnumFacing.getFront(meta);
        
        if(enumfacing.getAxis() == EnumFacing.Axis.Y){
            enumfacing = EnumFacing.NORTH;
        }
        
        return getDefaultState().withProperty(FACING, enumfacing);
    }
    
    @Override
    public int getMetaFromState(IBlockState state){ return state.getValue(FACING).getIndex(); }
    
    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state){ return false; }
    
    @Override
    @SuppressWarnings("deprecation")
    public EnumBlockRenderType getRenderType(IBlockState state){ return EnumBlockRenderType.ENTITYBLOCK_ANIMATED; }
    
    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
        return state.getValue(FACING).getAxis() == Axis.Z ? NORTH_SOUTH_AABB : EAST_WEST_AABB;
    }
    
    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state){ return false; }
    
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing heldItem, float side, float hitX, float hitY){
        if(!worldIn.isRemote && worldIn.getTileEntity(pos) instanceof TileEntityCooler || playerIn.isSneaking()){
            playerIn.openGui(Coolers.INSTANCE, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
        }
        
        return true;
    }
    
    /*@Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack){
        worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing()), 2);
    }*/

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand){
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
    }

    @Override
    protected BlockStateContainer createBlockState(){ return new BlockStateContainer(this, FACING); }
    
    @Override
    public boolean hasTileEntity(IBlockState state){ return true; }
    
    @Override
    public TileEntity createTileEntity(World world, IBlockState state){ return new TileEntityCooler(); }
    
    @Override
    public int quantityDropped(Random random){ return 0; }
    
    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state){
        ItemStack stack = new ItemStack(this, 1, 0);
        
        TileEntity tile = worldIn.getTileEntity(pos);
        if(tile instanceof TileEntityCooler){
            TileEntityCooler cooler = (TileEntityCooler)tile;
            NBTTagCompound tag = cooler.saveForItem();
            NBTTagCompound stackCompound = stack.getTagCompound();
            if(stackCompound == null){ stackCompound = new NBTTagCompound(); }
            stackCompound.setTag("save", tag);
            stack.setTagCompound(stackCompound);
        }
        
        spawnAsEntity(worldIn, pos, stack);
        
        super.breakBlock(worldIn, pos, state);
    }
}
