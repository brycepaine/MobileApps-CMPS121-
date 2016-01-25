package com.paine.dr.tictac;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private int curPlayer = 1;
    int winFlag = 0;
    ImageButton buttArray[] = new ImageButton[9];
    boolean tieGame;
    int turnCount;

  




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttArray[0]= (ImageButton) findViewById(R.id.imageButton00);
        buttArray[1] = (ImageButton) findViewById(R.id.imageButton01);
        buttArray[2]= (ImageButton) findViewById(R.id.imageButton02);
        buttArray[3]= (ImageButton) findViewById(R.id.imageButton10);
        buttArray[4] = (ImageButton) findViewById(R.id.imageButton11);
        buttArray[5] = (ImageButton) findViewById(R.id.imageButton12);
        buttArray[6]= (ImageButton) findViewById(R.id.imageButton20);
        buttArray[7]= (ImageButton) findViewById(R.id.imageButton21);
        buttArray[8]= (ImageButton) findViewById(R.id.imageButton22);





    }

    public void newGame(View v) {
        curPlayer = 1;
        turnCount = 1;

        for(ImageButton butt : buttArray) {
            butt.setImageResource(android.R.color.transparent);
            butt.setTag(Integer.toString(butt.getId()));
            butt.setClickable(true);
        }
        TextView c = (TextView) findViewById(R.id.XwonView);
        c.setVisibility(View.INVISIBLE);
        TextView d = (TextView) findViewById(R.id.OwonView);
        d.setVisibility(View.INVISIBLE);
        TextView e = (TextView) findViewById(R.id.tieGame);
        e.setVisibility(View.INVISIBLE);

    }

    public void tieGame(){
        for (ImageButton butt: buttArray){
            butt.setClickable(false);
        }
        View b = findViewById(R.id.newGame);
        b.setVisibility(View.VISIBLE);

        TextView c = (TextView) findViewById(R.id.tieGame);
        c.setVisibility(View.VISIBLE);

    }

    public void wonGame(){
        for(ImageButton butt : buttArray) {
            butt.setClickable(false);
        }

        if(curPlayer == 1) {
            TextView c = (TextView) findViewById(R.id.XwonView);
            c.setVisibility(View.VISIBLE);
        }
        else {
            TextView c = (TextView) findViewById(R.id.OwonView);
            c.setVisibility(View.VISIBLE);
        }
        View b = findViewById(R.id.newGame);
        b.setVisibility(View.VISIBLE);
    }

    public void checkWin(){

        String Butt00 = (String)findViewById(R.id.imageButton00).getTag();
        String Butt01 = (String)findViewById(R.id.imageButton01).getTag();
        String Butt02 = (String)findViewById(R.id.imageButton02).getTag();
        String Butt10 = (String)findViewById(R.id.imageButton10).getTag();
        String Butt11 = (String)findViewById(R.id.imageButton11).getTag();
        String Butt12 = (String)findViewById(R.id.imageButton12).getTag();
        String Butt20 = (String)findViewById(R.id.imageButton20).getTag();
        String Butt21 = (String)findViewById(R.id.imageButton21).getTag();
        String Butt22 = (String)findViewById(R.id.imageButton22).getTag();

        tieGame = true;

        if(Butt00.equals(Butt01) && Butt01.equals(Butt02) ||
                Butt10.equals(Butt11) && Butt11.equals(Butt12) ||
                Butt20.equals(Butt21) && Butt21.equals(Butt22)){
            wonGame();
        }

        else if(Butt00.equals(Butt10) && Butt10.equals(Butt20) ||
                Butt01.equals(Butt11) && Butt11.equals(Butt21) ||
                Butt02.equals(Butt12) && Butt12.equals(Butt22)){
            wonGame();
        }

        else if(Butt00.equals(Butt11) && Butt11.equals(Butt22) ||
                Butt02.equals(Butt11) && Butt11.equals(Butt20)){
            wonGame();
        }
        else if (turnCount == 9) tieGame();
    }



    public void clickBoard(View v) {
        // Get id of button
        int i = v.getId();

        // More useful, let's get the tag.
        String t = (String) v.getTag();
        // If you want to put a cross on it.
        ImageButton vv = (ImageButton) v;
        if (curPlayer == 1 &&v.isClickable()){
            vv.setImageResource(R.drawable.pcross);
            v.setTag("X");
            v.setClickable(false);


        }
        else if(v.isClickable()){
            vv.setImageResource(R.drawable.pcircle);
            v.setTag("O");
            v.setClickable(false);

        }
        checkWin();
        curPlayer = curPlayer * -1;
        turnCount += 1;


    }

}

