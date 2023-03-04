package com.mw_team.mw_industry_additions.init;

import com.mw_team.mw_industry_additions.*;
import com.mw_team.mw_industry_additions.blocks.*;
import com.mw_team.mw_industry_additions.datagen.*;
import net.minecraft.resources.*;
import net.minecraft.tags.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.material.*;
import net.minecraftforge.registries.*;
import org.mini2Dx.gdx.utils.*;

import java.util.function.*;

import static net.minecraft.tags.BlockTags.MINEABLE_WITH_AXE;

@SuppressWarnings("unchecked")
public class ModBlocks{
    //registeries
    public static final DeferredRegister<Item> BLOCK_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, IndustrialAdditions.MOD_ID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, IndustrialAdditions.MOD_ID);
    //tags
    public static final TagKey<Block> BLOOMERY = BlockTags.create(new ResourceLocation(IndustrialAdditions.MOD_ID, "bloomery_furnace"));
    public static final TagKey<Block> TRADING_STATION = BlockTags.create(new ResourceLocation(IndustrialAdditions.MOD_ID, "trade_station"));

    //mapping
    public static ObjectMap<RegistryObject<Block>, Array<TagKey<Block>>> tags = new ObjectMap<>();
    public static ObjectMap<RegistryObject<Block>, BiConsumer<Block, ModBlockStateProvider>> states = new ObjectMap<>();

    //blocks
    public static final RegistryObject<Block> BLOOMERY_FURNACE_HEARTH, BLOOMERY_FURNACE_CHAMBER, BLOOMERY_FURNACE_CHIMNEY, TRADE_STATION;

    static{
        final String bloomeryTex =  "block/bloomery/bloomery_furnace";
        BLOOMERY_FURNACE_HEARTH = registerBlock(
        "bloomery_furnace_hearth",
        () -> new Block(Block.Properties.of(Material.STONE).strength(4f, 4000f).requiresCorrectToolForDrops() ),
        Array.with(BlockTags.NEEDS_STONE_TOOL, BLOOMERY),
        CreativeModeTab.TAB_DECORATIONS
        );

        BLOOMERY_FURNACE_CHAMBER = registerBlock(
        "bloomery_furnace_chamber",
        () -> new BloomeryChamber(Block.Properties.of(Material.STONE).strength(4f, 4000f).requiresCorrectToolForDrops() ),
        Array.with(BlockTags.NEEDS_STONE_TOOL, BLOOMERY),
        CreativeModeTab.TAB_DECORATIONS,
        properties -> properties,
        (block, state) -> {
                state.horizontalBlock(block,state.modLoc(bloomeryTex+"_chamber_side"),state.modLoc(bloomeryTex+"_chamber_front"),state.modLoc(bloomeryTex+"_chamber_top"));
            }
        );

        BLOOMERY_FURNACE_CHIMNEY = registerBlock(
        "bloomery_furnace_chimney",
        () -> new Block(Block.Properties.of(Material.STONE).strength(2.5f, 2000f).requiresCorrectToolForDrops() ),
        Array.with(BlockTags.NEEDS_STONE_TOOL, BLOOMERY),
        CreativeModeTab.TAB_DECORATIONS
        );

        final String traderTex =  "block/trader/trader";

        TRADE_STATION = registerBlock(
                "trade_station",
                () -> new TradeStation(Block.Properties.of(Material.WOOD).strength(3f, 4000f).requiresCorrectToolForDrops() ),
                Array.with(BlockTags.NEEDS_STONE_TOOL, TRADING_STATION, MINEABLE_WITH_AXE),
                CreativeModeTab.TAB_MISC,
                properties -> properties,
                (block, state) -> {
                  state.horizontalBlock(block,state.modLoc(traderTex+"_side"),state.modLoc(traderTex+"_front"),state.modLoc(traderTex+"_top"));
               }
        );
    }

    //todo: replace with builder pattern.
    public static RegistryObject<Block> registerBlock(String name, Supplier<Block> sup, Array<TagKey<Block>> taglist){
        return registerBlock(name, sup, taglist, CreativeModeTab.TAB_BUILDING_BLOCKS);
    }

    public static RegistryObject<Block> registerBlock(String name, Supplier<Block> sup, Array<TagKey<Block>> taglist, CreativeModeTab tab){
        return registerBlock(name, sup, taglist, tab, t->t);
    }

    public static RegistryObject<Block> registerBlock(String name, Supplier<Block> sup, Array<TagKey<Block>> taglist, CreativeModeTab tab, UnaryOperator<Item.Properties> itemprops){
        return registerBlock(name,sup,taglist,tab,itemprops,(block, states) -> {states.simpleBlock(block);});
    }

    public static RegistryObject<Block> registerBlock(String name, Supplier<Block> sup, Array<TagKey<Block>> taglist, CreativeModeTab tab, UnaryOperator<Item.Properties> itemprops ,BiConsumer<Block,ModBlockStateProvider> state){
        var p = BLOCKS.register(name, sup);
        tags.put(p, taglist);
        var item = BLOCK_ITEMS.register(name, () -> new BlockItem(p.get(), itemprops.apply(new Item.Properties().tab(tab))));
        ModItems.models.put(item,(i, model) -> model.blockItem(i));
        ///TEMP
        states.put(p, state);
        return p;
    }
}
