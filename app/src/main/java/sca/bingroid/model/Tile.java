package sca.bingroid.model;

import android.support.annotation.Nullable;

public class Tile {

    private final boolean mWildcard;
    private final int mValue;

    public Tile() {
        mValue = -1;
        mWildcard = true;
    }

    public Tile( int value ) {
        mValue = value;
        mWildcard = false;
    }

    public boolean isWildcard() {
        return mWildcard;
    }

    public int getValue() {
        return mValue;
    }

    boolean equivalent(Tile otherTile ) { // not equals, we want to keep the reference based equality
        return ( isWildcard() && otherTile.isWildcard() ) || (getValue() == otherTile.getValue());
    }


    @Override
    public String toString() {
        if (mWildcard) return "*";
        else return Integer.toString(mValue);
    }

    public static @Nullable Tile ofString(@Nullable String value) {
        if (value == null) {
            return null;
        } else if (value.equals("*")) {
            return new Tile();
        } else {
            int numValue = Integer.parseInt(value);
            return new Tile(numValue);
        }
    }
}
