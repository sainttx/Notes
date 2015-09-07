package me.sainttx.banknotes.command;

import me.sainttx.banknotes.BanknotePlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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
        if (!sender.hasPermission("banknotes.reload")) {
            sender.sendMessage(plugin.colorMessage(plugin.getConfig().getString("messages.insufficient-permissions")));
        } else if (args.length == 0 || !args[0].equalsIgnoreCase("reload")) {
            return false;
        } else {
            plugin.reloadConfig();
            plugin.reload();
            sender.sendMessage(plugin.colorMessage(plugin.getConfig().getString("messages.reloaded")));
        }

        return true;
    }
}
