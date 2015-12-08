package com.sainttx.banknotes;

import com.sainttx.banknotes.command.BanknotesCommand;
import com.sainttx.banknotes.command.DepositCommand;
import com.sainttx.banknotes.command.WithdrawCommand;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /*
     * REGEX to find money
     */
    private final Pattern MONEY_PATTERN = Pattern.compile("((([1-9]\\d{0,2}(,\\d{3})*)|(([1-9]\\d*)?\\d))(\\.?\\d?\\d?)?$)");

    @Override
    public void onEnable() {
        // Save configuration and register listeners
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new BanknoteListener(this), this);

        // Register commands
        getCommand("withdraw").setExecutor(new WithdrawCommand(this));
        getCommand("deposit").setExecutor(new DepositCommand(this));
        getCommand("banknotes").setExecutor(new BanknotesCommand(this));

        // Load economy a tick later
        getServer().getScheduler().runTask(this, new Runnable() {
            @Override
            public void run() {
                RegisteredServiceProvider<Economy> provider = getServer().getServicesManager().getRegistration(Economy.class);

                if (provider == null) {
                    getLogger().info("Failed to find a valid economy provider, disabling...");
                    getServer().getPluginManager().disablePlugin(BanknotePlugin.this);
                } else {
                    economy = provider.getProvider();
                    getLogger().info("Found economy provider " + provider.getPlugin().getName());
                }
            }
        });

        // Load base itemstack and lore
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
     * @return A formatted string of the double
     */
    public String formatDouble(double value) {
        NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);

        int max = getConfig().getInt("settings.maximum-float-amount");
        int min = getConfig().getInt("settings.minimum-float-amount");

        nf.setMaximumFractionDigits(max);
        nf.setMinimumFractionDigits(min);
        return nf.format(value);
    }

    /**
     * Returns a colored message
     *
     * @param message The original message
     * @return The message formatted with char '&' replaced
     * by ChatColor.COLOR_CHAR
     */
    public String colorMessage(String message) {
        if (message == null) {
            return message;
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Returns a message from console formatted with {@link ChatColor#translateAlternateColorCodes(char, String)}
     *
     * @param path the path to the message
     * @return the message
     */
    public String getMessage(String path) {
        if (!getConfig().isString(path)) {
            return path;
        }

        return ChatColor.translateAlternateColorCodes('&', getConfig().getString(path));
    }

    /**
     * Reloads the configuration, the item, and it's lore
     */
    public void reload() {
        reloadConfig();

        // Load the base item
        base = new ItemStack(Material.getMaterial(getConfig().getString("note.material", "PAPER")), 1, (short) getConfig().getInt("note.data"));
        ItemMeta meta = base.getItemMeta();
        meta.setDisplayName(colorMessage(getConfig().getString("note.name", "Banknote")));
        base.setItemMeta(meta);

        // Load the base lore
        baseLore = getConfig().getStringList("note.lore");
    }

    /**
     * Creates a Banknote
     *
     * @param creatorName The name of who is creating the note
     * @param amount      The amount of money on the note
     * @return The banknote as an item
     */
    public ItemStack createBanknote(String creatorName, double amount) {
        List<String> formatLore = new ArrayList<String>();

        // Format the base lore
        for (String baseLore : this.baseLore) {
            if (creatorName.equals("CONSOLE")) {
                creatorName = getConfig().getString("settings.console-name");
            }
            formatLore.add(colorMessage(baseLore.replace("[money]", formatDouble(amount)).replace("[player]", creatorName)));
        }

        // Add the base lore to the item
        ItemStack ret = base.clone();
        ItemMeta meta = ret.getItemMeta();
        meta.setLore(formatLore);
        ret.setItemMeta(meta);

        return ret;
    }

    /**
     * Returns whether an ItemStack is a banknote
     *
     * @param itemstack The item that may or may not be a note
     * @return True if the item represents a note, false otherwise
     */
    public boolean isBanknote(ItemStack itemstack) {
        if (itemstack.getType() == base.getType() && itemstack.getDurability() == base.getDurability()
                && itemstack.getItemMeta().hasDisplayName() && itemstack.getItemMeta().hasLore()) {
            String display = itemstack.getItemMeta().getDisplayName();
            List<String> lore = itemstack.getItemMeta().getLore();

            // The size thing for the lore is a bit ghetto
            return display.equals(this.getMessage("note.name")) && lore.size() == getConfig().getStringList("note.lore").size();
        }
        return false;
    }

    /**
     * Returns the amount of money that the banknote holds
     *
     * @param itemstack The banknote
     * @return The amount of money that the note holds, 0 if the
     * item isn't a note
     */
    public double getBanknoteAmount(ItemStack itemstack) {
        if (itemstack.getItemMeta().hasDisplayName() && itemstack.getItemMeta().hasLore()) {
            String display = itemstack.getItemMeta().getDisplayName();
            List<String> lore = itemstack.getItemMeta().getLore();

            if (display.equals(this.getMessage("note.name"))) {
                for (String money : lore) {
                    Matcher matcher = MONEY_PATTERN.matcher(money);

                    if (matcher.find()) {
                        String amount = matcher.group(1);
                        return Double.parseDouble(amount.replaceAll(",", ""));


                    }
                }
            }
        }
        return 0;
    }
}
