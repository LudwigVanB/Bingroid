package sca.bingroid.model;

import java.util.ArrayList;
import java.util.Collections;

class TilesBag {

    private static final String LOG_TAG = TilesBag.class.getSimpleName();

    private ArrayList<Tile> mtbTiles = new ArrayList<>(40);

    TilesBag() {
        putAllTiles();
        Collections.shuffle( mtbTiles );
    }

    TilesBag(BoardState mBoardState) {
        putAllTiles();
        for (int posBoard=0; posBoard<GameEngine.NB_DRAWN_TILES; posBoard++) {
            Tile usedTile = mBoardState.getTile(posBoard);
            if (usedTile != null) {
                for (int posBag=mtbTiles.size()-1; posBag>=0; posBag--) {
                    Tile baggedTile = mtbTiles.get(posBag);
                    if ( usedTile.equivalent( baggedTile ) ) {
                        mtbTiles.remove(posBag);
                        break;
                    }
                }
            }
        }
        Collections.shuffle( mtbTiles );
    }

    private void putAllTiles() {
        mtbTiles.clear();
        for (Integer value : GameEngine.TB_TILES) {
            if (value == null) {
                // wildcard
                mtbTiles.add( new Tile() );
            } else {
                mtbTiles.add( new Tile(value) );
            }
        }
    }


    Tile draw() {
        return mtbTiles.remove( 0 );
    }


}
