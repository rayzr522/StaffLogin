/**
 * 
 */
package com.rayzr522.stafflogin.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.rayzr522.stafflogin.StaffLogin;

/**
 * @author Rayzr
 *
 */
public class CommandPasswordReset implements CommandExecutor {

    private StaffLogin plugin;

    /**
     * @param plugin The {@link StaffLogin} instance
     */
    public CommandPasswordReset(StaffLogin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(plugin.tr("usage-password"));
            return true;
        }

        @SuppressWarnings("deprecation")
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target == null) {
            sender.sendMessage(plugin.tr("no-such-player"));
            return true;
        }

        plugin.getPasswords().remove(target.getUniqueId());
        plugin.logOut(target);

        sender.sendMessage(plugin.tr("password-reset"));

        return true;
    }

}
