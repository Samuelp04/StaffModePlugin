package org.sammyp.staffmodeplugin.managers;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StaffModeManager {

    private Map<UUID, Boolean> staffModePlayers = new HashMap<>();
    private JavaPlugin plugin;
    private FileConfiguration config;
    private StaffModeManager staffModeManager;
    private FreezeManager freezeManager;
    private VanishManager vanishManager;
    private TeleportManager teleportManager;

    // Configuration defaults
    private String enabledMessage;
    private String disabledMessage;

    public StaffModeManager(JavaPlugin plugin, FreezeManager freezeManager, VanishManager vanishManager, TeleportManager teleportManager) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.config.options().copyDefaults(true); // Load default configuration
        plugin.saveDefaultConfig(); // Save default config if not exists
        loadConfiguration();
        this.freezeManager = freezeManager;
        this.vanishManager = vanishManager;
        this.teleportManager = teleportManager;
    }

    private void loadConfiguration() {
        ConfigurationSection pluginConfig = config.getConfigurationSection("plugin");
        if (pluginConfig != null) {
            enabledMessage = pluginConfig.getString("enabled-message", "&aStaff mode enabled."); // Default message if not found
            disabledMessage = pluginConfig.getString("disabled-message", "&aStaff mode disabled."); // Default message if not found
        } else {
            plugin.getLogger().warning("Plugin configuration section 'plugin' not found in config.yml!");
            enabledMessage = "&aStaff mode enabled."; // Default message if configuration is missing
            disabledMessage = "&aStaff mode disabled."; // Default message if configuration is missing
        }

        ConfigurationSection itemsSection = config.getConfigurationSection("staff-mode.items");
        if (itemsSection != null) {
            for (String key : itemsSection.getKeys(false)) {
                ConfigurationSection itemConfig = itemsSection.getConfigurationSection(key);
                if (itemConfig != null) {
                    ItemStack item = createItemFromConfig(itemConfig, key);
                    int slot = itemConfig.getInt("slot");
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        plugin.getLogger().info("Loading " + key + " into slot " + slot);
                        plugin.getLogger().info("Item: " + item.toString());
                    });
                }
            }
        } else {
            plugin.getLogger().warning("Plugin configuration section 'staff-mode.items' not found in config.yml!");
        }
    }

    public ItemStack createItemFromConfig(ConfigurationSection itemConfig, String key) {
        Material material = Material.getMaterial(itemConfig.getString("material", "STONE")); // Default to STONE if material is null
        if (material == null) {
            plugin.getLogger().warning("Invalid material '" + itemConfig.getString("material") + "' specified for item '" + key + "' in config.yml!");
            material = Material.STONE; // Provide a fallback Material
        }

        String displayName = itemConfig.getString("display-name", "&cUnnamed Item"); // Default display name if not found
        displayName = org.bukkit.ChatColor.translateAlternateColorCodes('&', displayName); // Translate color codes

        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(displayName);
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public void enableStaffMode(Player player) {
        staffModePlayers.put(player.getUniqueId(), true);

        // Clear inventory
        player.getInventory().clear();

        // Give staff items
        giveStaffItems(player);

        // Send enabled message
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', enabledMessage));
    }

    public void disableStaffMode(Player player) {
        staffModePlayers.put(player.getUniqueId(), false);

        // Restore player's inventory, clear effects, etc.
        // Here, you might want to store the player's original inventory before clearing it.
        player.getInventory().clear();

        player.setGameMode(GameMode.CREATIVE);

        // Send disabled message
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', enabledMessage));
    }

    public boolean isInStaffMode(Player player) {
        return staffModePlayers.getOrDefault(player.getUniqueId(), false);
    }

    public void disable() {
        staffModePlayers.clear();
    }

    private void giveStaffItems(Player player) {
        ConfigurationSection itemsSection = config.getConfigurationSection("staff-mode.items");
        if (itemsSection != null) {
            for (String key : itemsSection.getKeys(false)) {
                ConfigurationSection itemConfig = itemsSection.getConfigurationSection(key);
                if (itemConfig != null) {
                    ItemStack item = createItemFromConfig(itemConfig, key);
                    int slot = itemConfig.getInt("slot");
                    player.getInventory().setItem(slot, item);
                }
            }
        } else {
            plugin.getLogger().warning("Plugin configuration section 'staff-mode.items' not found in config.yml!");
        }
    }

    public boolean isItemMatchingConfig(ItemStack item, ConfigurationSection itemConfig) {
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
            return false;
        }

        Material material = Material.getMaterial(itemConfig.getString("material", "STONE"));
        if (material == null) {
            material = Material.STONE;
        }

        String displayName = itemConfig.getString("display-name", "&cUnnamed Item");
        displayName = org.bukkit.ChatColor.translateAlternateColorCodes('&', displayName);

        return item.getType() == material && item.getItemMeta().getDisplayName().equals(displayName);
    }

}