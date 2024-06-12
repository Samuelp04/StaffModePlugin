package org.sammyp.staffmodeplugin.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.sammyp.staffmodeplugin.managers.FreezeManager;
import org.sammyp.staffmodeplugin.managers.StaffModeManager;
import org.sammyp.staffmodeplugin.managers.TeleportManager;
import org.sammyp.staffmodeplugin.managers.VanishManager;

public class StaffModeListener implements Listener {

    private final JavaPlugin plugin;
    private StaffModeManager staffModeManager;
    private FreezeManager freezeManager;
    private VanishManager vanishManager;
    private TeleportManager teleportManager;

    public StaffModeListener(JavaPlugin plugin, FreezeManager freezeManager, VanishManager vanishManager, TeleportManager teleportManager, StaffModeManager staffModeManager) {
        this.plugin = plugin;
        this.staffModeManager = staffModeManager;
        this.freezeManager = freezeManager;
        this.vanishManager = vanishManager;
        this.teleportManager = teleportManager;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        staffModeManager.disableStaffMode(event.getPlayer()); // Ensure player starts without staff mode
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        staffModeManager.disableStaffMode(event.getPlayer()); // Clean up when player leaves
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        Action action = event.getAction();


        // Freeze Player item
        if (event.getItem().getType().equals(Material.getMaterial(this.plugin.getConfig().getString("items.freeze-player").toUpperCase()))){
            event.getPlayer().sendMessage(ChatColor.RED + "Right click a player to freeze them!!");

        }


        // Check Inventory item
        ConfigurationSection checkInventoryConfig = plugin.getConfig().getConfigurationSection("items.check-inventory");
        if (checkInventoryConfig != null && staffModeManager.isItemMatchingConfig(item, checkInventoryConfig)) {
            if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                if (player.getItemInHand().equals(Material.getMaterial(String.valueOf(checkInventoryConfig)))) {
                    player.sendMessage(ChatColor.YELLOW + "Right-click on a player to check their inventory.");
                    // Implement check inventory logic here
                }
            }
        }

        // Vanish item
        ConfigurationSection vanishConfig = plugin.getConfig().getConfigurationSection("items.vanish-toggle");
        if (vanishConfig != null && staffModeManager.isItemMatchingConfig(item, vanishConfig)) {
            if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                if (player.getItemInHand().equals(Material.getMaterial(String.valueOf(vanishConfig)))) {
                    if (vanishManager.isPlayerVanished(player)) {
                        vanishManager.unvanishPlayer(player);
                        player.sendMessage(ChatColor.GREEN + "You are now visible.");
                    } else {
                        vanishManager.vanishPlayer(player);
                        player.sendMessage(ChatColor.GREEN + "You are now vanished.");
                    }
                }
            }
        }

        // Teleport item
        ConfigurationSection teleportConfig = plugin.getConfig().getConfigurationSection("items.teleport-gui");
        if (teleportConfig != null && staffModeManager.isItemMatchingConfig(item, teleportConfig)) {
            if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                if (player.getItemInHand().equals(Material.getMaterial(String.valueOf(teleportConfig)))) {
                    teleportManager.openTeleportGUI(player);
                }
            }
        }
    }


    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();


        if (event.getRightClicked() instanceof Player) {
            Player target = (Player) event.getRightClicked();


            if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                String displayName = item.getItemMeta().getDisplayName();

                // Freeze Player item
                ConfigurationSection freezeConfig = plugin.getConfig().getConfigurationSection("items.teleport-gui");
                if (player.getItemInHand().equals(Material.getMaterial(String.valueOf(freezeConfig)))) {
                    if (freezeManager.isPlayerFrozen(target)) {
                        freezeManager.unfreezePlayer(target);
                    } else {
                        freezeManager.freezePlayer(target);
                    }
                }

                // Check Inventory item
                ConfigurationSection checkInventoryConfig = plugin.getConfig().getConfigurationSection("items.check-inventory");
                if (player.getItemInHand().equals(Material.getMaterial(String.valueOf(checkInventoryConfig)))) {
                    Inventory targetInventory = target.getInventory();
                    player.openInventory(targetInventory);
                    player.sendMessage(ChatColor.GREEN + "You are now viewing " + target.getName() + "'s inventory.");
                }


            }
        }
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        String inventoryTitle = event.getView().getTitle();

        if (inventoryTitle.equals(ChatColor.BLUE + "Teleport to Player")) {
            event.setCancelled(true); // Prevent taking items from the GUI
            teleportManager.handleTeleport(player, clickedItem);
        }
    }
}
