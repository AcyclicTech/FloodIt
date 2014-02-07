package com.kimigi.floodit;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FloodItMainActivity extends Activity {

	private Random random = new Random(System.currentTimeMillis());
	private static final int SMALL = 10;
	private static final int MEDIUM = 15;
	private static final int LARGE = 20;
	private static final String DATA_FILE = "floodit.dat";
	private int size = SMALL;
	
	private int count = 0;
	private int highscore = 0;
	private Map<String, Integer> scoreboard = new HashMap<String, Integer>();
	private int currentColor;
	private GetColor colors;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
			setContentView(R.layout.activity_flood_it_main);
		}else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
			setContentView(R.layout.activity_flood_it_main_horizontal);
		}

		
    	colors = GetColor.newInstance();

    	scoreboard.put("S", 999);
		scoreboard.put("M", 999);
		scoreboard.put("L", 999);

		loadScores();
		Button b = (Button) findViewById(R.id.rButton);
		b.setBackgroundColor(colors.getColors().get(0));
		b.setEnabled(false);

		b = (Button) findViewById(R.id.oButton);
		b.setBackgroundColor(colors.getColors().get(1));
		b.setEnabled(false);

		b = (Button) findViewById(R.id.yButton);
		b.setBackgroundColor(colors.getColors().get(2));
		b.setEnabled(false);

		b = (Button) findViewById(R.id.gButton);
		b.setBackgroundColor(colors.getColors().get(3));
		b.setEnabled(false);

		b = (Button) findViewById(R.id.bButton);
		b.setBackgroundColor(colors.getColors().get(4));
		b.setEnabled(false);

		b = (Button) findViewById(R.id.cButton);
		b.setBackgroundColor(colors.getColors().get(5));
		b.setEnabled(false);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if(newConfig.orientation == newConfig.ORIENTATION_PORTRAIT){
			setContentView(R.layout.activity_flood_it_main);
		}else if(newConfig.orientation == newConfig.ORIENTATION_LANDSCAPE){
			setContentView(R.layout.activity_flood_it_main_horizontal);
		}
		CanvasView canvas = (CanvasView) findViewById(R.id.canvas);
		canvas.update();
//		super.onConfigurationChanged(newConfig);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.flood_it_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
        case R.id.high_scores:
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	StringBuffer message = new StringBuffer();
        	message.append("Small:\t").append(scoreboard.get("S")).append("\n");
        	message.append("Medium:\t").append(scoreboard.get("M")).append("\n");
        	message.append("Large:\t").append(scoreboard.get("L")).append("\n");
        	builder.setMessage(message.toString())
        	       .setTitle(R.string.high_scores);
        	builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
        	builder.setNegativeButton(R.string.reset, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                	scoreboard.put("S", 999);
            		scoreboard.put("M", 999);
            		scoreboard.put("L", 999);
            		saveScores();
                }
            });
        	AlertDialog dialog = builder.create();
        	dialog.show();
        	return true;
        case R.id.contribute:
        	builder = new AlertDialog.Builder(this);
        	builder.setMessage(R.string.contribute_message)
        	       .setTitle(R.string.contribute);
        	builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                	dialog.dismiss();
                }
            });
        	builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });

        	dialog = builder.create();
        	dialog.show();
        	return true;
        case R.id.about:
        	builder = new AlertDialog.Builder(this);
        	ImageView iv = new ImageView(this);
        	iv.setImageResource(R.drawable.ic_launcher);
        	builder.setView(iv);
        	builder.setTitle(R.string.about);
        	builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                	startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com")));
                }
            });

        	dialog = builder.create();
        	dialog.show();
        	return true;
        default:
    		return super.onOptionsItemSelected(item);
		}
	}
	
	public void colorClick(View view){
		Button color = (Button)view;
		color.setSelected(true);
		String tag = (String) color.getTag();
		int selectedColor = GetColor.newInstance().getColor(tag);
		if(selectedColor != currentColor){
			currentColor = selectedColor;
			doMove();
		}
	}

	public void resizeGridClick(View view){
		Button sButton = (Button) findViewById(R.id.sButton);
		Button mButton = (Button) findViewById(R.id.mButton);
		Button lButton = (Button) findViewById(R.id.lButton);
		if(view.equals(sButton)){size = SMALL; sButton.setSelected(true);}
		if(view.equals(mButton)){size = MEDIUM;mButton.setSelected(true);}
		if(view.equals(lButton)){size = LARGE;lButton.setSelected(true);}
		resetButtonClick(view);
	}

	public void resetButtonClick(View view){
		Button b = (Button) findViewById(R.id.rButton);
		b.setEnabled(true);

		b = (Button) findViewById(R.id.oButton);
		b.setEnabled(true);

		b = (Button) findViewById(R.id.yButton);
		b.setEnabled(true);

		b = (Button) findViewById(R.id.gButton);
		b.setEnabled(true);

		b = (Button) findViewById(R.id.bButton);
		b.setEnabled(true);

		b = (Button) findViewById(R.id.cButton);
		b.setEnabled(true);

		if(size == SMALL){
			highscore = scoreboard.get("S");
		}else if(size == MEDIUM){
			highscore = scoreboard.get("M");
		}else if(size == LARGE){
			highscore = scoreboard.get("L");
		}
		TextView countField = (TextView) findViewById(R.id.score);
		countField.setText("0/" + highscore);
		
		CanvasView canvas = (CanvasView) findViewById(R.id.canvas);
		if(canvas.checkSuccess()){
			saveScores();
		}
		reset();
		currentColor = canvas.getRootColor();
		count = 0;
	}
	
	private void doMove(){
		CanvasView canvas = (CanvasView) findViewById(R.id.canvas);
		if(!canvas.checkSuccess()){
			TextView countField = (TextView) findViewById(R.id.score);
			countField.setText(++count + "/" + highscore);
			//ColorDrawable newColorD = (ColorDrawable) currentColor.getBackground();
			boolean success = iterate(currentColor);
			if(success){
				int tmpScore = 0;
				if(size == SMALL){
					tmpScore = scoreboard.get("S");
					if(count < tmpScore){
						scoreboard.put("S", count);
					}
				}else if(size == MEDIUM){
					tmpScore = scoreboard.get("M");
					if(count < tmpScore){
						scoreboard.put("M", count);
					}
				}else if(size == LARGE){
					tmpScore = scoreboard.get("L");
					if(count < tmpScore){
						scoreboard.put("L", count);
					}
				}
				
			}
		}
	}
	public void reset(){
		CanvasView canvas = (CanvasView) findViewById(R.id.canvas);
		canvas.init(size);
		canvas.update();
	}

	public boolean iterate(int color){
		CanvasView canvas = (CanvasView) findViewById(R.id.canvas);
		return canvas.iterate(color);
	}
	
	private void loadScores() {
		try {
			FileInputStream scoreFileStream = openFileInput(DATA_FILE);	
			ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(scoreFileStream));
			scoreboard = (Map<String, Integer>) ois.readObject();
			ois.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	private void saveScores(){
		try {
			FileOutputStream scoreFile = openFileOutput(DATA_FILE, Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(scoreFile));
			oos.writeObject(scoreboard);
			oos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

