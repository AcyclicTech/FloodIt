package com.kimigi.floodit;

import android.graphics.drawable.PaintDrawable;

public class FloodRectangle extends PaintDrawable {

	private boolean checked;
	int x;
	int y;

	@Override
	public String toString() {
		return "FloodRectangle [x=" + x + ", y=" + y + "]";
	}

	public FloodRectangle(int c, int x, int y, int width, int height){
		this.x = x;
		this.y = y;
		getPaint().setColor(c);
		setBounds(x, y, x + width, y + height);
		setChecked(false);
		getPaint().setStrokeWidth(1);
	}
	
	public void setChecked(boolean c){
		checked = c;
	}
	
	public boolean isChecked(){
		return checked;
	}
	
	public int getColor(){
		return getPaint().getColor();
	}
	
	public void setColor(int c){
		getPaint().setColor(c);
	}
}
