package com.rayzr522.stafflogin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class StaffLogin extends JavaPlugin {

    private Map<UUID, Integer> passwords = new HashMap<>();
    private Map<UUID, Boolean> loggedIn = new HashMap<>();
    private Map<String, String> messages = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        if (!load()) {
            getLogger().severe("Failed to load the config!");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        long delay = TimeUnit.MINUTES.toSeconds(15) * 20;
        new BukkitRunnable() {
            @Override
            public void run() {
                save();
            }
        }.runTaskTimer(this, delay, delay);

        Bukkit.getPluginManager().registerEvents(new EventListener(this), this);

        getCommand("login").setExecutor(new CommandLogin(this));
        getCommand("logout").setExecutor(new CommandLogout(this));
        getCommand("password").setExecutor(new CommandPassword(this));
    }

    @Override
    public void onDisable() {
        save();
    }

    public boolean load() {
        FileConfiguration config = getConfig();
        if (config.isConfigurationSection("passwords")) {
            passwords.clear();
            ConfigurationSection passwordsSection = config.getConfigurationSection("passwords");
            passwordsSection.getKeys(false).forEach(k -> {
                passwords.put(UUID.fromString(k), passwordsSection.getInt(k));
            });
        }

        ConfigurationSection messagesSection = config.getConfigurationSection("messages");
        String prefix = config.getString("prefix");

        messagesSection.getKeys(false).forEach(k -> {
            messages.put(k, ChatColor.translateAlternateColorCodes('&', prefix + " " + messagesSection.get(k)));
        });

        return true;
    }

    public void save() {
        passwords.entrySet().stream().forEach(e -> {
            getConfig().set("passwords." + e.getKey(), e.getValue());
        });
        saveConfig();
    }

    /**
     * @return the passwords
     */
    public Map<UUID, Integer> getPasswords() {
        return passwords;
    }

    /**
     * @return the loggedIn
     */
    public Map<UUID, Boolean> getLoggedIn() {
        return loggedIn;
    }

    public boolean logOut(Player player) {
        return loggedIn.put(player.getUniqueId(), false);
    }

    public boolean logIn(Player player, int password) {
        if (!passwords.containsKey(player.getUniqueId()))
            return false;
        if (loggedIn.getOrDefault(player.getUniqueId(), false))
            return true;
        if (passwords.get(player.getUniqueId()) != password)
            return false;
        loggedIn.put(player.getUniqueId(), true);
        return true;
    }

    /**
     * @param player The player to check
     * @return Whether or not a player should be prevented from logging in
     */
    public boolean shouldPrevent(Player player) {
        return player != null && player.hasPermission("StaffLogin.require") && !loggedIn.getOrDefault(player.getUniqueId(), false);
    }

    /**
     * @return The custom prevention message
     */
    public String tr(String key) {
        return messages.get(key);
    }

    /**
     * @param player
     * @return
     */
    public boolean canSetPassword(Player player) {
        return !passwords.containsKey(player.getUniqueId()) || loggedIn.get(player.getUniqueId());
    }

    /**
     * @param player
     * @return
     */
    public boolean setPassword(Player player, int password) {
        if (!canSetPassword(player)) {
            return false;
        }
        passwords.put(player.getUniqueId(), password);
        loggedIn.put(player.getUniqueId(), true);
        return true;
    }

}
