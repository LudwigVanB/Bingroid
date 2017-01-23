package com.sebcano.bingroid;

import com.sebcano.bingroid.model.Tile;

interface IBoardView {

    interface Callbacks {
        void onSquareClicked(int squareId );
        void onDrawClicked();
        void onResetClicked();
    }

    void setCallbacks( Callbacks callbacks );
    void onNewTileDrawn( Tile tile );
    void onTilePlaced( int squareId, Tile tile );
    void displayScore(int score);
    void onReset();
    void restoreState( Tile currentTile, Tile[] tbBoard, Tile[] tbHistory, boolean canDraw, Integer score );
}
