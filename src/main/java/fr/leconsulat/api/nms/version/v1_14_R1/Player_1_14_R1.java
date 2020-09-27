package fr.leconsulat.api.nms.version.v1_14_R1;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.nbt.CompoundTag;
import fr.leconsulat.api.nbt.ListTag;
import fr.leconsulat.api.nbt.NBTType;
import fr.leconsulat.api.nms.api.NBT;
import fr.leconsulat.api.nms.api.Player;
import net.minecraft.server.v1_14_R1.EntityArrow;
import net.minecraft.server.v1_14_R1.MobEffect;
import net.minecraft.server.v1_14_R1.MobEffectList;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Player_1_14_R1 implements Player {
    
    @Override
    public boolean pickup(org.bukkit.entity.Player player, Entity entity){
        switch(entity.getType()){
            case ARROW:
            case SPECTRAL_ARROW:
            case DROPPED_ITEM:
            case TRIDENT:
                break;
            default:
                return true;
        }
        if(entity.getType() != EntityType.DROPPED_ITEM){
            EntityArrow arrowEntity = (EntityArrow)((CraftEntity)entity).getHandle();
            arrowEntity.shake = 0;
            arrowEntity.inGround = true;
            if(arrowEntity.fromPlayer != EntityArrow.PickupStatus.ALLOWED){
                return false;
            }
        }
        ((CraftEntity)entity).getHandle().pickup(((CraftPlayer)player).getHandle());
        return true;
    }
    
    @Override
    public void addEffect(org.bukkit.entity.Player player, CompoundTag effect){
        MobEffect mobEffect = MobEffect.b((NBTTagCompound)ConsulatAPI.getNMS().getNBT().toNMS(effect));
        player.addPotionEffect(new PotionEffect(PotionEffectType.getById(MobEffectList.getId(mobEffect.getMobEffect())), mobEffect.getDuration(), mobEffect.getAmplifier(), mobEffect.isAmbient(), mobEffect.isShowParticles()));
    }
    
    @Override
    public ListTag<CompoundTag> getEffectsAsTag(org.bukkit.entity.Player player){
        NBT nbtNMS = ConsulatAPI.getNMS().getNBT();
        ListTag<CompoundTag> effects = new ListTag<>(NBTType.COMPOUND);
        for(MobEffect effect : ((CraftPlayer)player).getHandle().effects.values()){
            NBTTagCompound effectTag = (NBTTagCompound)nbtNMS.toNMS(new CompoundTag());
            effect.a(effectTag);
            effects.addTag(nbtNMS.nmsToCompound(effectTag));
        }
        return effects;
    }
    
}
