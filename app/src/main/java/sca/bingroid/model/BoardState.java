package sca.bingroid.model;

import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

class BoardState implements Serializable {

    private final List<Tile> mtbPlacedTiles = Arrays.asList( new Tile[GameEngine.NB_DRAWN_TILES] );
    private int mNbPlacedTiles = 0;

    void placeTile( int pos,@Nullable Tile tile ) {
        Tile prevTile = getTile(pos);
        mtbPlacedTiles.set( pos, tile );
        if (prevTile == null && tile != null) {
            mNbPlacedTiles++;
        } else if (prevTile != null && tile == null) {
            mNbPlacedTiles--;
        }
    }

    Tile getTile( int pos ) {
        return mtbPlacedTiles.get(pos);
    }

    int getNbPlacedTiles() {
        return mNbPlacedTiles;
    }

    private static final String JSON_PLACED_TILES = "placedTiles";

    public JSONObject toJSON() throws JSONException {
        JSONArray array = new JSONArray();
        for (Tile tile : mtbPlacedTiles) {
            if (tile != null) {
                array.put( tile.toString() );
            } else {
                array.put(null);
            }
        }
        JSONObject json = new JSONObject();
        json.put( JSON_PLACED_TILES, array );
        return json;
    }

    public static BoardState ofJSON( JSONObject json ) throws JSONException {
        BoardState state = new BoardState();
        JSONArray array = (JSONArray) json.get(JSON_PLACED_TILES);
        for (int i=0; i<array.length(); i++) {
            if ( ! array.isNull(i) ) {
                String value = array.getString(i);
                if (value != null) {
                    Tile tile = Tile.ofString(value);
                    state.placeTile(i, tile);
                }
            }
        }
        return state;
    }
}
