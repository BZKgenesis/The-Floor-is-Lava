package net.bzkgns.theFloorIsLavaManager.currency;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MoneyManager {

    private final Map<UUID, PlayerBalance> playerBalances;

    public MoneyManager() {
        this.playerBalances = new HashMap<>();
    }

    public PlayerBalance getBalance(UUID playerUUID) {
        if (!playerBalances.containsKey(playerUUID)) {
            PlayerBalance newBalance = new PlayerBalance(0, 0, 0);
            playerBalances.put(playerUUID, newBalance);
            return newBalance;
        } else{
            return playerBalances.get(playerUUID);
        }
    }

    public void setBalance(UUID playerUUID, Integer material, Integer resource, Integer money) {
        if (playerBalances.containsKey(playerUUID)){
            playerBalances.get(playerUUID).set(material, resource, money);
        } else {
            playerBalances.put(playerUUID, new PlayerBalance(0, 0, 0));
        }
    }

    public void addBalance(UUID playerUUID, Price amountToAdd) {
        PlayerBalance currentBalance = getBalance(playerUUID);
        currentBalance.add(amountToAdd);
    }

    public boolean subtractBalance(UUID playerUUID, Price amountToSubtract) {
        PlayerBalance currentBalance = getBalance(playerUUID);
        return currentBalance.pay(amountToSubtract);
    }

    public boolean hasEnoughBalance(UUID playerUUID, @Nullable Price requiredAmount) {
        return getBalance(playerUUID).hasEnough(requiredAmount);
    }
}
