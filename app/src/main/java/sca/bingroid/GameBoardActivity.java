package sca.bingroid;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import sca.bingroid.model.GameEngine;
import sca.bingroid.model.Tile;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GameBoardActivity extends AppCompatActivity {

    private final String LOG_TAG = GameBoardActivity.class.getSimpleName();

    private IBoardView mBoardView;
    private GameEngine mGameEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_board);

        mBoardView = (BoardView) findViewById(R.id.board_view);
        mBoardView.setCallbacks( new BoardViewCallbacks() );

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( this );
        String gameState = prefs.getString("gameState", null);
        mGameEngine = GameEngine.ofString(gameState);

        Tile[] tbBoard = new Tile[GameEngine.NB_DRAWN_TILES];
        for ( int pos=0; pos<tbBoard.length; pos++) {
            tbBoard[pos] = mGameEngine.getTile(pos);
        }
        Integer score = null;
        if (mGameEngine.isGameOver()) score = mGameEngine.getScore();
        mBoardView.restoreState( mGameEngine.getCurrentTile(), tbBoard, mGameEngine.getTilesHistory(), mGameEngine.canDraw(), score );
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( this );
        SharedPreferences.Editor editor = prefs.edit();

        String gameState = mGameEngine.toString();
        editor.putString( "gameState", gameState );

        editor.commit();
    }

    private class BoardViewCallbacks implements BoardView.Callbacks {
        @Override
        public void onSquareClicked(int squareId) {
            GameEngine.PlacementResult placementResult = mGameEngine.placeCurrentTile(squareId);
            if ( placementResult.isValid() ) {
                mBoardView.onTilePlaced(squareId, mGameEngine.getCurrentTile() );
                int previousPos = placementResult.getPreviousPos();
                if (previousPos != -1) {
                    mBoardView.onTilePlaced(previousPos, null);
                }
                if (mGameEngine.isGameOver()) {
                    int score = mGameEngine.getScore();
                    mBoardView.displayScore( score );
                }
            }
        }

        @Override
        public void onDrawClicked() {
            Tile tile = mGameEngine.draw();
            if (tile != null) {
                mBoardView.onNewTileDrawn( tile );
            }
        }

        @Override
        public void onResetClicked() {
            new AlertDialog.Builder(GameBoardActivity.this)
                    .setMessage(R.string.confirm_reset)
                    .setPositiveButton(R.string.reset, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mGameEngine = GameEngine.ofString(null);
                            mBoardView.onReset();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();
        }
    }

}
