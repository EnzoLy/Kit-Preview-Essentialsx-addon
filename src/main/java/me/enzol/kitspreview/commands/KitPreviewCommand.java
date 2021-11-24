package me.enzol.kitspreview.commands;

import me.enzol.kitspreview.KitsPreview;
import me.enzol.kitspreview.kitpreview.KitPreview;
import me.enzol.kitspreview.kitpreview.listeners.InventoryListener;
import me.enzol.kitspreview.utils.Color;
import me.enzol.kitspreview.utils.EssentialsUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class KitPreviewCommand implements CommandExecutor, TabExecutor{

    private static KitsPreview plugin = KitsPreview.getInstance();
    private static Configuration config = plugin.getConfig();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args){

        if(!(sender instanceof Player)){
            sender.sendMessage(ChatColor.RED + "Only players");
            return false;
        }

        Player player = (Player) sender;

        if(args.length < 1){
            player.sendMessage(Color.translate( "&bUse&7: "));
            player.sendMessage(Color.translate("&b/kitpreview (kit name)"));
            return false;
        }

        if(player.isSleeping()) return false;

        String kitName = args[0];

        KitPreview kitPreview = KitPreview.getByName(kitName);

        if(kitPreview == null){
            player.sendMessage(ChatColor.RED + "Kit not found");
            return false;
        }

        Inventory inventory = Bukkit.createInventory(null,
            9 * kitPreview.getRows(),
            Color.translate(config.getString("gui.title").replace("{kit}", kitName)));

        if(kitPreview.getItems().isEmpty()){
            EssentialsUtils.getItems(player, kitName).forEach(inventory::addItem);
        }else {
            kitPreview.getItems().forEach(kitItem -> inventory.setItem(kitItem.getSlot(), kitItem.getItem()));
        }

        player.openInventory(inventory);

        InventoryListener.inventorysOpen.add(player.getUniqueId());

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args){
        Set<String> kits = KitsPreview.getInstance().getKits();
        if (args.length == 1) {
            return new ArrayList<>(kits).stream()
                .filter(s1 -> s1.toLowerCase().startsWith(args[0]))
                .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
