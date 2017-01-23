/**
 * 
 */
package com.rayzr522.stafflogin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Rayzr
 *
 */
public class CommandLogin implements CommandExecutor {

    private StaffLogin plugin;

    /**
     * @param staffLogin
     */
    public CommandLogin(StaffLogin plugin) {
        this.plugin = plugin;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this!");
            return true;
        }
        Player player = (Player) sender;

        if (!plugin.getPasswords().containsKey(player.getUniqueId())) {
            player.sendMessage(plugin.tr("create-password"));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(plugin.tr("usage-login"));
            return true;
        }

        if (plugin.getLoggedIn().getOrDefault(player.getUniqueId(), false)) {
            player.sendMessage(plugin.tr("already-logged-in"));
            return true;
        }

        if (!plugin.logIn(player, args[0].hashCode())) {
            player.sendMessage(plugin.tr("wrong-password"));
            return true;
        }

        player.sendMessage(plugin.tr("logged-in"));

        return true;
    }

}
