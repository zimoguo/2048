package com.sdufe.game;

import com.sdufe.game.view.MyLayout;
import com.sdufe.game.view.MyLayout.onMyLayoutListener;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.Menu;
import android.widget.TextView;

/**
 * @author lili.guo
 *
 * 2014-10-24
 */
public class MainActivity extends Activity implements onMyLayoutListener{
	
	private MyLayout myLayout;
	private TextView tv_score;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		myLayout=(MyLayout) findViewById(R.id.id_game2048);
		tv_score=(TextView) findViewById(R.id.id_score);
		
		myLayout.setonMyLayoutListener(this);
	}

	@Override
	public void onScoreChange(int score) {
		tv_score.setText("Score"+score);
	}

	@Override
	public void onGameOver() {
		new AlertDialog.Builder(this).setTitle("GAME OVER")  
        .setMessage("YOU HAVE GOT " + tv_score.getText())  
        .setPositiveButton("RESTART", new OnClickListener()  
        {  
            @Override  
            public void onClick(DialogInterface dialog, int which)  
            {  
                myLayout.restart();  
            }  
        }).setNegativeButton("EXIT", new OnClickListener()  
        {  

            @Override  
            public void onClick(DialogInterface dialog, int which)  
            {  
                finish();  
            }  
        }).show(); 
	}

}
