package fr.leconsulat.api.nms.api;

import fr.leconsulat.api.nbt.CompoundTag;
import fr.leconsulat.api.nbt.ListTag;
import org.bukkit.entity.Entity;

public interface Player {
    ListTag<CompoundTag> getEffectsAsTag(org.bukkit.entity.Player player);
    
    boolean pickup(org.bukkit.entity.Player player, Entity entity);
    
    void addEffect(org.bukkit.entity.Player player, CompoundTag effect);
}
