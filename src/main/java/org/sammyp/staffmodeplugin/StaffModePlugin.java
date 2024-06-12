package org.sammyp.staffmodeplugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.sammyp.staffmodeplugin.commands.StaffModeCommand;
import org.sammyp.staffmodeplugin.listeners.StaffModeListener;
import org.sammyp.staffmodeplugin.managers.FreezeManager;
import org.sammyp.staffmodeplugin.managers.StaffModeManager;
import org.sammyp.staffmodeplugin.managers.TeleportManager;
import org.sammyp.staffmodeplugin.managers.VanishManager;

public class StaffModePlugin extends JavaPlugin {

    private StaffModeManager staffModeManager;
    private FreezeManager freezeManager;
    private String prefix;
    private VanishManager vanishManager;
    private TeleportManager teleportManager;

    @Override
    public void onEnable() {
        // Initialize config
        saveDefaultConfig();
        reloadConfig();

        // Load prefix from config
        prefix = getConfig().getString("plugin.prefix", "&7[&bStaffMode&7] ");

        // Initialize StaffModeManager
        staffModeManager = new StaffModeManager(this, freezeManager, vanishManager, teleportManager);
        freezeManager = new FreezeManager(this);
        vanishManager = new VanishManager();
        teleportManager = new TeleportManager();
        // Register commands
        getCommand("staffmode").setExecutor(new StaffModeCommand(staffModeManager, prefix));

        // Register events
        new StaffModeListener(this, freezeManager, vanishManager, teleportManager,staffModeManager);
    }

    @Override
    public void onDisable() {
        // Disable any ongoing tasks, save data, etc.
        if (staffModeManager != null) {
            staffModeManager.disable(); // Ensure staffModeManager is not null before calling disable()
        }
    }

    public StaffModeManager getStaffModeManager() {
        return staffModeManager;
    }

    public String getPrefix() {
        return prefix;
    }

    public FreezeManager getFreezeManager() {
        return freezeManager;
    }
}
