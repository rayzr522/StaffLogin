/**
 * 
 */
package com.rayzr522.stafflogin.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.rayzr522.stafflogin.StaffLogin;

/**
 * @author Rayzr
 *
 */
public class CommandLogout implements CommandExecutor {

    private StaffLogin plugin;

    /**
     * @param staffLogin
     */
    public CommandLogout(StaffLogin plugin) {
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

        if (plugin.logOut(player)) {
            player.sendMessage(plugin.tr("logged-out"));
        } else {
            player.sendMessage(plugin.tr("not-logged-in"));
        }

        return true;
    }

}
