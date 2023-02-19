package com.mw_team.mw_industry_additions.datagen;

import com.mw_team.mw_industry_additions.init.*;
import net.minecraft.data.*;
import net.minecraft.data.tags.*;
import net.minecraftforge.common.data.*;
import org.jetbrains.annotations.*;

public class ModBlockTagsProvider extends BlockTagsProvider{

    public ModBlockTagsProvider(DataGenerator pGenerator, String modId, @Nullable ExistingFileHelper existingFileHelper){
        super(pGenerator, modId, existingFileHelper);
    }

    @Override
    protected void addTags(){
        //just so all the block data is in one place bc having it spread across multiple files is stupid.
        ModBlocks.tags.forEach(tagEntry -> {
            for(var tag:tagEntry.value){
                System.out.println(tag.location() +","+tagEntry.key.get().getName());
                this.tag(tag).add(tagEntry.key.get());
            }
        });

    }
}
