package me.sainttx.banknotes.command;

import me.sainttx.banknotes.BanknotePlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

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
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        return false;
    }
}