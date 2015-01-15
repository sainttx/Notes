package me.sainttx.banknotes;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.NumberFormat;
import java.util.List;

/**
 * Created by Matthew on 14/01/2015.
 */
public class BanknotePlugin extends JavaPlugin {

    /*
     * The base item
     */
    private ItemStack base;

    /*
     * The base lore for the item
     */
    private List<String> baseLore;

    /*
     * Vault economy implementation
     */
    private Economy economy;

    @Override
    public void onEnable() {
        // Save configuration and register listeners
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new BanknoteListener(this), this);
        economy = getServer().getServicesManager().getRegistration(Economy.class).getProvider();

        // Reload
        reload();
    }

    /**
     * Returns the Economy implementation
     */
    public Economy getEconomy() {
        return economy;
    }

    /**
     * Returns a formatted String representation of a double, rounded to 2
     * decimal places
     *
     * @param value The double to be formatted
     *
     * @return A formatted string of the double
     */
    public String formatDouble(double value) {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        return nf.format(value);
    }

    /**
     * Returns a colored message
     *
     * @param message The original message
     *
     * @return The message formatted with char '&' replaced
     *         by ChatColor.COLOR_CHAR
     */
    public String colorMessage(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Reloads the configuration, the item, and it's lore
     */
    public void reload() {
        reloadConfig();

        // Load the base item
        base = new ItemStack(Material.getMaterial(getConfig().getString("note.material", "PAPER")), 1, (short) getConfig().getInt("note.data"));
        ItemMeta meta = base.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', getConfig().getString("note.name", "Banknote")));
        base.setItemMeta(meta);

        // Load the base lore
        baseLore = getConfig().getStringList("note.lore");
    }

    /**
     * Creates a Banknote
     *
     * @param creating  The player creating the note
     * @param amount    The amount of money on the note
     *
     * @return The banknote as an item
     */
    public ItemStack createBanknote(Player creating, double amount) {
        return null;
    }

    /**
     * Returns whether an ItemStack is a banknote
     *
     * @param itemstack The item that may or may not be a note
     *
     * @return True if the item represents a note, false otherwise
     */
    public boolean isBanknote(ItemStack itemstack) {
        return false;
    }

    /**
     * Returns the amount of money that the banknote holds
     *
     * @param itemstack The banknote
     *
     * @return The amount of money that the note holds, 0 if the
     *         item isn't a note
     */
    public double getBanknoteAmount(ItemStack itemstack) {
        return 0;
    }
}
