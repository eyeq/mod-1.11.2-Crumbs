package eyeq.crumbs.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import eyeq.crumbs.Crumbs;
import eyeq.util.item.UItemFood;
import net.minecraft.util.EnumHand;

public class ItemCrumbs extends UItemFood {
    public ItemCrumbs(int amount, float saturation, boolean isWolfFood) {
        super(amount, saturation, isWolfFood);
        this.setMaxItemUseDuration(1);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack itemStack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
        if(player.world.isRemote) {
            return false;
        }
        if(!(target instanceof EntityChicken)) {
            return false;
        }
        if(itemStack.getItem() != Crumbs.crumbs) {
            return false;
        }
        EntityChicken chicken = (EntityChicken) target;
        if(chicken.getGrowingAge() == 0 && !chicken.isInLove()) {
            chicken.setInLove(player);
        } else if(chicken.isChild()) {
            chicken.ageUp((int) ((-chicken.getGrowingAge() / 20) * 0.1F), true);
        } else {
            return false;
        }
        if(!player.isCreative()) {
            itemStack.shrink(1);
        }
        return true;
    }
}
