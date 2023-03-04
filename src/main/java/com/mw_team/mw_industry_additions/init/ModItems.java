package com.mw_team.mw_industry_additions.init;


import com.mw_team.mw_industry_additions.*;
import com.mw_team.mw_industry_additions.datagen.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.*;
import net.minecraft.world.item.*;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.registries.*;
import org.mini2Dx.gdx.utils.*;

import java.util.function.*;
@SuppressWarnings("unchecked")

public class ModItems{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, IndustrialAdditions.MOD_ID);
    public static ObjectMap<RegistryObject<? extends Item>, BiFunction<Item, ModItemModelProvider,ItemModelBuilder>> models = new ObjectMap<>();
    public static ObjectMap<RegistryObject<Item>, Array<TagKey<Item>>> tags = new ObjectMap<>();
    public static final TagKey<Item> TRADE_CARDS = ItemTags.create(new ResourceLocation("forge:trade_cards"));
    public static final TagKey<Item> PROFESSION_CARDS = ItemTags.create(new ResourceLocation("forge:profession_cards"));
    public static final TagKey<Item> CARDS = ItemTags.create(new ResourceLocation("mw_industry_additions:cards"));
    public static final TagKey<Item> TRADER_PAYMENTS = ItemTags.create(new ResourceLocation("mw_industry_additions:trader_payments"));

    //public static final RegistryObject<Item> CARD_DUMMY;
    //static{
    //    CARD_DUMMY = registerItem("card_dummy",
    //    () -> new Item(new Item.Properties()),
    //    Array.with(CARDS),
    //    CreativeModeTab.TAB_MISC
    //    );
    //}

    public static RegistryObject<Item> registerItem(String name, Supplier<Item> sup, Array<TagKey<Item>> taglist, CreativeModeTab tabMisc){
        return registerItem(name, sup, taglist, CreativeModeTab.TAB_MISC);
    }
}
