package me.enzol.kitspreview.sign;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.Kit;
import me.enzol.kitspreview.KitsPreview;
import me.enzol.kitspreview.utils.Color;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignListener implements Listener{

    private final KitsPreview plugin = KitsPreview.getInstance();
    private final Configuration config = plugin.getConfig();
    private final Essentials ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");

    @EventHandler
    public void onSignChange(SignChangeEvent event){
		if(!event.getPlayer().hasPermission("kitspreview.sign")) return;

        if(event.getLine(0) == null) return;

        if(event.getLine(0).equalsIgnoreCase("[kitpreview]")){
            String kitName = event.getLine(1);

            Kit kit;
            try{
                kit = new Kit(kitName, ess);
            }catch(Exception e){
                event.setLine(1, ChatColor.RED + "Kit not found");
                return;
            }

            event.setLine(0, Color.translate(config.getString("sign.lines.1").replace("{kitname}", kit.getName())));
            event.setLine(1, Color.translate(config.getString("sign.lines.2").replace("{kitname}", kit.getName())));
            event.setLine(2, Color.translate(config.getString("sign.lines.3").replace("{kitname}", kit.getName())));
            event.setLine(3, Color.translate(config.getString("sign.lines.4").replace("{kitname}", kit.getName())));
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        if(event.getClickedBlock() != null && event.getClickedBlock().getType() != Material.AIR){
            Player player = event.getPlayer();
            Block block = event.getClickedBlock();
            if(block.getType().name().contains("SIGN") && event.getAction().name().startsWith("RIGHT_")) {
                Sign sign = (Sign) block.getState();
                if(sign.getLine(0) == null) return;

                if(sign.getLine(0).equalsIgnoreCase(Color.translate(config.getString("sign.lines.1")))){
					if(config.getString("sign.lines.1").contains("{kitname}")){
						String kitName = sign.getLine(0);
						player.performCommand("kitpreview " + ChatColor.stripColor(kitName));
					}else if(config.getString("sign.lines.2").contains("{kitname}")){
						String kitName = sign.getLine(1);
						player.performCommand("kitpreview " + ChatColor.stripColor(kitName));
					}else if(config.getString("sign.lines.3").contains("{kitname}")){
						String kitName = sign.getLine(2);
						player.performCommand("kitpreview " + ChatColor.stripColor(kitName));
					}else if(config.getString("sign.lines.4").contains("{kitname}")){
						String kitName = sign.getLine(3);
						player.performCommand("kitpreview " + ChatColor.stripColor(kitName));
					}
                }
            }
        }
    }

}
