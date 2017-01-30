package com.sebcano.bingroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.sebcano.bingroid.model.GameEngine;

public class RulesActivity extends AppCompatActivity {

    private TextView createTextView(int value) {
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL;

        TextView tv = new TextView(this);
        tv.setLayoutParams( lp );
        tv.setText(Integer.toString(value));

        return tv;
    }

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_rules );
        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        int[] tbScores = GameEngine.TB_SCORES;
        TableLayout scoreTable = (TableLayout) findViewById(R.id.table_score);
        int halfScoreLength = tbScores.length/2;
        for (int i=1; i<=halfScoreLength; i++) {
            TableRow tr = new TableRow(this);
            if (i%2==1) {
                tr.setBackgroundResource( R.drawable.even_row_shape );
            }

            TextView tvSeriesLength = createTextView( i );
            TextView tvSeriesScore = createTextView( tbScores[i-1] );
            tr.addView(tvSeriesLength);
            tr.addView(tvSeriesScore);

            View iv = getLayoutInflater().inflate( R.layout.table_divider, null );
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.MATCH_PARENT);
            iv.setLayoutParams( lp );
            tr.addView( iv );

            tvSeriesLength = createTextView( i+halfScoreLength );
            tvSeriesScore = createTextView( tbScores[i+halfScoreLength-1] );
            tr.addView(tvSeriesLength);
            tr.addView(tvSeriesScore);

            scoreTable.addView( tr );
        }
    }
}
