package eyeq.crumbs.item;

import eyeq.util.world.WorldUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.*;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import eyeq.crumbs.Crumbs;

public class ItemFishingGloves extends ItemSword {
    public ItemFishingGloves(ToolMaterial material) {
        super(material);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack itemStack, World world, EntityLivingBase entity, int timeLeft) {
        if(!(entity instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = ((EntityPlayer) entity);
        int index = player.inventory.getSlotFor(new ItemStack(Crumbs.crumbs));
        if(!player.isCreative() && index == -1) {
            return;
        }
        int charge = this.getMaxItemUseDuration(itemStack) - timeLeft;
        float velocity = ItemBow.getArrowVelocity(charge);
        if(velocity < 0.2F) {
            return;
        }
        WorldUtils.playSound(world, player, SoundEvents.ENTITY_ARROW_SHOOT, itemRand);
        if(!player.isCreative()) {
            player.inventory.decrStackSize(index, 1);
        }
        player.addStat(StatList.getObjectUseStats(this));
        if(world.isRemote) {
            return;
        }
        float rotationYaw = player.rotationYaw / 180.0F * (float) Math.PI;
        float rotationYawSin = MathHelper.sin(rotationYaw);
        float rotationYawCos = MathHelper.cos(rotationYaw);
        EntityItem entityItem = new EntityItem(world, player.posX - rotationYawCos * 1.5F, player.posY + player.getEyeHeight() - 0.1, player.posZ - rotationYawSin * 1.5F, new ItemStack(Crumbs.crumbs));
        float rotationPitch = player.rotationPitch / 180.0F * (float) Math.PI;
        double motionX = -rotationYawSin * MathHelper.cos(rotationPitch);
        double motionY = -MathHelper.sin(rotationPitch);
        double motionZ = rotationYawCos * MathHelper.cos(rotationPitch);
        float f2 = MathHelper.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
        velocity = velocity * 2.0F / f2;
        entityItem.setVelocity(motionX * velocity, motionY * velocity, motionZ * velocity);
        world.spawnEntity(entityItem);
        itemStack.damageItem(1, player);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 36000;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
        if(!player.isCreative() && !player.inventory.hasItemStack(new ItemStack(Crumbs.crumbs))) {
            return new ActionResult<>(EnumActionResult.FAIL, itemStack);
        }
        player.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
    }
}
