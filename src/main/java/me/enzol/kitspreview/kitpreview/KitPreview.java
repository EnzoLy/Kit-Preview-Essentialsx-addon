package me.enzol.kitspreview.kitpreview;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.enzol.kitspreview.KitsPreview;
import me.enzol.kitspreview.kitpreview.item.KitItem;

import java.io.*;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class KitPreview {

    @Getter private static final Map<String, KitPreview> kits = Maps.newHashMap();

    private String kitName;
    @Setter private int rows;
    private final List<KitItem> items = Lists.newArrayList();

    public void save(){
        Gson gson = KitsPreview.getGson();

        try (Writer writer = new FileWriter(KitsPreview.getInstance().getDataFolder() + File.separator + kitName + ".json")) {
            gson.toJson(this, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static KitPreview getByName(String name){
        return kits.get(name.toLowerCase());
    }

}