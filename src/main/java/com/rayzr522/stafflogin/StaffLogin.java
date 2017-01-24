package com.rayzr522.stafflogin;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class StaffLogin extends JavaPlugin {

    private static final Pattern ENCRYPTED_PATTERN = Pattern.compile("\\d+:[a-z0-9]+:[a-z0-9]+");

    private Map<UUID, String> passwords = new HashMap<>();
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

    /**
     * Attempts to load all information from the config;
     * 
     * @return {@code true}, unless something goes wrong.
     */
    public boolean load() {
        FileConfiguration config = getConfig();
        if (config.isConfigurationSection("passwords")) {
            passwords.clear();
            ConfigurationSection passwordsSection = config.getConfigurationSection("passwords");
            passwordsSection.getKeys(false).forEach(k -> {
                String password = passwordsSection.getString(k);
                if (!ENCRYPTED_PATTERN.matcher(password).matches()) {
                    getLogger().warning(String.format("Invalid password for UUID %s: %s", k, password));
                } else {
                    passwords.put(UUID.fromString(k), password);
                }
            });
        }

        ConfigurationSection messagesSection = config.getConfigurationSection("messages");
        String prefix = config.getString("prefix");

        messagesSection.getKeys(false).forEach(k -> {
            messages.put(k, ChatColor.translateAlternateColorCodes('&', prefix + " " + messagesSection.get(k)));
        });

        return true;
    }

    /**
     * Saves all important data to the config file.
     */
    public void save() {
        passwords.entrySet().stream().forEach(e -> {
            getConfig().set("passwords." + e.getKey(), e.getValue());
        });
        saveConfig();
    }

    /**
     * @return The map of passwords
     */
    public Map<UUID, String> getPasswords() {
        return passwords;
    }

    /**
     * @return The map of logged-in player
     */
    public Map<UUID, Boolean> getLoggedIn() {
        return loggedIn;
    }

    /**
     * Logs a player out
     * 
     * @param player The player to log out
     * @return Whether or not the player was logged in
     */
    public boolean logOut(Player player) {
        return loggedIn.put(player.getUniqueId(), false);
    }

    /**
     * Attempt to log a player in
     * 
     * @param player The player to attempt to log in
     * @param password The password to use
     * @return Whether or not the player was logged in. This can return {@code false} for many reasons, including not having set a password, already being logged in, and of course, providing the wrong password.
     */
    public boolean logIn(Player player, String password) {
        if (!passwords.containsKey(player.getUniqueId()))
            return false;
        if (loggedIn.getOrDefault(player.getUniqueId(), false))
            return true;
        if (!Encrypter.check(password, passwords.get(player.getUniqueId())))
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
     * @return Returns a message from the config with the given key
     */
    public String tr(String key) {
        return messages.get(key);
    }

    /**
     * This returns true if they have yet to set a password or if they are already logged in.
     * 
     * @param player Player to check
     * @return Whether or not a player is allowed to set a new password
     */
    public boolean canSetPassword(Player player) {
        return !passwords.containsKey(player.getUniqueId()) || loggedIn.getOrDefault(player.getUniqueId(), false);
    }

    /**
     * Attempts to set the password for a player. The plain-text password is encrypted via {@link Encrypter#apply(String, byte[], int, int) this method}.
     * 
     * @param player The player to set the password for
     * @param password The plain-text password to set
     * @return Whether or not the password was set (returns false if {@link #canSetPassword(Player)} returns <code>false</code>)
     */
    public boolean setPassword(Player player, String password) {
        if (!canSetPassword(player)) {
            return false;
        }

        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);

        passwords.put(player.getUniqueId(), Encrypter.apply(password, salt, 2560, 512));
        loggedIn.put(player.getUniqueId(), true);
        return true;
    }

}
