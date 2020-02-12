package com.dronez.Items;

import com.dronez.entities.Drone;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Stream;

public class DroneSpawnEggItem extends SpawnEggItem {

    //0 for air, 1 for iron, 2 for gold, 3 for diamond
    private String blades;
    private String shell;
    private String core;

    public DroneSpawnEggItem(EntityType<Drone> typeIn, int primaryColorIn, int secondaryColorIn, Item.Properties builder)
    {//May want to change the input of the types to a list to be cleaner, then add constants for the indexes of each item like BLADE1_POSITION = 0;
        super(typeIn, primaryColorIn, secondaryColorIn, builder);
        //Decode the NBT, first digit is blades, second digit is shell, third digit is core 1 = iron, 2 = gold, 3 = diamond
        //compound = new CompoundNBT();
        //compound.write();
        this.blades = blades;
        this.shell = shell;
        this.core = core;

    }



    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new StringTextComponent("Blades: " + blades));
        tooltip.add(new StringTextComponent("Blade shell: " + shell));
        tooltip.add(new StringTextComponent("Blade core: " + core));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        if (worldIn.isRemote) {
            return new ActionResult<>(ActionResultType.PASS, itemstack);
        } else {
            RayTraceResult raytraceresult = rayTrace(worldIn, playerIn, RayTraceContext.FluidMode.SOURCE_ONLY);
            if (raytraceresult.getType() != RayTraceResult.Type.BLOCK) {
                return new ActionResult<>(ActionResultType.PASS, itemstack);
            } else {
                BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult)raytraceresult;
                BlockPos blockpos = blockraytraceresult.getPos();
                if (!(worldIn.getBlockState(blockpos).getBlock() instanceof FlowingFluidBlock)) {
                    return new ActionResult<>(ActionResultType.PASS, itemstack);
                } else if (worldIn.isBlockModifiable(playerIn, blockpos) && playerIn.canPlayerEdit(blockpos, blockraytraceresult.getFace(), itemstack)) {
                    EntityType<?> entitytype = this.getType(itemstack.getTag());
                    if (entitytype.spawn(worldIn, itemstack, playerIn, blockpos, SpawnReason.SPAWN_EGG, false, false) == null) {
                        return new ActionResult<>(ActionResultType.PASS, itemstack);
                    } else {
                        if (!playerIn.abilities.isCreativeMode) {
                            itemstack.shrink(1);
                        }

                        playerIn.addStat(Stats.ITEM_USED.get(this));
                        return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
                    }
                } else {
                    return new ActionResult<>(ActionResultType.FAIL, itemstack);
                }
            }
        }
    }


}