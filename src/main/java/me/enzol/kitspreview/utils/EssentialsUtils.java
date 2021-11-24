package me.enzol.kitspreview.utils;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.Kit;
import com.earth2me.essentials.MetaItemStack;
import com.earth2me.essentials.User;
import com.earth2me.essentials.textreader.IText;
import com.earth2me.essentials.textreader.KeywordReplacer;
import com.earth2me.essentials.textreader.SimpleTextInput;
import com.google.common.collect.Lists;
import me.enzol.kitspreview.KitsPreview;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;

import static com.earth2me.essentials.I18n.tl;

public class EssentialsUtils {

    private static Configuration config =  KitsPreview.getInstance().getConfig();

    public static List<ItemStack> getItems(Player player, String kitName){
        Essentials ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
        User user = ess.getUser(player);
        Kit kit;
        List<ItemStack> item = Lists.newArrayList();
        try{
            kit = new Kit(kitName, ess);
        }catch(Exception e){
            player.sendMessage(ChatColor.RED + "Kit not found");
            return item;
        }

        try{
            IText input = new SimpleTextInput(kit.getItems());
            IText output = new KeywordReplacer(input, user.getSource(), ess, true, true);

            final boolean allowUnsafe = ess.getSettings().allowUnsafeEnchantments();
            for(String kitItem : output.getLines()){
                if(kitItem.startsWith(ess.getSettings().getCurrencySymbol())){
                    BigDecimal value = new BigDecimal(kitItem.substring(ess.getSettings().getCurrencySymbol().length()).trim());
                    Material balance = Material.matchMaterial(config.getString("gui.items.balance", "GOLD_NUGGET"));
                    ItemStack money = new ItemStack(balance);
                    ItemMeta moneyMeta = money.getItemMeta();
                    moneyMeta.setDisplayName(ChatColor.GOLD + ess.getSettings().getCurrencySymbol() + value);
                    money.setItemMeta(moneyMeta);
                    item.add(money);
                    continue;
                }

                if(kitItem.startsWith("/")){
                    String command = kitItem;
                    String name = user.getName();
                    command = command.replace("{player}", name);
                    Material commands = Material.matchMaterial(config.getString("gui.items.commands", "COMMAND_BLOCK"));
                    ItemStack commandItem = new ItemStack(commands);
                    ItemMeta commandMeta = commandItem.getItemMeta();
                    commandMeta.setDisplayName(ChatColor.GREEN + "Command: " + ChatColor.WHITE + command);
                    commandItem.setItemMeta(commandMeta);
                    item.add(commandItem);
                    continue;
                }

                final String[] parts = kitItem.split(" +");
                final ItemStack parseStack = ess.getItemDb().get(parts[0], parts.length > 1 ? Integer.parseInt(parts[1]) : 1);

                if(parseStack.getType() == Material.AIR) continue;

                final MetaItemStack metaStack = new MetaItemStack(parseStack);

                if(parts.length > 2) metaStack.parseStringMeta(null, allowUnsafe, parts, 2, ess);

                item.add(metaStack.getItemStack());
            }
        }catch (Exception e) {
            user.getBase().updateInventory();
            ess.getLogger().log(Level.WARNING, e.getMessage());
            try{
                throw new Exception(tl("kitError2"), e);
            }catch(Exception e1){
                e1.printStackTrace();
            }
        }
        return item;
    }

    public static boolean isNewVersion(){
        String versionString = Bukkit.getPluginManager().getPlugin("Essentials").getDescription().getVersion();
        int version = Integer.parseInt(getThirdString(versionString));
        System.out.println(version);
        return version > 18;
    }

    //Get thrid string from a string
    private static String getThirdString(String string){
        String[] strings = string.split("\\.");
        return strings[1];
    }
}