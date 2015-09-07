package me.sainttx.banknotes;

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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerClaimNote(PlayerInteractEvent event) {
        if (!plugin.getConfig().getBoolean("settings.allow-right-click-to-deposit-notes", true)) {
            return;
        }

        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                && event.getPlayer().hasPermission("banknotes.deposit")) {
            ItemStack item = event.getPlayer().getItemInHand();

            if (item != null && plugin.isBanknote(item)) {
                double amount = plugin.getBanknoteAmount(item);

                if (amount > 0) {
                    plugin.getEconomy().depositPlayer(event.getPlayer(), amount);
                    String message = plugin.getConfig().getString("messages.note-redeemed");
                    event.getPlayer().sendMessage(plugin.colorMessage(message.replace("[money]", plugin.formatDouble(amount))));

                    // Remove the slip
                    if (item.getAmount() <= 1) {
                        event.getPlayer().getInventory().removeItem(item);
                    } else {
                        item.setAmount(item.getAmount() - 1);
                    }
                }
            }
        }
    }
}
