package me.enzol.kitspreview.kitpreview.listeners;

import me.enzol.kitspreview.KitsPreview;
import me.enzol.kitspreview.utils.Color;
import org.bukkit.configuration.Configuration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class InventoryListener implements Listener{

    private final KitsPreview plugin = KitsPreview.getInstance();
    private final Configuration config = plugin.getConfig();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        String title = Color.translate(config.getString("gui.title").replace("{kit}", ""));
        ItemStack item = event.getCurrentItem();

        InventoryView inventory = event.getView();
        if(inventory.getTitle().contains(title)){
            event.setCancelled(true);
        }else{
            if(item == null){
              item = inventory.getItem(event.getSlot());
            }
            if(item != null && item.getItemMeta() != null) {
                ItemMeta meta = item.getItemMeta();
                PersistentDataContainer container = meta.getPersistentDataContainer();
                if(container.has(plugin.getNamespacedKey(), PersistentDataType.STRING)){
                    event.setCurrentItem(null);
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event){
        ItemStack itemStack = event.getItemDrop().getItemStack();
        if(itemStack.getItemMeta() != null){
            ItemMeta meta = itemStack.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();
            if(container.has(plugin.getNamespacedKey(), PersistentDataType.STRING)){
                event.getItemDrop().remove();
            }
        }
    }

}
