package com.sainttx.banknotes.command;

import com.sainttx.banknotes.BanknotePlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Matthew on 9/7/2015.
 */
public class BanknotesCommand implements CommandExecutor {

    /*
     * The plugin instance
     */
    private BanknotePlugin plugin;

    /**
     * Creates the "/deposit" command handler
     */
    public BanknotesCommand(BanknotePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return false;
        } else if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("banknotes.reload")) {
                sender.sendMessage(plugin.colorMessage(plugin.getConfig().getString("messages.insufficient-permissions")));
            } else {
                plugin.reloadConfig();
                plugin.reload();
                sender.sendMessage(plugin.colorMessage(plugin.getConfig().getString("messages.reloaded")));
            }
            return true;
        } else if (args[0].equalsIgnoreCase("give") && args.length >= 3) {
            if (!sender.hasPermission("banknotes.give")) {
                sender.sendMessage(plugin.colorMessage(plugin.getConfig().getString("messages.insufficient-permissions")));
            } else {
                // give player amount
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(plugin.colorMessage(plugin.getConfig().getString("messages.target-not-found")));
                    return true;
                }

                double amount;
                try {
                    amount = Double.parseDouble(args[2]);
                } catch (NumberFormatException ex) {
                    sender.sendMessage(plugin.colorMessage(plugin.getConfig().getString("messages.invalid-number")));
                    return true;
                }

                if (Double.isNaN(amount) || Double.isInfinite(amount) || amount <= 0) {
                    sender.sendMessage(plugin.colorMessage(plugin.getConfig().getString("messages.invalid-number")));
                } else {
                    ItemStack banknote = plugin.createBanknote(sender.getName(), amount);
                    target.getInventory().addItem(banknote);

                    //Use console-name if the note is given by a console command
                    String senderName = sender instanceof ConsoleCommandSender ? plugin.getConfig().getString("settings.console-name") : sender.getName();
                    target.sendMessage(plugin.colorMessage(plugin.getConfig().getString("messages.note-received")
                            .replace("[money]", plugin.formatDouble(amount))
                            .replace("[player]", senderName)));
                    sender.sendMessage(plugin.colorMessage(plugin.getConfig().getString("messages.note-given")
                            .replace("[money]", plugin.formatDouble(amount))
                            .replace("[player]", target.getName())));


        /*

                    //Old code, just in case.

                    if (sender instanceof ConsoleCommandSender) {
                        String senderName = plugin.getConfig().getString("settings.console-name");
                        target.getInventory().addItem(banknote);

                        target.sendMessage(plugin.colorMessage(plugin.getConfig().getString("messages.note-received")
                                .replace("[money]", plugin.formatDouble(amount))
                                .replace("[player]", senderName)));
                        sender.sendMessage(plugin.colorMessage(plugin.getConfig().getString("messages.note-given")
                                .replace("[money]", plugin.formatDouble(amount))
                                .replace("[player]", target.getName())));
                    }else if (sender instanceof Player) {

                        target.getInventory().addItem(banknote);
                        target.sendMessage(plugin.colorMessage(plugin.getConfig().getString("messages.note-received")
                                .replace("[money]", plugin.formatDouble(amount))
                                .replace("[player]", sender.getName())));
                        sender.sendMessage(plugin.colorMessage(plugin.getConfig().getString("messages.note-given")
                                .replace("[money]", plugin.formatDouble(amount))
                                .replace("[player]", target.getName())));
                    }

        */

                }
            }
            return true;
        }

        return false;
    }
}
