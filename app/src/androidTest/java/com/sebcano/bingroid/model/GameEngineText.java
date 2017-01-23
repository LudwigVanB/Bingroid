package com.sebcano.bingroid.model;


import android.test.AndroidTestCase;

public class GameEngineText extends AndroidTestCase {

    private static void checkTileEquivalent( Tile tile1, Tile tile2) {
        if (tile1 == null || tile2 == null ) {
            assertTrue( tile1 == null && tile2 == null);
        } else {
            assertTrue(tile1.equivalent(tile2));
        }
    }

    private static void checkEngineStateRestore( GameEngine engineSrc ) {
        String state = engineSrc.toString();

        GameEngine restoredEngine = GameEngine.ofString(state);
        assertEquals( engineSrc.isGameOver(), restoredEngine.isGameOver() );

        checkTileEquivalent( engineSrc.getCurrentTile(), restoredEngine.getCurrentTile()  );

        for (int pos=0; pos< GameEngine.NB_DRAWN_TILES; pos++) {
            checkTileEquivalent( engineSrc.getTile(pos), restoredEngine.getTile(pos) );
        }

        Tile[] tbHistorySrc = engineSrc.getTilesHistory();
        Tile[] tbRestoredHistory = restoredEngine.getTilesHistory();
        assertEquals( tbHistorySrc.length, tbRestoredHistory.length );
        for (int pos=0; pos<tbHistorySrc.length; pos++) {
            checkTileEquivalent( tbHistorySrc[pos], tbRestoredHistory[pos] );
        }

        assertEquals( state, restoredEngine.toString() );
    }

    public void testLoadSaveState() throws Exception {
        GameEngine engineSrc = GameEngine.ofString(null);
        checkEngineStateRestore( engineSrc );

        engineSrc.draw();
        checkEngineStateRestore( engineSrc );

        engineSrc.placeCurrentTile(0);
        checkEngineStateRestore( engineSrc );

        engineSrc.draw();
        checkEngineStateRestore( engineSrc );

        engineSrc.placeCurrentTile(2);
        checkEngineStateRestore( engineSrc );

        engineSrc.draw();
        engineSrc.placeCurrentTile(1);
        int pos = 3;
        while (engineSrc.draw() != null) {
            engineSrc.placeCurrentTile(pos);
            pos++;
        }
        assertTrue( engineSrc.isGameOver() );
        checkEngineStateRestore( engineSrc );
    }

}
