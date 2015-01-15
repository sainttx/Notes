package me.sainttx.banknotes;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

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

    @Override
    public void onEnable() {
        // Save configuration and register listeners
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new BanknoteListener(this), this);

        // Reload
        reload();
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
