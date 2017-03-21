package eyeq.crumbs.entity;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import eyeq.crumbs.event.CrumbsEventHandler;

public class EntityBait extends Entity {
    public EntityBait(World world) {
        super(world);
        this.setSize(0F, 0F);
    }

    public EntityBait(World world, double x, double y, double z) {
        this(world);
        this.setPosition(x, y, z);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if(this.world.isRemote) {
            return;
        }
        BlockPos pos = this.getPosition();
        if(this.world.getBlockState(pos).getMaterial() != Material.WATER) {
            this.setPosition(posX, posY - 1, posZ);
        } else if(this.world.getBlockState(pos.up()).getMaterial() == Material.WATER) {
            this.setPosition(posX, posY + 1, posZ);
        }
        for(EntityFishHook entity : this.world.getEntitiesWithinAABB(EntityFishHook.class, this.getEntityBoundingBox().expand(8.0, 4.0, 8.0))) {
            if(entity.caughtEntity == null) {
                CrumbsEventHandler.addTarget(entity);
            }
        }
        if(this.rand.nextInt(600) == 0) {
            this.setDead();
        }
    }

    @Override
    protected void entityInit() {}

    @Override
    protected void readEntityFromNBT(NBTTagCompound tagCompund) {}

    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound) {}
}
