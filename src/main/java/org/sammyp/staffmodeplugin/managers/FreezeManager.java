package org.sammyp.staffmodeplugin.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class FreezeManager {

    private final JavaPlugin plugin;
    private Scoreboard scoreboard;
    private Team frozenTeam;

    public FreezeManager(JavaPlugin plugin) {
        this.plugin = plugin;
        setupScoreboard();
    }

    private void setupScoreboard() {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        if (scoreboardManager != null) {
            scoreboard = scoreboardManager.getMainScoreboard();
            frozenTeam = scoreboard.getTeam("FrozenPlayers");
            if (frozenTeam == null) {
                frozenTeam = scoreboard.registerNewTeam("FrozenPlayers");
            }
        } else {
            plugin.getLogger().warning("Scoreboard manager not found! Freezing functionality may not work.");
        }
    }

    public void freezePlayer(Player player) {
        if (frozenTeam == null) {
            plugin.getLogger().warning("Frozen team not initialized! Freezing functionality may not work.");
            return;
        }

        // Add player to frozen team
        frozenTeam.addEntry(player.getName());

        // Implement visual indication or other actions for freezing the player
        player.sendMessage("You have been frozen!");

        // You can also cancel player movement or actions here if needed
    }

    public void unfreezePlayer(Player player) {
        if (frozenTeam == null) {
            plugin.getLogger().warning("Frozen team not initialized! Unfreezing functionality may not work.");
            return;
        }

        // Remove player from frozen team
        frozenTeam.removeEntry(player.getName());

        // Implement any actions needed to unfreeze the player
        player.sendMessage("You have been unfrozen!");
    }

    public boolean isPlayerFrozen(Player player) {
        return frozenTeam != null && frozenTeam.hasEntry(player.getName());
    }
}
