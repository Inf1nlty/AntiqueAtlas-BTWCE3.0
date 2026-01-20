package hunternif.atlas.core;


import net.minecraft.src.Item;

public class Tile {
    public final int biomeID;
    private transient short variationNumber;

    public Tile(int biomeID) {
        this(biomeID, (byte)0);
        this.randomizeTexture();
    }

    public Tile(int biomeID, byte variationNumber) {
        this.biomeID = biomeID;
        this.variationNumber = (short)variationNumber;
    }

    public void randomizeTexture() {
        this.variationNumber = (short) Item.itemRand.nextInt(32767);
    }

    public short getVariationNumber() {
        return this.variationNumber;
    }

    public String toString() {
        return "tile" + this.biomeID;
    }
}
