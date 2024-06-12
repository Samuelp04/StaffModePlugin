package org.sammyp.staffmodeplugin.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class VanishManager {

    private final Set<Player> vanishedPlayers = new HashSet<>();

    public void vanishPlayer(Player player) {
        vanishedPlayers.add(player);
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.hidePlayer(player);
        }
    }

    public void unvanishPlayer(Player player) {
        vanishedPlayers.remove(player);
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.showPlayer(player);
        }
    }

    public boolean isPlayerVanished(Player player) {
        return vanishedPlayers.contains(player);
    }

    public void disable() {
        for (Player player : vanishedPlayers) {
            unvanishPlayer(player);
        }
        vanishedPlayers.clear();
    }
}
