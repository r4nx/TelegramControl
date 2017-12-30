package io.github.r4nx.telegramcontrol.commands;

import com.google.common.base.Joiner;
import io.github.r4nx.telegramcontrol.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class TelegramControlCommand implements CommandExecutor {
    private final Main plugin;

    public TelegramControlCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            String[] messages = {
                    ChatColor.AQUA + String.format("TelegramControl v%s. Author: %s.", plugin.getDescription().getVersion(), plugin.getDescription().getAuthors().get(0)),
                    ChatColor.AQUA + "Type /help to see the list of commands."
            };
            Arrays.asList(messages).forEach(sender::sendMessage);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "help":
                if (!sender.hasPermission("telegramcontrol.help")) {
                    sender.sendMessage(ChatColor.RED + "Access denied.");
                    return true;
                }
                String[] messages = {
                        ChatColor.GREEN + "======= " + ChatColor.AQUA + "TelegramControl" + ChatColor.GREEN + " =======",
                        ChatColor.AQUA + "/telegramcontrol reload" + ChatColor.GREEN + " - reload configuration",
                        ChatColor.AQUA + "/telegramcontrol send <message>" + ChatColor.GREEN + " - send message to specified chat ID"
                };
                Arrays.asList(messages).forEach(sender::sendMessage);
                break;
            case "reload":
                if (!sender.hasPermission("telegramcontrol.reload")) {
                    sender.sendMessage(ChatColor.RED + "Access denied.");
                    return true;
                }
                plugin.reloadConfig();
                sender.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Configuration reloaded!");
                plugin.telegram.sendMessage(plugin.getConfig().getInt("telegram.chatId"), "*[INFO]* Configuration reloaded!", true);
                break;
            case "send":
                if (!sender.hasPermission("telegramcontrol.send")) {
                    sender.sendMessage(ChatColor.RED + "Access denied.");
                    return true;
                }
                plugin.telegram.sendMessage(plugin.getConfig().getInt("telegram.chatId"),
                        String.format("*[MESSAGE]* From %s: %s", sender.getName().replace("_", "\\_"),
                                Joiner.on(" ").join(Arrays.copyOfRange(args, 1, args.length))), true);
                sender.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Message sent!");
                break;
            default:
                sender.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Unknown subcommand.");
        }
        return true;
    }
}
