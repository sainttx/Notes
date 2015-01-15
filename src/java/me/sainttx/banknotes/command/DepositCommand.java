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
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can deposit bank notes");
        } else {
            Player player = (Player) sender;
            ItemStack item = player.getItemInHand();

            if (item != null && plugin.isBanknote(item)) {
                double amount = plugin.getBanknoteAmount(item);

                if (amount > 0) {
                    plugin.getEconomy().depositPlayer(player, amount);
                    player.sendMessage(plugin.colorMessage(plugin.getConfig().getString("note-redeemed").replace("[money]", plugin.formatDouble(amount))));
                } else {
                    player.sendMessage(plugin.colorMessage(plugin.getConfig().getString("invalid-note")));
                }

                // Remove the item
                player.getInventory().removeItem(item);
            } else {
                player.sendMessage(plugin.colorMessage(plugin.getConfig().getString("nothing-in-hand")));
            }
        }
        return false;
    }
}
