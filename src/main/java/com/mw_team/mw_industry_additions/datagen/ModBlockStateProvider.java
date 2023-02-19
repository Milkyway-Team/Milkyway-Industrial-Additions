package com.mw_team.mw_industry_additions.datagen;

import com.mw_team.mw_industry_additions.init.*;
import net.minecraft.data.*;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.*;

public class ModBlockStateProvider extends BlockStateProvider{
    public ModBlockStateProvider(DataGenerator gen, String modid, ExistingFileHelper exFileHelper){
        super(gen, modid, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels(){
        for(var entry : ModBlocks.states){
            entry.value.accept(entry.key.get(),this);
        }
    }
}
