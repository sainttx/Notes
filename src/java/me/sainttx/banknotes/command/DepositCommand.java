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
public class DepositCommand implements CommandExecutor {

    /*
     * The plugin instance
     */
    private BanknotePlugin plugin;

    /**
     * Creates the "/deposit" command handler
     */
    public DepositCommand(BanknotePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can deposit bank notes");
        } else if (!sender.hasPermission("banknotes.deposit")) {
            sender.sendMessage(plugin.colorMessage(plugin.getConfig().getString("messages.insufficient-permissions")));
        } else {
            Player player = (Player) sender;
            ItemStack item = player.getItemInHand();

            if (item != null && plugin.isBanknote(item)) {
                double amount = plugin.getBanknoteAmount(item);

                if (amount > 0) {
                    plugin.getEconomy().depositPlayer(player, amount);
                    player.sendMessage(plugin.colorMessage(plugin.getConfig().getString("messages.note-redeemed").replace("[money]", plugin.formatDouble(amount))));
                } else {
                    player.sendMessage(plugin.colorMessage(plugin.getConfig().getString("messages.invalid-note")));
                }

                // Remove the slip
                if (item.getAmount() <= 1) {
                    player.getInventory().removeItem(item);
                } else {
                    item.setAmount(item.getAmount() - 1);
                }
            } else {
                player.sendMessage(plugin.colorMessage(plugin.getConfig().getString("messages.nothing-in-hand")));
            }
        }
        return true;
    }
}
