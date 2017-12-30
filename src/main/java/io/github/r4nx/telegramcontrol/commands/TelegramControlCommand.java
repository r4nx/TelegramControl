package io.github.r4nx.telegramcontrol.commands;

import com.google.common.base.Joiner;
import io.github.r4nx.telegramcontrol.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;

public class TelegramControlCommand implements CommandExecutor {
    private final Main plugin;

    public TelegramControlCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.AQUA + String.format("TelegramControl v%s. Author: Ranx.\nType /help to see the list of commands.",
                    plugin.getDescription().getVersion()));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "help":
                if (!sender.isOp() && !sender.hasPermission("telegramcontrol.help")) {
                    sender.sendMessage(ChatColor.RED + "Access denied.");
                    return true;
                }
                sender.sendMessage(ChatColor.GREEN + String.join("", Collections.nCopies(7, "=")) +
                        ChatColor.AQUA + " TelegramControl " +
                        ChatColor.GREEN + String.join("", Collections.nCopies(7, "=")) + "\n" +
                        ChatColor.AQUA + "/telegramcontrol reload" + ChatColor.GREEN + " - reload configuration" + "\n" +
                        ChatColor.AQUA + "/telegramcontrol send <message>" + ChatColor.GREEN + " - send message to specified chat ID");
                break;
            case "reload":
                if (!sender.isOp() && !sender.hasPermission("telegramcontrol.reload")) {
                    sender.sendMessage(ChatColor.RED + "Access denied.");
                    return true;
                }
                plugin.reloadConfig();
                sender.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Configuration reloaded!");
                plugin.telegram.sendMessage(plugin.getConfig().getInt("telegram.chatId"), "*[INFO]* Configuration reloaded!", true);
                break;
            case "send":
                if (!sender.isOp() && !sender.hasPermission("telegramcontrol.send")) {
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
