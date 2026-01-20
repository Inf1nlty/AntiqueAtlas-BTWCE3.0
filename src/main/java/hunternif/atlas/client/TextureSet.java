package hunternif.atlas.client;

import net.minecraft.src.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

public class TextureSet implements Comparable<TextureSet> {
    public static final TextureSet TEST;
    public static final TextureSet ICE;
    public static final TextureSet DESERT;
    public static final TextureSet HILLS;
    public static final TextureSet DESERT_HILLS;
    public static final TextureSet PLAINS;
    public static final TextureSet SUNFLOWERS;
    public static final TextureSet ICE_SPIKES;
    public static final TextureSet SNOW_PINES;
    public static final TextureSet SNOW_PINES_HILLS;
    public static final TextureSet SNOW_HILLS;
    public static final TextureSet SNOW;
    public static final TextureSet MOUNTAINS_NAKED;
    public static final TextureSet MOUNTAINS;
    public static final TextureSet MOUNTAINS_SNOW_CAPS;
    public static final TextureSet MOUNTAINS_ALL;
    public static final TextureSet FOREST;
    public static final TextureSet FOREST_HILLS;
    public static final TextureSet FOREST_FLOWERS;
    public static final TextureSet SPARSE_FOREST;
    public static final TextureSet SPARSE_FOREST_HILLS;
    public static final TextureSet DENSE_FOREST;
    public static final TextureSet DENSE_FOREST_HILLS;
    public static final TextureSet BIRCH;
    public static final TextureSet BIRCH_HILLS;
    public static final TextureSet TALL_BIRCH;
    public static final TextureSet TALL_BIRCH_HILLS;
    public static final TextureSet DENSE_BIRCH;
    public static final TextureSet JUNGLE;
    public static final TextureSet JUNGLE_HILLS;
    public static final TextureSet JUNGLE_CLIFFS;
    public static final TextureSet JUNGLE_EDGE;
    public static final TextureSet JUNGLE_EDGE_HILLS;
    public static final TextureSet PINES;
    public static final TextureSet PINES_HILLS;
    public static final TextureSet MEGA_SPRUCE;
    public static final TextureSet MEGA_SPRUCE_HILLS;
    public static final TextureSet MEGA_TAIGA;
    public static final TextureSet MEGA_TAIGA_HILLS;
    public static final TextureSet SAVANNA;
    public static final TextureSet SAVANNA_CLIFFS;
    public static final TextureSet PLATEAU_SAVANNA;
    public static final TextureSet PLATEAU_SAVANNA_M;
    public static final TextureSet MESA;
    public static final TextureSet BRYCE;
    public static final TextureSet PLATEAU_MESA;
    public static final TextureSet PLATEAU_MESA_LOW;
    public static final TextureSet PLATEAU_MESA_TREES;
    public static final TextureSet PLATEAU_MESA_TREES_LOW;
    public static final TextureSet SWAMP;
    public static final TextureSet SWAMP_HILLS;
    public static final TextureSet WATER;
    public static final TextureSet LAVA;
    public static final TextureSet SHORE;
    public static final TextureSet ROCK_SHORE;
    public static final TextureSet LAVA_SHORE;
    public static final TextureSet MUSHROOM;
    public static final TextureSet CAVE_WALLS;
    public static final TextureSet HOUSE;
    public static final TextureSet FENCE;
    public static final TextureSet LIBRARY;
    public static final TextureSet SMITHY;
    public static final TextureSet L_HOUSE;
    public static final TextureSet FARMLAND_LARGE;
    public static final TextureSet FARMLAND_SMALL;
    public static final TextureSet VILLAGE_TORCH;
    public static final TextureSet WELL;
    public static final TextureSet HUT;
    public static final TextureSet HOUSE_SMALL;
    public static final TextureSet BUTCHERS_SHOP;
    public static final TextureSet CHURCH;
    public static final TextureSet NETHER_BRIDGE;
    public static final TextureSet NETHER_BRIDGE_X;
    public static final TextureSet NETHER_BRIDGE_Z;
    public static final TextureSet NETHER_BRIDGE_END_X;
    public static final TextureSet NETHER_BRIDGE_END_Z;
    public static final TextureSet NETHER_BRIDGE_GATE;
    public static final TextureSet NETHER_TOWER;
    public static final TextureSet NETHER_WALL;
    public static final TextureSet NETHER_HALL;
    public static final TextureSet NETHER_FORT_STAIRS;
    public static final TextureSet NETHER_THRONE;
    public final String name;
    public final ResourceLocation[] textures;
    private final Set<TextureSet> stitchTo;
    private final Set<TextureSet> stitchToHorizontal;
    private final Set<TextureSet> stitchToVertical;
    final boolean isStandard;
    private boolean stitchesToNull;
    private boolean anisotropicStitching;

    private static TextureSet standard(String name, ResourceLocation... textures) {
        return new TextureSet(true, name, textures);
    }

    private TextureSet(boolean isStandard, String name, ResourceLocation... textures) {
        this.stitchTo = new HashSet();
        this.stitchToHorizontal = new HashSet();
        this.stitchToVertical = new HashSet();
        this.stitchesToNull = false;
        this.anisotropicStitching = false;
        this.isStandard = isStandard;
        this.name = name;
        this.textures = textures;
    }

    public TextureSet(String name, ResourceLocation... textures) {
        this(false, name, textures);
    }

    public TextureSet stitchesToNull() {
        this.stitchesToNull = true;
        return this;
    }

    public TextureSet stitchTo(TextureSet... textureSets) {
        for(TextureSet textureSet : textureSets) {
            this.stitchTo.add(textureSet);
        }

        return this;
    }

    public TextureSet stitchToMutual(TextureSet... textureSets) {
        for(TextureSet textureSet : textureSets) {
            this.stitchTo.add(textureSet);
            textureSet.stitchTo.add(this);
        }

        return this;
    }

    public TextureSet stitchToHorizontal(TextureSet... textureSets) {
        this.anisotropicStitching = true;

        for(TextureSet textureSet : textureSets) {
            this.stitchToHorizontal.add(textureSet);
        }

        return this;
    }

    public TextureSet stitchToVertical(TextureSet... textureSets) {
        this.anisotropicStitching = true;

        for(TextureSet textureSet : textureSets) {
            this.stitchToVertical.add(textureSet);
        }

        return this;
    }

    public boolean shouldStitchTo(TextureSet toSet) {
        return toSet == this || this.stitchesToNull && toSet == null || this.stitchTo.contains(toSet);
    }

    public boolean shouldStitchToHorizontally(TextureSet toSet) {
        if (toSet != this && (!this.stitchesToNull || toSet != null)) {
            return this.anisotropicStitching ? this.stitchToHorizontal.contains(toSet) : this.stitchTo.contains(toSet);
        } else {
            return true;
        }
    }

    public boolean shouldStitchToVertically(TextureSet toSet) {
        if (toSet != this && (!this.stitchesToNull || toSet != null)) {
            return this.anisotropicStitching ? this.stitchToVertical.contains(toSet) : this.stitchTo.contains(toSet);
        } else {
            return true;
        }
    }

    public boolean equals(Object obj) {
        if (obj != null && obj instanceof TextureSet set) {
            return this.name.equals(set.name);
        } else {
            return false;
        }
    }

    public static final void stitchMutually(TextureSet... sets) {
        for(TextureSet set1 : sets) {
            for(TextureSet set2 : sets) {
                if (set1 != set2) {
                    set1.stitchTo(set2);
                }
            }
        }

    }

    public static final void stitchMutuallyHorizontally(TextureSet... sets) {
        for(TextureSet set1 : sets) {
            for(TextureSet set2 : sets) {
                if (set1 != set2) {
                    set1.stitchToHorizontal(set2);
                }
            }
        }

    }

    public static final void stitchMutuallyVertically(TextureSet... sets) {
        for(TextureSet set1 : sets) {
            for(TextureSet set2 : sets) {
                if (set1 != set2) {
                    set1.stitchToVertical(set2);
                }
            }
        }

    }

    public int compareTo(TextureSet textureSet) {
        return this.name.compareTo(textureSet.name);
    }

    static {
        TEST = new TextureSet(false, "TEST", new ResourceLocation[]{Textures.TILE_TEST, Textures.TILE_TEST});
        ICE = standard("ICE", Textures.TILE_ICE_NOBORDER);
        DESERT = standard("DESERT", Textures.TILE_SAND, Textures.TILE_SAND, Textures.TILE_SAND2, Textures.TILE_SAND2, Textures.TILE_SAND3, Textures.TILE_SAND3, Textures.TILE_SAND_BUSHES, Textures.TILE_SAND_BUSHES, Textures.TILE_CACTI);
        HILLS = standard("HILLS", Textures.TILE_HILLS);
        DESERT_HILLS = standard("DESERT_HILLS", Textures.TILE_HILLS, Textures.TILE_HILLS, Textures.TILE_HILLS, Textures.TILE_HILLS_BUSHES, Textures.TILE_HILLS_CACTI);
        PLAINS = standard("PLAINS", Textures.TILE_GRASS, Textures.TILE_GRASS2, Textures.TILE_GRASS3, Textures.TILE_GRASS4);
        SUNFLOWERS = standard("SUNFLOWERS", Textures.TILE_SUNFLOWERS, Textures.TILE_SUNFLOWERS2, Textures.TILE_GRASS3, Textures.TILE_GRASS4);
        ICE_SPIKES = standard("ICE_SPIKES", Textures.TILE_ICE_SPIKES, Textures.TILE_ICE_SPIKES2);
        SNOW_PINES = standard("SNOW_PINES", Textures.TILE_SNOW_PINES, Textures.TILE_SNOW_PINES2, Textures.TILE_SNOW_PINES3);
        SNOW_PINES_HILLS = standard("SNOW_PINES_HILLS", Textures.TILE_SNOW_PINES_HILLS, Textures.TILE_SNOW_PINES_HILLS2, Textures.TILE_SNOW_PINES_HILLS3);
        SNOW_HILLS = standard("SNOW_HILLS", Textures.TILE_SNOW_HILLS, Textures.TILE_SNOW_HILLS2);
        SNOW = standard("SNOW", Textures.TILE_SNOW, Textures.TILE_SNOW, Textures.TILE_SNOW, Textures.TILE_SNOW, Textures.TILE_SNOW, Textures.TILE_SNOW1, Textures.TILE_SNOW1, Textures.TILE_SNOW1, Textures.TILE_SNOW2, Textures.TILE_SNOW2, Textures.TILE_SNOW2, Textures.TILE_SNOW3, Textures.TILE_SNOW4, Textures.TILE_SNOW5, Textures.TILE_SNOW6);
        MOUNTAINS_NAKED = standard("MOUNTAINS_NAKED", Textures.TILE_MOUNTAINS, Textures.TILE_MOUNTAINS2);
        MOUNTAINS = standard("MOUNTAINS", Textures.TILE_MOUNTAINS, Textures.TILE_MOUNTAINS, Textures.TILE_MOUNTAINS2, Textures.TILE_MOUNTAINS2, Textures.TILE_MOUNTAINS3, Textures.TILE_MOUNTAINS4);
        MOUNTAINS_SNOW_CAPS = standard("MOUNTAINS_SNOW_CAPS", Textures.TILE_MOUNTAINS, Textures.TILE_SNOW_CAPS);
        MOUNTAINS_ALL = standard("MOUNTAINS_ALL", Textures.TILE_SNOW_CAPS, Textures.TILE_SNOW_CAPS, Textures.TILE_MOUNTAINS, Textures.TILE_MOUNTAINS2, Textures.TILE_MOUNTAINS3, Textures.TILE_MOUNTAINS4);
        FOREST = standard("FOREST", Textures.TILE_FOREST, Textures.TILE_FOREST2, Textures.TILE_FOREST3);
        FOREST_HILLS = standard("FOREST_HILLS", Textures.TILE_FOREST_HILLS, Textures.TILE_FOREST_HILLS2, Textures.TILE_FOREST_HILLS3);
        FOREST_FLOWERS = standard("FOREST_FLOWERS", Textures.TILE_FOREST_FLOWERS, Textures.TILE_FOREST_FLOWERS2, Textures.TILE_FOREST_FLOWERS3);
        SPARSE_FOREST = standard("SPARSE_FOREST", Textures.TILE_SPARSE_FOREST, Textures.TILE_SPARSE_FOREST2, Textures.TILE_SPARSE_FOREST3);
        SPARSE_FOREST_HILLS = standard("SPARSE_FOREST_HILLS", Textures.TILE_SPARSE_FOREST_HILLS, Textures.TILE_SPARSE_FOREST_HILLS2, Textures.TILE_SPARSE_FOREST_HILLS3);
        DENSE_FOREST = standard("DENSE_FOREST", Textures.TILE_DENSE_FOREST, Textures.TILE_DENSE_FOREST2);
        DENSE_FOREST_HILLS = standard("DENSE_FOREST_HILLS", Textures.TILE_DENSE_FOREST_HILLS, Textures.TILE_DENSE_FOREST_HILLS2);
        BIRCH = standard("BIRCH", Textures.TILE_BIRCH, Textures.TILE_BIRCH2);
        BIRCH_HILLS = standard("BIRCH_HILLS", Textures.TILE_BIRCH_HILLS, Textures.TILE_BIRCH_HILLS2);
        TALL_BIRCH = standard("TALL_BIRCH", Textures.TILE_TALL_BIRCH, Textures.TILE_TALL_BIRCH2);
        TALL_BIRCH_HILLS = standard("TALL_BIRCH_HILLS", Textures.TILE_TALL_BIRCH_HILLS, Textures.TILE_TALL_BIRCH_HILLS2);
        DENSE_BIRCH = standard("DENSE_BIRCH", Textures.TILE_DENSE_BIRCH);
        JUNGLE = standard("JUNGLE", Textures.TILE_JUNGLE, Textures.TILE_JUNGLE2);
        JUNGLE_HILLS = standard("JUNGLE_HILLS", Textures.TILE_JUNGLE_HILLS, Textures.TILE_JUNGLE_HILLS2);
        JUNGLE_CLIFFS = standard("JUNGLE_CLIFFS", Textures.TILE_JUNGLE_CLIFFS, Textures.TILE_JUNGLE_CLIFFS2, Textures.TILE_BUSHES_CLIFFS);
        JUNGLE_EDGE = standard("JUNGLE_EDGE", Textures.TILE_JUNGLE_EDGE, Textures.TILE_JUNGLE_EDGE2, Textures.TILE_JUNGLE_EDGE3, Textures.TILE_GRASS2, Textures.TILE_GRASS3, Textures.TILE_GRASS4);
        JUNGLE_EDGE_HILLS = standard("JUNGLE_EDGE_HILLS", Textures.TILE_JUNGLE_EDGE_HILLS, Textures.TILE_JUNGLE_EDGE_HILLS2, Textures.TILE_JUNGLE_EDGE_HILLS3, Textures.TILE_HILLS_GRASS, Textures.TILE_HILLS_GRASS);
        PINES = standard("PINES", Textures.TILE_PINES, Textures.TILE_PINES2, Textures.TILE_PINES3);
        PINES_HILLS = standard("PINES_HILLS", Textures.TILE_PINES_HILLS, Textures.TILE_PINES_HILLS2, Textures.TILE_PINES_HILLS3);
        MEGA_SPRUCE = standard("MEGA_SPRUCE", Textures.TILE_MEGA_SPRUCE, Textures.TILE_MEGA_SPRUCE2);
        MEGA_SPRUCE_HILLS = standard("MEGA_SPRUCE_HILLS", Textures.TILE_MEGA_SPRUCE_HILLS, Textures.TILE_MEGA_SPRUCE_HILLS2);
        MEGA_TAIGA = standard("MEGA_TAIGA", Textures.TILE_MEGA_TAIGA, Textures.TILE_MEGA_TAIGA2);
        MEGA_TAIGA_HILLS = standard("MEGA_TAIGA_HILLS", Textures.TILE_MEGA_TAIGA_HILLS, Textures.TILE_MEGA_TAIGA_HILLS2);
        SAVANNA = standard("SAVANNA", Textures.TILE_SAVANNA, Textures.TILE_SAVANNA2, Textures.TILE_SAVANNA3, Textures.TILE_GRASS, Textures.TILE_GRASS2, Textures.TILE_GRASS2, Textures.TILE_GRASS3, Textures.TILE_GRASS3, Textures.TILE_GRASS4, Textures.TILE_GRASS4);
        SAVANNA_CLIFFS = standard("SAVANNA_CLIFFS", Textures.TILE_SAVANNA_CLIFFS, Textures.TILE_SAVANNA_CLIFFS2, Textures.TILE_SAVANNA_CLIFFS3, Textures.TILE_CLIFFS);
        PLATEAU_SAVANNA = standard("PLATEAU_SAVANNA", Textures.TILE_PLATEAU_GRASS, Textures.TILE_PLATEAU_GRASS, Textures.TILE_PLATEAU_GRASS2, Textures.TILE_PLATEAU_GRASS2, Textures.TILE_PLATEAU_GRASS3, Textures.TILE_PLATEAU_GRASS3, Textures.TILE_PLATEAU_SAVANNA, Textures.TILE_PLATEAU_SAVANNA2, Textures.TILE_PLATEAU_SAVANNA3);
        PLATEAU_SAVANNA_M = standard("PLATEAU_SAVANNA_M", Textures.TILE_PLATEAU_GRASS, Textures.TILE_PLATEAU_GRASS2, Textures.TILE_PLATEAU_GRASS3, Textures.TILE_PLATEAU_SAVANNA2, Textures.TILE_PLATEAU_SAVANNA3, Textures.TILE_CLIFFS_CLOUDS, Textures.TILE_SAVANNA_CLIFFS_CLOUDS, Textures.TILE_SAVANNA_CLIFFS_CLOUDS2, Textures.TILE_SAVANNA_CLIFFS_CLOUDS3);
        MESA = standard("MESA", Textures.TILE_MESA, Textures.TILE_MESA2, Textures.TILE_MESA3, Textures.TILE_MESA4, Textures.TILE_SAND_BUSHES);
        BRYCE = standard("BRYCE", Textures.TILE_BRYCE, Textures.TILE_BRYCE2, Textures.TILE_BRYCE3, Textures.TILE_BRYCE4);
        PLATEAU_MESA = standard("PLATEAU_MESA", Textures.TILE_PLATEAU_MESA, Textures.TILE_PLATEAU_MESA2);
        PLATEAU_MESA_LOW = standard("PLATEAU_MESA_LOW", Textures.TILE_PLATEAU_MESA_LOW, Textures.TILE_PLATEAU_MESA_LOW2);
        PLATEAU_MESA_TREES = standard("PLATEAU_MESA_TREES", Textures.TILE_PLATEAU_MESA, Textures.TILE_PLATEAU_MESA2, Textures.TILE_PLATEAU_TREES);
        PLATEAU_MESA_TREES_LOW = standard("PLATEAU_MESA_TREES_LOW", Textures.TILE_PLATEAU_MESA_LOW, Textures.TILE_PLATEAU_MESA_LOW2, Textures.TILE_PLATEAU_TREES_LOW);
        SWAMP = standard("SWAMP", Textures.TILE_SWAMP, Textures.TILE_SWAMP, Textures.TILE_SWAMP, Textures.TILE_SWAMP2, Textures.TILE_SWAMP3, Textures.TILE_SWAMP4, Textures.TILE_SWAMP5, Textures.TILE_SWAMP6);
        SWAMP_HILLS = standard("SWAMP_HILLS", Textures.TILE_SWAMP_HILLS, Textures.TILE_SWAMP_HILLS2, Textures.TILE_SWAMP_HILLS3, Textures.TILE_SWAMP_HILLS4, Textures.TILE_SWAMP_HILLS5);
        WATER = standard("WATER", Textures.TILE_WATER, Textures.TILE_WATER2);
        LAVA = standard("LAVA", Textures.TILE_LAVA, Textures.TILE_LAVA2);
        SHORE = new TextureSetShore("SHORE", WATER, new ResourceLocation[]{Textures.TILE_SHORE, Textures.TILE_SHORE2, Textures.TILE_SHORE3});
        ROCK_SHORE = (new TextureSetShore("ROCK_SHORE", WATER, new ResourceLocation[]{Textures.TILE_ROCK_SHORE})).stitchesToNull();
        LAVA_SHORE = (new TextureSetShore("LAVA_SHORE", LAVA, new ResourceLocation[]{Textures.TILE_LAVA_SHORE, Textures.TILE_LAVA_SHORE2})).stitchesToNull();
        MUSHROOM = standard("MUSHROOM", Textures.TILE_MUSHROOM, Textures.TILE_MUSHROOM2);
        CAVE_WALLS = standard("CAVE_WALLS", Textures.TILE_CAVE_WALLS);
        HOUSE = standard("HOUSE", Textures.TILE_HOUSE);
        FENCE = standard("FENCE", Textures.TILE_FENCE).stitchTo(HOUSE);
        LIBRARY = standard("LIBRARY", Textures.TILE_LIBRARY);
        SMITHY = standard("SMITHY", Textures.TILE_SMITHY);
        L_HOUSE = standard("L-HOUSE", Textures.TILE_L_HOUSE);
        FARMLAND_LARGE = standard("FARMLAND_LARGE", Textures.TILE_FARMLAND_LARGE);
        FARMLAND_SMALL = standard("FARMLAND_SMALL", Textures.TILE_FARMLAND_SMALL);
        VILLAGE_TORCH = standard("VILLAGE_TORCH", Textures.TILE_VILLAGE_TORCH);
        WELL = standard("WELL", Textures.TILE_WELL);
        HUT = standard("HUT", Textures.TILE_HUT);
        HOUSE_SMALL = standard("HOUSE_SMALL", Textures.TILE_HOUSE_SMALL);
        BUTCHERS_SHOP = standard("BUTCHERS_SHOP", Textures.TILE_BUTCHERS_SHOP);
        CHURCH = standard("CHURCH", Textures.TILE_CHURCH);
        NETHER_BRIDGE = standard("NETHER_BRIDGE", Textures.TILE_NETHER_BRIDGE);
        NETHER_BRIDGE_X = standard("NETHER_BRIDGE_X", Textures.TILE_NETHER_BRIDGE_X);
        NETHER_BRIDGE_Z = standard("NETHER_BRIDGE_Z", Textures.TILE_NETHER_BRIDGE_Z);
        NETHER_BRIDGE_END_X = standard("NETHER_BRIDGE_END_X", Textures.TILE_NETHER_BRIDGE_END_X);
        NETHER_BRIDGE_END_Z = standard("NETHER_BRIDGE_END_Z", Textures.TILE_NETHER_BRIDGE_END_Z);
        NETHER_BRIDGE_GATE = standard("NETHER_BRIDGE_GATE", Textures.TILE_NETHER_BRIDGE_GATE);
        NETHER_TOWER = standard("NETHER_TOWER", Textures.TILE_NETHER_TOWER);
        NETHER_WALL = standard("NETHER_WALL", Textures.TILE_NETHER_WALL);
        NETHER_HALL = standard("NETHER_HALL", Textures.TILE_NETHER_HALL);
        NETHER_FORT_STAIRS = standard("NETHER_FORT_STAIRS", Textures.TILE_NETHER_FORT_STAIRS);
        NETHER_THRONE = standard("NETHER_THRONE", Textures.TILE_NETHER_THRONE);
        stitchMutually(PLAINS, SUNFLOWERS);
        WATER.stitchTo(SHORE, ROCK_SHORE, SWAMP);
        LAVA.stitchTo(LAVA_SHORE);
        SWAMP.stitchTo(SWAMP_HILLS);
        SNOW.stitchTo(SNOW_PINES, SNOW_HILLS, ICE_SPIKES, SNOW_PINES_HILLS);
        SNOW_PINES.stitchTo(SNOW, SNOW_HILLS, ICE_SPIKES, SNOW_PINES_HILLS);
        stitchMutually(MOUNTAINS, MOUNTAINS_NAKED, MOUNTAINS_SNOW_CAPS, MOUNTAINS_ALL);
        DESERT.stitchTo(MESA, BRYCE);
        stitchMutually(PLATEAU_MESA, PLATEAU_MESA_TREES, PLATEAU_SAVANNA, PLATEAU_SAVANNA_M);
        stitchMutually(PLATEAU_MESA_LOW, PLATEAU_MESA_TREES_LOW);
        LAVA.stitchTo(NETHER_BRIDGE, NETHER_BRIDGE_GATE, NETHER_TOWER, NETHER_WALL, NETHER_HALL, NETHER_FORT_STAIRS, NETHER_BRIDGE_X, NETHER_BRIDGE_END_X, NETHER_BRIDGE_Z, NETHER_BRIDGE_END_Z);
        stitchMutuallyHorizontally(NETHER_BRIDGE, NETHER_BRIDGE_GATE, NETHER_TOWER, NETHER_HALL, NETHER_FORT_STAIRS, NETHER_THRONE, NETHER_BRIDGE_X, NETHER_BRIDGE_END_X);
        stitchMutuallyVertically(NETHER_BRIDGE, NETHER_BRIDGE_GATE, NETHER_TOWER, NETHER_HALL, NETHER_FORT_STAIRS, NETHER_THRONE, NETHER_BRIDGE_Z, NETHER_BRIDGE_END_Z);
        stitchMutuallyHorizontally(NETHER_WALL, NETHER_HALL, NETHER_FORT_STAIRS);
        stitchMutuallyVertically(NETHER_WALL, NETHER_HALL, NETHER_FORT_STAIRS);
    }

    private static class TextureSetShore extends TextureSet {
        private final TextureSet water;

        public TextureSetShore(String name, TextureSet water, ResourceLocation... textures) {
            super(true, name, textures);
            this.water = water;
        }

        public boolean shouldStitchToHorizontally(TextureSet otherSet) {
            return otherSet == this || !this.water.shouldStitchToHorizontally(otherSet);
        }

        public boolean shouldStitchToVertically(TextureSet otherSet) {
            return otherSet == this || !this.water.shouldStitchToVertically(otherSet);
        }
    }
}
