package net.gudenau.coolers;

import java.lang.reflect.Constructor;

import net.gudenau.coolers.api.CoolerType;
import net.gudenau.coolers.api.FoodRegistry;
import net.gudenau.coolers.block.BlockCooler;
import net.gudenau.coolers.compatibility.VanillaFoodInfo;
import net.gudenau.coolers.handler.GuiHandler;
import net.gudenau.coolers.item.ItemBlockCooler;
import net.gudenau.coolers.proxy.Proxy;
import net.gudenau.coolers.tiles.TileEntityCooler;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;

import static net.gudenau.coolers.Coolers.MOD_ID;

/**
 * Created by gudenau on 12/15/2016.
 * <p>
 * coolers
 */
@Mod(modid = MOD_ID)
public class Coolers {
    public static final String MOD_ID = "gud_coolers";
    
    @SuppressWarnings("unused")
    @Instance(MOD_ID)
    public static Coolers INSTANCE;

    @SuppressWarnings({"WeakerAccess", "unused"})
    @SidedProxy(clientSide = "net.gudenau.coolers.proxy.ClientProxy", serverSide = "net.gudenau.coolers.proxy.ServerProxy")
    public static Proxy proxy;

    public ItemBlockCooler itemCooler;
    
    @EventHandler
    public void onPreInit(FMLPreInitializationEvent event){
        itemCooler = registerBlock(new BlockCooler(), ItemBlockCooler.class, TileEntityCooler.class);
    
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

        CraftingManager craftingManager = CraftingManager.getInstance();
        ItemStack coolerStack = new ItemStack(itemCooler, 1, 0);

        NBTTagCompound compound = new NBTTagCompound();
        NBTTagCompound save = new NBTTagCompound();
        save.setInteger("type", CoolerType.NORMAL.ordinal());
        compound.setTag("save", save);
        coolerStack.setTagCompound(compound);

        craftingManager.addRecipe(new ShapedOreRecipe(coolerStack,
                "sbs",
                "bcb",
                "sbs",
                's', Items.SNOWBALL,
                'b', Blocks.SNOW,
                'c', "chestWood"
        ));
        craftingManager.addRecipe(new ShapedOreRecipe(coolerStack,
                "sbs",
                "bcb",
                "sbs",
                's', Items.SNOWBALL,
                'b', Blocks.SNOW,
                'c', "chestTrapped"
        ));

        save.setInteger("type", CoolerType.TRANSPARENT.ordinal());
        for(Block block : new Block[]{
                Blocks.ICE,
                Blocks.FROSTED_ICE,
                Blocks.PACKED_ICE
        }) {
            craftingManager.addRecipe(new ShapedOreRecipe(coolerStack,
                    "sbs",
                    "bcb",
                    "sbs",
                    's', Items.SNOWBALL,
                    'b', block,
                    'c', "chestWood"
            ));
            craftingManager.addRecipe(new ShapedOreRecipe(coolerStack,
                    "sbs",
                    "bcb",
                    "sbs",
                    's', Items.SNOWBALL,
                    'b', block,
                    'c', "chestTrapped"
            ));
        }

        RecipeSorter.register(MOD_ID + "_cooler", RecipeCoolerColor.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
        craftingManager.addRecipe(new RecipeCoolerColor());

        FoodRegistry.addFoodType(ItemFood.class, new VanillaFoodInfo());

        proxy.preInit();
    }

    @EventHandler
    public void onPostInit(FMLPostInitializationEvent event){
        proxy.postInit();
    }
    
    private <T extends ItemBlock> T registerBlock(BlockCooler block, Class<T> itemClass, Class<? extends TileEntity> tileClass){
        try{
            Constructor<T> constructor = itemClass.getConstructor(Block.class);
            constructor.setAccessible(true);
            T item = constructor.newInstance(block);
            block.setUnlocalizedName(MOD_ID + "_cooler");
            block.setCreativeTab(CreativeTabs.FOOD);
            item.setUnlocalizedName(MOD_ID + "_cooler");
            GameRegistry.register(block, new ResourceLocation(MOD_ID, "cooler"));
            GameRegistry.register(item, new ResourceLocation(MOD_ID, "cooler"));
            GameRegistry.registerTileEntity(tileClass, MOD_ID + "_cooler");
            return item;
        }catch(ReflectiveOperationException e){
            throw new RuntimeException("Could not create item!", e);
        }
    }
}
