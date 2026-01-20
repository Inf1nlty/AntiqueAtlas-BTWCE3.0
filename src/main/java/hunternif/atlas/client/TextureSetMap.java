package hunternif.atlas.client;

import hunternif.atlas.util.SaveData;
import net.minecraft.src.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TextureSetMap extends SaveData {
    private static final TextureSetMap INSTANCE = new TextureSetMap();
    private final Map<String, TextureSet> map = new HashMap();

    public static TextureSetMap instance() {
        return INSTANCE;
    }

    public void register(TextureSet set) {
        TextureSet old = (TextureSet)this.map.put(set.name, set);
        if (!set.equals(old)) {
            this.markDirty();
        }

    }

    public TextureSet createAndRegister(ResourceLocation... textures) {
        TextureSet set = new TextureSet(UUID.randomUUID().toString(), textures);
        this.register(set);
        return set;
    }

    public TextureSet getByName(String name) {
        return (TextureSet)this.map.get(name);
    }

    public TextureSet getByNameNonNull(String name) {
        TextureSet set = this.getByName(name);
        return set == null ? TextureSet.TEST : set;
    }

    public boolean isRegistered(String name) {
        return this.map.containsKey(name);
    }

    public Collection<TextureSet> getAllTextureSets() {
        return this.map.values();
    }

    public Collection<TextureSet> getAllNonStandardTextureSets() {
        List<TextureSet> list = new ArrayList(this.map.size());

        for(TextureSet set : this.map.values()) {
            if (!set.isStandard) {
                list.add(set);
            }
        }

        return list;
    }
}
