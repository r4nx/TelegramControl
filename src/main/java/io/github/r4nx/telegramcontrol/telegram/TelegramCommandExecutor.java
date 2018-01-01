package io.github.r4nx.telegramcontrol.telegram;

import com.google.common.base.Joiner;
import io.github.r4nx.telegramcontrol.Main;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
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
                    plugin.telegram.sendMessage(from, "*[DONE]* Pong!", true);
                    break;
                case "/exec":
                    if (!plugin.getConfig().getBoolean("execEnabled")) {
                        plugin.telegram.sendMessage(from, "*[ERROR]* Exec is disabled in the plugin configuration!", true);
                        break;
                    }
                    if (cmd.size() > 1)
                        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
                                Joiner.on(" ").join(Arrays.copyOfRange(cmd.toArray(), 1, cmd.size())));
                    else plugin.telegram.sendMessage(from, "*[ERROR]* At least 1 argument is required!", true);
                    break;
                case "/msg":
                    if (cmd.size() > 2) {
                        if (plugin.getServer().getPlayer(cmd.get(1)) != null)
                            plugin.getServer().getPlayer(cmd.get(1)).sendMessage(plugin.getPrefix() +
                                    ChatColor.GREEN + "Message: " + ChatColor.YELLOW +
                                    Joiner.on(" ").join(Arrays.copyOfRange(cmd.toArray(), 2, cmd.size())));
                        else
                            plugin.telegram.sendMessage(from, String.format("*[ERROR]* %s is offline!", cmd.get(1).replace("_", "\\_")), true);
                    }
                    else plugin.telegram.sendMessage(from, "*[ERROR]* At least 2 arguments are required!", true);
                    break;
                case "/uptime":
                    long now = System.currentTimeMillis();
                    long diff = now - plugin.serverStart;
                    String uptimeMsg = (int) (diff / 86400000L) + " days " + (int) (diff / 3600000L % 24L) + " hours " +
                            (int) (diff / 60000L % 60L) + " minutes " + (int) (diff / 1000L % 60L) + " seconds.";
                    plugin.telegram.sendMessage(from, "*[DONE]* Uptime: " + uptimeMsg, true);
                    break;
                case "/players":
                    ArrayList<String> players = new ArrayList<>();
                    plugin.getServer().getOnlinePlayers().forEach(player -> players.add(player.getName().replace("_", "\\_")));
                    plugin.telegram.sendMessage(from, String.format("*[DONE]* Online players [%d/%d]: ", players.size(), plugin.getServer().getMaxPlayers()) + Joiner.on(", ").join(players), true);
                    break;
                default:
                    plugin.telegram.sendMessage(from, "*[ERROR]* Unknown command!", true);
            }

            if (plugin.getConfig().getBoolean("logTelegramCommands"))
                plugin.getLogger().info(String.format("Command from %s: %s", from, update.get("text")));
        }
    }
}
