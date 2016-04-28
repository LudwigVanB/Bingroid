package sca.bingroid.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class GameEngineTest {

    private static BoardState createState(Integer... tilesValues ) {
        assertEquals( 20, tilesValues.length );
        BoardState state = new BoardState();
        int pos = 0;
        for ( Integer value : tilesValues ) {
            Tile tile;
            if (value == null) tile = new Tile();
            else tile = new Tile(value);
            state.placeTile( pos, tile );
            pos++;
        }
        return state;
    }

    private void checkScore( int expectedScore, Integer... tilesValues ) {
        BoardState state = createState(tilesValues);
        GameEngine gameEngine = GameEngine.ofString(null);
        gameEngine.setState( state );
        assertEquals( expectedScore, gameEngine.getScore());
    }

    @Test
    public void testScoreComputation() throws Exception {
        checkScore( 300,
                1, 2, 3, 4, 5,
                10, 10, 11, 11, 12,
                15, 15, 16, 16, 17,
                20, 21, 22, 23, 24
        );

        checkScore( 0,
                30, 29, 28, 27, 26,
                19, 18, 17, 16, 15,
                14, 13, 12, 11, 10,
                5, 4, 3, 2, 1
        );

        checkScore( 86,
                25, 1, 3, 5, 7,
                9, 11, 11, 12, 15,
                16, 17, 20, 22, 23,
                25, 30, null, 8, 15 );

        checkScore( 86,
                25, null, 3, 5, 7,
                9, 11, 11, 12, 15,
                16, 17, 20, 22, 23,
                25, 28, 30, 8, 15);

        checkScore( 300,
                1, 2, 3, 4, 5,
                null, null, null, null, 12,
                null, null, null, 16, 17,
                20, 21, 22, 23, 24 );

        checkScore( 300,
                null, null, 3, 4, 5,
                10, 10, 11, 11, 12,
                15, 15, 16, 16, 17,
                20, 21, 22, 23, 24
        );

        checkScore( 300,
                1, 2, 3, 4, 5,
                10, 10, 11, 11, 12,
                15, 15, 16, 16, 17,
                20, 21, 22, null, null
        );

        checkScore( 300,
                null, null, null, null, null,
                null, null, 3, null, null,
                null, null, null, null, null,
                null, null, null, null, null);

        checkScore( 300,
                null, null, null, null, null,
                null, null, null, null, null,
                null, null, null, null, null,
                null, null, null, null, null);
    }
}
