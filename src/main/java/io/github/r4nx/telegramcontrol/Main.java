package io.github.r4nx.telegramcontrol;

import io.github.r4nx.telegramcontrol.commands.TelegramControlCommand;
import io.github.r4nx.telegramcontrol.telegram.Telegram;
import io.github.r4nx.telegramcontrol.telegram.TelegramCommandExecutor;
import org.bstats.bukkit.Metrics;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    public Telegram telegram;
    public long serverStart;
    private static final String PREFIX = ChatColor.AQUA + "[TelegramControl] ";

    public String getPrefix() {
        return PREFIX;
    }

    @Override
    public void onEnable() {
        serverStart = System.currentTimeMillis();

        FileConfiguration config = this.getConfig();
        config.addDefault("telegram.botToken", "");
        config.addDefault("telegram.chatId", 0);
        config.addDefault("telegram.updatesCheckInterval", 100L);
        config.addDefault("telegram.lastUpdate", 0);
        config.addDefault("telegram.lastMessage", 0);
        config.addDefault("execEnabled", true);
        config.addDefault("logTelegramCommands", false);
        config.options().copyDefaults(true);
        saveConfig();

        telegram = new Telegram(getConfig().getString("telegram.botToken"));
        telegram.setLastUpdate(config.getInt("telegram.lastUpdate"));
        telegram.setLastMessage(config.getInt("telegram.lastMessage"));
        TelegramCommandExecutor telegramCommandExecutor = new TelegramCommandExecutor(this);
        Metrics metrics = new Metrics(this);

        if (telegram.testConnection()) getLogger().info("Connection established!");
        else {
            getLogger().warning("Connection failed!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getServer().getScheduler().scheduleSyncRepeatingTask(this,
                () -> telegramCommandExecutor.execute(telegram.getUpdates()),
                0L, config.getLong("telegram.updatesCheckInterval"));

        getCommand("telegramcontrol").setExecutor(new TelegramControlCommand(this));

        telegram.sendMessage(getConfig().getInt("telegram.chatId"), "*[INFO]* Plugin enabled!", true);
    }

    @Override
    public void onDisable() {
        getConfig().set("telegram.lastUpdate", telegram.getLastUpdate());
        getConfig().set("telegram.lastMessage", telegram.getLastMessage());
        saveConfig();
        telegram.sendMessage(getConfig().getInt("telegram.chatId"), "*[INFO]* Plugin disabled!", true);
    }
}
