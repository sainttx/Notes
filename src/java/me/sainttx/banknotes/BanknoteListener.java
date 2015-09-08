package me.sainttx.banknotes;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Matthew on 14/01/2015.
 */
public class BanknoteListener implements Listener {

    /*
     * The plugin instance
     */
    private BanknotePlugin plugin;

    /**
     * Creates the note listener
     */
    public BanknoteListener(BanknotePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerClaimNote(PlayerInteractEvent event) {
        // Check if we need to use /deposit or if we can right click
        if (!plugin.getConfig().getBoolean("settings.allow-right-click-to-deposit-notes", true)) {
            return;
        }

        // Check the action
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        // Check if the player is allowed to deposit bank notes
        if (!event.getPlayer().hasPermission("banknotes.deposit")) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = event.getPlayer().getItemInHand();

        // Verify that this is a real banknote
        if (item == null || !plugin.isBanknote(item)) {
            return;
        }

        double amount = plugin.getBanknoteAmount(item);

        // Negative banknotes are not allowed
        if (Double.compare(amount, 0) < 0) {
            return;
        }

        // Double check the response
        EconomyResponse response = plugin.getEconomy().depositPlayer(player, amount);
        if (response == null || !response.transactionSuccess()) {
            player.sendMessage(ChatColor.RED + "There was an error processing your transaction");
            plugin.getLogger().warning("Error processing player right click deposit " +
                    "(" + player.getName() + " for $" + plugin.formatDouble(amount) + ") " +
                    "[message: " + (response == null ? "null" : response.errorMessage) + "]");
            return;
        }

        // Deposit the money
        String message = plugin.getConfig().getString("messages.note-redeemed");
        player.sendMessage(plugin.colorMessage(message.replace("[money]", plugin.formatDouble(amount))));

        // Remove the slip
        if (item.getAmount() <= 1) {
            event.getPlayer().getInventory().removeItem(item);
        } else {
            item.setAmount(item.getAmount() - 1);
        }
    }
}
