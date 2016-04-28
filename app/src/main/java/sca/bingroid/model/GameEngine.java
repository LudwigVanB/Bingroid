package sca.bingroid.model;


import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GameEngine {
    public static final int NB_DRAWN_TILES = 20;

    static final Integer[] TB_TILES = new Integer[] {
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
            11, 12, 13, 14, 15, 16, 17 ,18 ,19,
            11, 12, 13, 14, 15, 16, 17 ,18 ,19,
            20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30,
            null
    };

    private static final int[] TB_SCORES = new int[] {
            0, 1 ,3, 5, 7,
            9, 11, 15, 20, 25,
            30, 35, 40, 50, 60,
            70, 85, 100, 150, 300 };

    private static final String LOG_TAG = GameEngine.class.getSimpleName();

    public static class PlacementResult {
        boolean mIsValid;
        int mPreviousPos;

        private PlacementResult(boolean isValid, int previousPos) {
            mIsValid = isValid;
            mPreviousPos = previousPos;
        }

        public boolean isValid() {
            return mIsValid;
        }
        public int getPreviousPos() {
            return mPreviousPos;
        }
    }

    private BoardState mBoardState;
    private TilesBag mTilesBag;

    private Tile mCurrentTile;
    private int mCurrentTilePlacedPos;
    private ArrayList<Tile> mListDrawnTiles;

    private GameEngine() { }

    public void reset() {
        mBoardState = new BoardState();
        mTilesBag = new TilesBag();
        mCurrentTile = null;
        mCurrentTilePlacedPos = -1;
        mListDrawnTiles = new ArrayList<>(NB_DRAWN_TILES);
    }


    void setState( BoardState state ) {
        mBoardState = state;
    }

    private static final String JSON_CURRENT_TILE = "currentTile";
    private static final String JSON_CURRENT_TILE_PLACED = "currentTilePlacedPos";
    private static final String JSON_BOARD_STATE = "boardState";
    private static final String JSON_TILES_HISTORY = "tilesHistory";

    @Override
    public String toString() {
        if (mCurrentTile == null) return null;
        else {
            try {
                JSONObject json = new JSONObject();
                if (mCurrentTile != null) {
                    json.put( JSON_CURRENT_TILE, mCurrentTile.toString() );
                    json.put( JSON_CURRENT_TILE_PLACED, mCurrentTilePlacedPos );
                }

                JSONArray arrayHistory = new JSONArray();
                for (Tile tile : mListDrawnTiles) {
                    arrayHistory.put( tile.toString() );
                }
                json.put( JSON_TILES_HISTORY, arrayHistory );

                json.put( JSON_BOARD_STATE, mBoardState.toJSON() );
                return json.toString();
            } catch (JSONException e) {
                Log.e( LOG_TAG, "Couldn't save state", e );
                return null;
            }
        }
    }

    public static GameEngine ofString(@Nullable String gameState ) {
        GameEngine gameEngine = new GameEngine();
        if (gameState == null) {
            gameEngine.reset();
        } else {
            try {
                JSONObject json = new JSONObject(gameState);
                String currentTileStr = json.getString(JSON_CURRENT_TILE);
                if (currentTileStr != null) {
                    gameEngine.mCurrentTilePlacedPos = json.getInt(JSON_CURRENT_TILE_PLACED);
                    gameEngine.mCurrentTile = Tile.ofString(currentTileStr);
                }

                gameEngine.mListDrawnTiles = new ArrayList<>();
                JSONArray array = (JSONArray) json.get(JSON_TILES_HISTORY);
                for (int i=0; i<array.length(); i++) {
                    String value = array.getString(i);
                    Tile tile = Tile.ofString(value);
                    gameEngine.mListDrawnTiles.add(tile);
                }

                JSONObject jsonBoardState = (JSONObject) json.get(JSON_BOARD_STATE);
                gameEngine.mBoardState = BoardState.ofJSON( jsonBoardState );
                gameEngine.mTilesBag = new TilesBag( gameEngine.mBoardState );
            } catch (JSONException e) {
                Log.e( LOG_TAG, "Couldn't restore state", e );
                gameEngine.reset();
            }
        }
        return gameEngine;
    }

    public boolean canDraw() {
        return !isGameOver() && ( mCurrentTile == null || mCurrentTilePlacedPos != -1 );
    }

    public Tile draw() {
        if ( canDraw() ) {
            mCurrentTilePlacedPos = -1;
            mCurrentTile = mTilesBag.draw();
            mListDrawnTiles.add( mCurrentTile );
            return mCurrentTile;
        } else {
            return null;
        }
    }

    public PlacementResult placeCurrentTile(int pos) {
        if ( mBoardState.getTile(pos) != null ) {
            return new PlacementResult(false, -1);
        } else {
            if (mCurrentTilePlacedPos != -1) {
                mBoardState.placeTile( mCurrentTilePlacedPos, null );
            }
            int oldPos = mCurrentTilePlacedPos;
            mBoardState.placeTile(pos, mCurrentTile);
            mCurrentTilePlacedPos = pos;
            return new PlacementResult(true, oldPos);
        }
    }

    public Tile getCurrentTile() {
        return mCurrentTile;
    }

    public Tile getTile( int pos ) { return mBoardState.getTile(pos); }

    public boolean isGameOver() {
        return mBoardState.getNbPlacedTiles() == NB_DRAWN_TILES;
    }

    private static class Series {
        Series( int startPosIn, int endPosIn) {
            startPos = startPosIn;
            endPos = endPosIn;
        }

        public int startPos; // inclusive
        public int endPos; //inclusive

        public int getScore() {
            return TB_SCORES[endPos-startPos];
        }
    }

    private int getPartialScore( Series currentSeries, Tile prevNonWildcardTile ) {
        int score = 0;
        for (int pos = currentSeries.endPos+1; pos<NB_DRAWN_TILES; pos++) {
            Tile currentTile = mBoardState.getTile(pos);
            if (!currentTile.isWildcard()) {
                if (prevNonWildcardTile == null || prevNonWildcardTile.getValue() <= currentTile.getValue()) {
                    // the series keep growing
                    currentSeries.endPos = pos;
                } else {
                    // Broken series
                    score += currentSeries.getScore();
                    currentSeries = new Series(pos, pos);
                }
                prevNonWildcardTile = currentTile;
            } else { // We have a wildcard
                Series currentSeriesWithWildcard = new Series( currentSeries.startPos, pos );
                int scoreWithWildcardInCurrentSeries = score + getPartialScore( currentSeriesWithWildcard, prevNonWildcardTile );
                Series newSeriesWithWildcard = new Series( pos, pos );
                int scoreWithWildcardInNewSeries = score + currentSeries.getScore() + getPartialScore(newSeriesWithWildcard, null );
                return Math.max( scoreWithWildcardInCurrentSeries, scoreWithWildcardInNewSeries );
            }
        }
        return score + currentSeries.getScore();
    }

    public int getScore() {
        Tile firstTile = mBoardState.getTile(0);
        int score = getPartialScore( new Series(0,0), firstTile.isWildcard() ? null : firstTile );
        return score;
    }

    public Tile[] getTilesHistory() {
        return mListDrawnTiles.toArray( new Tile[mListDrawnTiles.size()] );
    }


}
