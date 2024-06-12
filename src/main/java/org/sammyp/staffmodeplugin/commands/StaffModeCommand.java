package org.sammyp.staffmodeplugin.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.sammyp.staffmodeplugin.managers.StaffModeManager;

public class StaffModeCommand implements CommandExecutor {

    private StaffModeManager staffModeManager;
    private String prefix;

    public StaffModeCommand(StaffModeManager staffModeManager, String prefix) {
        this.staffModeManager = staffModeManager;
        this.prefix = prefix;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("on")) {
                staffModeManager.enableStaffMode(player);
                return true;
            } else if (args[0].equalsIgnoreCase("off")) {
                staffModeManager.disableStaffMode(player);
                return true;
            }
        }

        player.sendMessage(ChatColor.RED + "Usage: /staffmode <on/off>");
        return true;
    }
}