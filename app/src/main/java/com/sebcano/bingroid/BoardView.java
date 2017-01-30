package com.sebcano.bingroid;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.sebcano.bingroid.model.GameEngine;
import com.sebcano.bingroid.model.Tile;

public class BoardView extends GridLayout implements IBoardView {



    private IBoardView.Callbacks mCallbacks;

    private final static int NB_SQUARES = GameEngine.NB_DRAWN_TILES;
    private Button mTbSquares[];
    private View mTitleZone;
    private Button mDrawBtn;
    private View mScoreZone;
    private TextView mCurrentTileText;
    private TextView mTilesHistoryText;
    private TextView mRoundScoreText;
    private TextView mTotalScoreText;

    public BoardView(Context context) {
        super(context);
        initViews(context);
    }

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public BoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    @Override
    public void setCallbacks( Callbacks callbacks ) {
        mCallbacks = callbacks;
    }

    private void initViews(final Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        mTbSquares = new Button[NB_SQUARES];
        for (int i=0; i<NB_SQUARES; i++) {
            Button btn = (Button) inflater.inflate( R.layout.board_space, null );
            btn.setId(i);
            btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int btnId= v.getId();
                    mCallbacks.onSquareClicked(btnId);
                }
            });
            addView(btn);
            mTbSquares[i] = btn;
        }

        mTitleZone = inflater.inflate( R.layout.title_zone, null );
        mDrawBtn = (Button) mTitleZone.findViewById( R.id.drawButton );
        mDrawBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.onDrawClicked();
            }
        });

        mScoreZone = inflater.inflate( R.layout.score_zone, null );
        mCurrentTileText = (TextView) mScoreZone.findViewById(R.id.currentTileText);
        mTilesHistoryText = (TextView) mScoreZone.findViewById(R.id.tilesHistoryText);
        mRoundScoreText = (TextView) mScoreZone.findViewById(R.id.scoreText);
        mTotalScoreText = (TextView) mScoreZone.findViewById(R.id.totalScoreText);
        Button menuBtn = (Button) mScoreZone.findViewById( R.id.menuButton );
        menuBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getContext(), v);
                popup.inflate(R.menu.menu);
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.new_round:
                                mCallbacks.onNewRoundClicked();
                                return true;
                            case R.id.new_game:
                                mCallbacks.onResetClicked();
                                return true;
                            case R.id.rules:
                                mCallbacks.onRulesClicked();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
            }
        });
    }

    private void positionSquare(Button btn, int iRow, int iCol, int spaceWidth, int spaceHeight, float textSizePx ) {
        GridLayout.LayoutParams param = new GridLayout.LayoutParams();
        param.width = spaceWidth;
        param.height = spaceHeight;
        param.rowSpec = GridLayout.spec(iRow,GridLayout.FILL);
        param.columnSpec = GridLayout.spec(iCol,GridLayout.FILL);
        btn.setLayoutParams( param );
        btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizePx );
        addView(btn);
    }

    private void positionZone(View view, int iRow, int iCol, int rowSpan, int colSpan ) {
        GridLayout.LayoutParams param = new GridLayout.LayoutParams();
        param.rowSpec = GridLayout.spec(iRow, rowSpan, GridLayout.FILL);
        param.columnSpec = GridLayout.spec(iCol, colSpan, GridLayout.FILL);
        view.setLayoutParams( param );
        addView(view);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        removeAllViews();

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        double aspectRatio = ((double)width)/height;

        int rowCount = (int) Math.round( (NB_SQUARES+1)/(1+aspectRatio) );
        int colCount = NB_SQUARES+1 - rowCount;
        setRowCount(rowCount);
        setColumnCount(colCount);
        int spaceWidth = width/colCount;
        int spaceHeight = height/rowCount;
        float textSizePx = Math.max(spaceHeight, spaceWidth/2 ) * 0.80f;

        int upperRowColCount = (int)Math.ceil((colCount+1)/2.0f);
        int iBtn = 0;
        for (int iCol=0; iCol<upperRowColCount; iCol++) {
            Button btn = mTbSquares[iBtn++];
            positionSquare(btn, 0, iCol, spaceWidth, spaceHeight, textSizePx);
        }
        for (int iRow=1; iRow<rowCount; iRow++) {
            Button btn = mTbSquares[iBtn++];
            positionSquare(btn, iRow, upperRowColCount-1, spaceWidth, spaceHeight, textSizePx);
        }
        for (int iCol=upperRowColCount; iCol<colCount; iCol++) {
            Button btn = mTbSquares[iBtn++];
            positionSquare(btn, rowCount-1, iCol, spaceWidth, spaceHeight, textSizePx);
        }

        int rowSpan = rowCount-1;

        // Title zone
        int colSpan = colCount-upperRowColCount;
        positionZone( mTitleZone, 0, upperRowColCount, rowSpan, colSpan );

        // Score zone
        mCurrentTileText.setTextSize( TypedValue.COMPLEX_UNIT_DIP, textSizePx*1.20f );
        colSpan = upperRowColCount - 1;
        positionZone( mScoreZone, 1, 0, rowSpan, colSpan );

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onNewTileDrawn( Tile tile ) {
        String tileString = tile.toString();
        mCurrentTileText.setText( tileString );
        String tilesHistory = (mTilesHistoryText.getText() + " " + tileString).trim();
        mTilesHistoryText.setText( tilesHistory );
        mDrawBtn.setEnabled(false);
    }

    @Override
    public void onTilePlaced( int squareId, Tile tile ) {
        Button square = mTbSquares[squareId];
        if (tile != null) {
            square.setText(tile.toString());
            square.setEnabled(false);
        } else {
            square.setText("");
            square.setEnabled(true);
        }
        mDrawBtn.setEnabled(true);
    }

    @Override
    public void displayScore(Integer roundScore, Integer[] tbRoundScores, int gameScore) {
        if (roundScore != null) {
            mRoundScoreText.setText( Integer.toString(roundScore) );
            mDrawBtn.setEnabled(false);
        }

        if (tbRoundScores.length == 1) {
            mTotalScoreText.setText( Integer.toString(gameScore) );
        } else if (tbRoundScores.length > 1) {
            StringBuilder sbTotalScore = new StringBuilder();
            sbTotalScore.append( tbRoundScores[0] );
            for (int i=1; i<tbRoundScores.length; i++) {
                sbTotalScore.append( " + " );
                sbTotalScore.append( tbRoundScores[i] );
            }
            sbTotalScore.append( " = " );
            sbTotalScore.append( gameScore );
            mTotalScoreText.setText( sbTotalScore );
        }
    }

    @Override
    public void onNewRound() {
        mDrawBtn.setEnabled(true);
        mRoundScoreText.setText("-");
        mTilesHistoryText.setText("");
        mCurrentTileText.setText("");
        for (Button btn : mTbSquares) {
            btn.setText("");
            btn.setEnabled(true);
        }
    }

    @Override
    public void onReset() {
        onNewRound();
        mTotalScoreText.setText( "-" );
    }

    @Override
    public void restoreState(Tile currentTile, Tile[] tbBoard, Tile[] tbHistory, boolean canDraw, Integer roundScore, Integer[] tbRoundScores, Integer totalScore) {
        onReset();
        for (int pos=0; pos<tbBoard.length; pos++) {
            onTilePlaced( pos, tbBoard[pos] );
        }
        if (currentTile != null) mCurrentTileText.setText( currentTile.toString() );
        String tilesHistory = TextUtils.join(" ", tbHistory);
        mTilesHistoryText.setText( tilesHistory );
        displayScore( roundScore, tbRoundScores, totalScore );
        mDrawBtn.setEnabled(canDraw);
    }
}
