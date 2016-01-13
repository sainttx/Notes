package com.sainttx.notes.command;

import com.sainttx.notes.NotesPlugin;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Matthew on 14/01/2015.
 */
public class WithdrawCommand implements CommandExecutor {

    /*
    * The plugin instance
    */
    private NotesPlugin plugin;

    /**
     * Creates the "/withdraw <amount>" command handler
     */
    public WithdrawCommand(NotesPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can withdraw bank notes");
        } else if (!sender.hasPermission("banknotes.withdraw")) {
            sender.sendMessage(plugin.getMessage("messages.insufficient-permissions"));
        } else if (args.length == 0) {
            return false;
        } else {
            Player player = (Player) sender;
            double amount;

            try {
                amount = args[0].equalsIgnoreCase("all")
                        ? plugin.getEconomy().getBalance(player) : Double.parseDouble(args[0]);
            } catch (NumberFormatException invalidNumber) {
                player.sendMessage(plugin.getMessage("messages.invalid-number"));
                return true;
            }

            double min = plugin.getConfig().getDouble("settings.minimum-withdraw-amount");
            double max = plugin.getConfig().getDouble("settings.maximum-withdraw-amount");

            if (Double.isNaN(amount) || Double.isInfinite(amount) || amount <= 0) {
                player.sendMessage(plugin.getMessage("messages.invalid-number"));
            } else if (Double.compare(amount, min) < 0) {
                player.sendMessage(plugin.getMessage("messages.less-than-minimum")
                        .replace("[money]", plugin.formatDouble(min)));
            } else if (Double.compare(amount, max) > 0) {
                player.sendMessage(plugin.getMessage("messages.more-than-maximum")
                        .replace("[money]", plugin.formatDouble(max)));
            } else if (Double.compare(plugin.getEconomy().getBalance(player), amount) < 0) {
                player.sendMessage(plugin.getMessage("messages.insufficient-funds"));
            } else if (player.getInventory().firstEmpty() == -1) {
                player.sendMessage(plugin.getMessage("messages.inventory-full"));
            } else {
                ItemStack banknote = plugin.createBanknote(player.getName(), amount);
                EconomyResponse response = plugin.getEconomy().withdrawPlayer(player, amount);

                if (response == null || !response.transactionSuccess()) {
                    player.sendMessage(ChatColor.RED + "There was an error processing your transaction");
                    plugin.getLogger().warning("Error processing player withdrawal " +
                            "(" + player.getName() + " for $" + plugin.formatDouble(amount) + ") " +
                            "[message: " + (response == null ? "null" : response.errorMessage) + "]");
                    return true;
                }

                player.getInventory().addItem(banknote);
                player.sendMessage(plugin.getMessage("messages.note-created").replace("[money]", plugin.formatDouble(amount)));
            }
        }

        return true;
    }
}
