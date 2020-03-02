package fr.leconsulat.api.economy;

import org.bukkit.Bukkit;

import java.util.UUID;

public class ShopInfo {

    private int x, y, z, price;
    private String material, owner_uuid;

    public ShopInfo(int x, int y, int z, String material, int price, String owner_uuid) {
        this.x = x;
        this.z = z;
        this.y = y;
        this.material = material;
        this.price = price;
        this.owner_uuid = owner_uuid;
    }

    public int getShopX() {
        return x;
    }

    public int getShopZ() {
        return z;
    }

    public int getShopY() { return y; }

    public String getShopMaterial() {
        return material;
    }

    public int getShopPrice() {
        return price;
    }

    public String getShopOwnerUUID() {
        return owner_uuid;
    }

    public String getShopPlayerName() {
        return Bukkit.getOfflinePlayer(UUID.fromString(owner_uuid)).getName();
    }

}
