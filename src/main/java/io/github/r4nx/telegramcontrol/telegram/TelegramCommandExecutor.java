package io.github.r4nx.telegramcontrol.telegram;

import com.google.common.base.Joiner;
import io.github.r4nx.telegramcontrol.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TelegramCommandExecutor {
    private final Main plugin;

    public TelegramCommandExecutor(Main plugin) {
        this.plugin = plugin;
    }

    public void execute(List<HashMap> updates) {
        if (updates.size() == 0) return;

        int chatId = plugin.getConfig().getInt("telegram.chatId");

        for (HashMap update : updates) {
            List<String> cmd = new ArrayList<>();

            Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(update.get("text").toString());
            while (m.find())
                cmd.add(m.group(1).replace("\"", ""));

            int from = (int) update.get("from");

            if (from == chatId && cmd.size() > 0 && cmd.get(0).startsWith("/")) switch (cmd.get(0).toLowerCase()) {
                case "/ping":
                    plugin.telegram.sendMessage(from, "Pong!", false);
                    break;
                case "/exec":
                    if (cmd.size() > 1)
                        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
                                update.get("text").toString().substring("/exec ".length()));
                    else plugin.telegram.sendMessage(from, "At least 1 argument is required!", false);
                    break;
                case "/uptime":
                    long now = System.currentTimeMillis();
                    long diff = now - plugin.serverStart;
                    String uptimeMsg = (int) (diff / 86400000L) + " days " + (int) (diff / 3600000L % 24L) + " hours " +
                            (int) (diff / 60000L % 60L) + " minutes " + (int) (diff / 1000L % 60L) + " seconds.";
                    plugin.telegram.sendMessage(from, "Uptime: " + uptimeMsg, false);
                    break;
                case "/players":
                    ArrayList<String> players = new ArrayList<>();
                    plugin.getServer().getOnlinePlayers().forEach(player -> players.add(player.getName()));
                    plugin.telegram.sendMessage(from, "Online players: " + Joiner.on(", ").join(players), false);
                    break;
                default:
                    plugin.telegram.sendMessage(from, "Unknown command!", false);
            }

            if (plugin.getConfig().getBoolean("logTelegramCommands"))
                plugin.getLogger().info(String.format("Command from %s: %s", from, update.get("text")));
        }
    }
}
