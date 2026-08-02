package net.bzkgns.theFloorIsLava.config.shop;

public record ShopBuyCustomItem(String id, int quantity, int resource, int material) {
    public ShopBuyCustomItem(String id, int resource, int material) {
        this(id, 1, resource, material);
    }
}
