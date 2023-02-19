package com.mw_team.mw_industry_additions.init;


import com.mw_team.mw_industry_additions.*;
import com.mw_team.mw_industry_additions.datagen.*;
import net.minecraft.tags.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.registries.*;
import org.mini2Dx.gdx.utils.*;

import java.util.function.*;

public class ModItems{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, IndustrialAdditions.MOD_ID);
    public static ObjectMap<RegistryObject<? extends Item>, BiFunction<Item, ModItemModelProvider,ItemModelBuilder>> models = new ObjectMap<>();
}
