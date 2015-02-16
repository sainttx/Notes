package me.sainttx.banknotes.command;

import me.sainttx.banknotes.BanknotePlugin;
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
    private BanknotePlugin plugin;

    /**
     * Creates the "/withdraw <amount>" command handler
     */
    public WithdrawCommand(BanknotePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can withdraw bank notes");
        } else if (!sender.hasPermission("banknotes.withdraw")) {
            sender.sendMessage(plugin.colorMessage(plugin.getConfig().getString("messages.insufficient-permissions")));
        } else if (args.length == 0) {
            return false;
        } else {
            Player player = (Player) sender;

            try {
                double amount = Double.parseDouble(args[0]);
                double min = plugin.getConfig().getDouble("settings.minimum-withdraw-amount");
                double max = plugin.getConfig().getDouble("settings.maximum-withdraw-amount");

                if (Double.isNaN(amount) || Double.isInfinite(amount) || amount <= 0) {
                    player.sendMessage(plugin.colorMessage(plugin.getConfig().getString("messages.invalid-number")));
                } else if (amount < min) {
                    player.sendMessage(plugin.colorMessage(plugin.getConfig().getString("messages.less-than-minimum").replace("[money]", plugin.formatDouble(min))));
                } else if (amount > max) {
                    player.sendMessage(plugin.colorMessage(plugin.getConfig().getString("messages.more-than-maximum").replace("[money]", plugin.formatDouble(max))));
                } else if (plugin.getEconomy().getBalance(player) < amount) {
                    player.sendMessage(plugin.colorMessage(plugin.getConfig().getString("messages.insufficient-funds")));
                } else if (player.getInventory().firstEmpty() == -1) {
                    player.sendMessage(plugin.colorMessage(plugin.getConfig().getString("messages.inventory-full")));
                } else {
                    ItemStack banknote = plugin.createBanknote(player, amount);
                    plugin.getEconomy().withdrawPlayer(player, amount);

                    player.getInventory().addItem(banknote);
                    player.sendMessage(plugin.colorMessage(plugin.getConfig().getString("messages.note-created").replace("[money]", plugin.formatDouble(amount))));
                }
            } catch (NumberFormatException invalidNumber) {
                player.sendMessage(plugin.colorMessage(plugin.getConfig().getString("messages.invalid-number")));
            }
        }
        return true;
    }
}
