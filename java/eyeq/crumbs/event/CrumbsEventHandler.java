package eyeq.crumbs.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

import eyeq.crumbs.Crumbs;
import eyeq.crumbs.entity.EntityBait;

public class CrumbsEventHandler {
    private static final List<EntityItem> crumbsList = new ArrayList<>();
    private static final Map<EntityFishHook, Integer> fishHooksMap = new HashMap<>();

    public static void addTarget(EntityFishHook entity) {
        int n;
        if(fishHooksMap.containsKey(entity)) {
            n = fishHooksMap.get(entity);
        } else {
            n = 0;
        }
        fishHooksMap.put(entity, n + 1);
    }

    @SubscribeEvent
    public void onWorldTick(WorldTickEvent event) {
        if(event.phase != Phase.START) {
            return;
        }
        World world = event.world;
        if(world.isRemote) {
            return;
        }
        List<EntityItem> removeList = new ArrayList<>();
        for(EntityItem entity : crumbsList) {
            if(entity.world != world) {
                continue;
            }
            if(entity.isDead) {
                removeList.add(entity);
                continue;
            }
            if(entity.isInWater()) {
                removeList.add(entity);
                world.spawnEntity(new EntityBait(world, entity.posX, entity.posY, entity.posZ));
            }
        }
        for(EntityItem entity : removeList) {
            ItemStack itemStack = entity.getEntityItem();
            if(itemStack.getCount() > 1) {
                itemStack.shrink(1);
            } else {
                crumbsList.remove(entity);
                entity.setDead();
            }
        }
        for(EntityFishHook entity : fishHooksMap.keySet()) {
            int num = (int) (fishHooksMap.get(entity) * Math.random());
            if(num > 10) {
                num = 10;
            }
            double posX = entity.posX;
            double posY = entity.posY;
            double posZ = entity.posZ;
            double motionX = entity.motionX;
            double motionY = entity.motionY;
            double motionZ = entity.motionZ;
            float prebRotationYaw = entity.prevRotationYaw;
            float prevRotationPitch = entity.prevRotationPitch;
            float rotationYaw = entity.rotationYaw;
            float rotationPitch = entity.rotationPitch;
            for(int i = 0; i < num; i++) {
                if(getTicksCatchable(entity) > 0) {
                    break;
                }
                entity.onUpdate();
            }
            entity.setPosition(posX, posY, posZ);
            entity.setVelocity(motionX, motionY, motionZ);
            entity.prevRotationYaw = prebRotationYaw;
            entity.prevRotationPitch = prevRotationPitch;
            entity.rotationYaw = rotationYaw;
            entity.rotationPitch = rotationPitch;
        }
        fishHooksMap.clear();
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if(event.getWorld().isRemote) {
            return;
        }
        Entity entity = event.getEntity();
        if(entity instanceof EntityChicken) {
            EntityCreature chicken = (EntityChicken) entity;
            chicken.tasks.addTask(3, new EntityAITempt(chicken, 1.0, Crumbs.crumbs, false));
        } else if(entity instanceof EntityItem) {
             if(((EntityItem) entity).getEntityItem().getItem() == Crumbs.crumbs) {
                 crumbsList.add((EntityItem) entity);
             }
        }
    }

    public static int getTicksCatchable(EntityFishHook entity) {
        return ObfuscationReflectionHelper.getPrivateValue(EntityFishHook.class, entity, "ticksCatchable", "field_146045_ax");
    }
}
