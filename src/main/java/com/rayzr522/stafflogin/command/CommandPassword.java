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
public class CommandPassword implements CommandExecutor {

    private StaffLogin plugin;

    /**
     * @param plugin The {@link StaffLogin} instance
     */
    public CommandPassword(StaffLogin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this!");
            return true;
        }
        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage(plugin.tr("usage-password"));
            return true;
        }

        if (!plugin.setPassword(player, args[0])) {
            player.sendMessage(plugin.tr("login-change-pass"));
            return true;
        }

        player.sendMessage(plugin.tr("password-set"));

        return true;
    }

}