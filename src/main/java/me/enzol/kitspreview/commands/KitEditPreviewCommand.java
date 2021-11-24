package me.enzol.kitspreview.commands;

import com.earth2me.essentials.Essentials;
import com.google.common.collect.Lists;
import me.enzol.kitspreview.KitsPreview;
import me.enzol.kitspreview.kitpreview.KitPreview;
import me.enzol.kitspreview.utils.Color;
import me.enzol.kitspreview.utils.EssentialsUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KitEditPreviewCommand implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players");
            return false;
        }
        Player player = (Player) sender;

        if(!player.hasPermission("kitpreview.edit")) return false;

        if(args.length < 2){
            player.sendMessage(Color.translate( "&bUse&7: "));
            player.sendMessage(Color.translate("&b/kiteditpreview &7createinventory&f (rows) (kit name)"));
            player.sendMessage(Color.translate("&b/kiteditpreview &7edit&f (kit name)"));
            player.sendMessage(Color.translate("&b/kiteditpreview &7setrows&f (rows) (kit name)"));
            return false;
        }

        if(player.isSleeping()) return false;
        String type = args[0];

        if(type.equalsIgnoreCase("create") || type.equalsIgnoreCase("createinventory")){
            try{
                int rows = Integer.parseInt(args[1]);

                if(rows > 6){
                    player.sendMessage(ChatColor.RED + "Max rows is 6.");
                    return false;
                }

                String kitName = args[2];

                if(KitPreview.getByName(kitName) != null){
                    player.sendMessage(ChatColor.RED + "That kit already exists.");
                    return false;
                }

                KitPreview kitPreview = new KitPreview(kitName, rows);
                Inventory inventory = Bukkit.createInventory(null, 9 * kitPreview.getRows(), "Editing " + kitName);
                EssentialsUtils.getItems(player, kitName).forEach(inventory::addItem);
                player.openInventory(inventory);
            }catch (NumberFormatException ignore){
                player.sendMessage(ChatColor.RED + "Only numbers.");
            }

        } else if (type.equalsIgnoreCase("edit")){
            String kitName = args[1];

            KitPreview kitPreview = KitPreview.getByName(kitName);

            if(kitPreview == null){
                player.sendMessage(ChatColor.RED + "Kit not found.");
                return false;
            }

            Inventory inventory = Bukkit.createInventory(null, 9 * kitPreview.getRows(), "Editing " + kitName);

            if(kitPreview.getItems().isEmpty()){
                EssentialsUtils.getItems(player, kitName).forEach(inventory::addItem);
            }else {
                kitPreview.getItems().forEach(kitItem -> inventory.setItem(kitItem.getSlot(), kitItem.getItem()));
            }

            player.openInventory(inventory);
        }else if (type.equalsIgnoreCase("setrows")){
            try{
                int rows = Integer.parseInt(args[1]);

                if(rows > 6){
                    player.sendMessage(ChatColor.RED + "Max rows is 6.");
                    return false;
                }

                String kitName = args[2];

                KitPreview kitPreview = KitPreview.getByName(kitName);

                if(kitPreview == null){
                    player.sendMessage(ChatColor.RED + "Kit not found.");
                    return false;
                }

                kitPreview.setRows(rows);
                kitPreview.save();
                player.sendMessage(ChatColor.GREEN + kitName + " rows successfully changed.");
            }catch (NumberFormatException ignore){
                player.sendMessage(ChatColor.RED + "Only numbers");
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args){
        Set<String> kits = KitsPreview.getInstance().getKits();
        if (args.length == 1) {
            return Stream.of("create", "createinventory", "edit", "setrows")
                .filter(s1 -> s1.toLowerCase().startsWith(args[0]))
                .collect(Collectors.toList());
        }
        if (args.length == 2) {
            return new ArrayList<>(kits).stream()
                .filter(s1 -> s1.toLowerCase().startsWith(args[1]))
                .collect(Collectors.toList());
        }
        if (args.length == 3) {
            return new ArrayList<>(kits).stream()
                .filter(s1 -> s1.toLowerCase().startsWith(args[2]))
                .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}