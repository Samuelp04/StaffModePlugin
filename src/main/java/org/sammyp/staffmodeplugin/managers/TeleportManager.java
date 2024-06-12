package org.sammyp.staffmodeplugin.managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class TeleportManager {

    private final Inventory teleportGUI;

    public TeleportManager() {
        this.teleportGUI = Bukkit.createInventory(null, 54, ChatColor.BLUE + "Teleport to Player");
    }

    public void openTeleportGUI(Player player) {
        teleportGUI.clear();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            ItemStack playerHead = new ItemStack(org.bukkit.Material.PLAYER_HEAD);
            ItemMeta meta = playerHead.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + onlinePlayer.getName());
            playerHead.setItemMeta(meta);
            teleportGUI.addItem(playerHead);
        }
        player.openInventory(teleportGUI);
    }

    public void handleTeleport(Player player, ItemStack clickedItem) {
        if (clickedItem != null && clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()) {
            String targetName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
            Player target = Bukkit.getPlayer(targetName);
            if (target != null) {
                player.teleport(target.getLocation());
                player.sendMessage(ChatColor.GREEN + "Teleported to " + targetName);
            } else {
                player.sendMessage(ChatColor.RED + "Could not find player " + targetName);
            }
        }
    }
}