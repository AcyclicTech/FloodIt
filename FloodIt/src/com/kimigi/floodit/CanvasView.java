package com.kimigi.floodit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

public class CanvasView extends View {

	private List<List<FloodRectangle>> matrix = null;
	private int size;
	private Paint defaultPaint;
	private Paint textPaint;
	private Rect defaultRect;
	private GetColor colors;
	private boolean success = false;
	private boolean running = false;
	private Set<Point> pointsToCheck = new HashSet<Point>();
	private Point[] pointArray = new Point[]{new Point(-1, 0), new Point(0,-1), new Point(1,0), new Point(0,1)};

	
	public CanvasView(Context context) {
		super(context);
		init();
	}

    public CanvasView(Context context, AttributeSet attrs) {
    	super(context, attrs);
		init();
    }
    
    private void init(){
    	success = false;
    	colors = GetColor.newInstance();
		defaultPaint = new Paint();
		defaultPaint.setColor(Color.BLACK);
		textPaint = new Paint();
		textPaint.setColor(Color.WHITE);
		textPaint.setLinearText(true);
		textPaint.setTextSize(40);
		defaultRect = new Rect(0,  0,  getWidth(),  getHeight());
    }

	public void init(int gridSize){
		success = false;
    	running = true;
		size = gridSize;
		int width = getWidth();
		int cellSize = width / size;
		matrix = new ArrayList<List<FloodRectangle>>();
		for(int i = 0; i < size; i++){
			List<FloodRectangle> colorRow = new ArrayList<FloodRectangle>();
			for(int j = 0; j < size; j++){
				FloodRectangle rect = new FloodRectangle(colors.getColor(), j * cellSize, i * cellSize, cellSize, cellSize);
				colorRow.add(rect);
			}
			matrix.add(colorRow);
		}
		pointsToCheck.clear();
		pointsToCheck.add(new Point(0,0));
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		canvas.drawRect(defaultRect, defaultPaint);
		if(running){
			for(int i = 0; i < size; i++){
				for(int j = 0; j < size; j++){
					FloodRectangle rect = matrix.get(i).get(j);
					rect.draw(canvas);
				}
			}
		}else{
			canvas.drawText("Click a size to begin", getWidth() / 3, getHeight() / 2, textPaint);
		}
	}

	public void update(){
		invalidate();
//		graphics = (Graphics2D) getGraphics();
//		graphics.clearRect(0, 0, getWidth(), getHeight());
//		for(int i = 0; i < size; i++){
//			for(int j = 0; j < size; j++){
//				FloodRectangle rect = matrix.get(i).get(j);
//				graphics.setColor(rect.getColor());
//				graphics.fill(rect);
//				graphics.setColor(Color.WHITE);
//				graphics.fill(stroke.createStrokedShape(rect));
//			}
//		}
	}
	
	public boolean iterate(int nextColor){
		boolean success = false;
		for(int i = 0; i < size; i++){
			for(int j = 0; j < size; j++){
				FloodRectangle rect = matrix.get(i).get(j);
				rect.setChecked(false);
			}
		}
		FloodRectangle rect = matrix.get(0).get(0);
		int rootColor = rect.getColor();
		
		//iterate(0, 0, rect.getColor(), nextColor);
		Set<Point> currentPoints = new HashSet<Point>(pointsToCheck);
		iterate(currentPoints, rootColor, nextColor);
		update();
		success = checkSuccess(nextColor);
		return success;
	}
	
	private void iterate(Set<Point> points, int rootColor, int nextColor){
		for(Point p: points){
			FloodRectangle rect = matrix.get(p.x).get(p.y);
			int currentColor = rect.getColor();
			if(!rect.isChecked()){
				rect.setChecked(true);
//				if(rect.getColor() == rootColor){
					rect.setColor(nextColor);
//					if(!pointsToCheck.contains(rect)){
//						pointsToCheck.add(rect);
//					}
//				}
				checkAdjacent(p, currentColor, nextColor);
			}
		}
	}
	
	private void checkAdjacent(Point p, int rootColor, int nextColor) {
		for(Point nextP: pointArray){
			int x = p.x + nextP.x;
			int y = p.y + nextP.y;
			Point currentPoint = new Point(x, y);
			if(x >= 0 && x < matrix.size() && y >= 0 && y < matrix.size()){
				FloodRectangle rect = matrix.get(x).get(y);
			//	if(!rect.isChecked()){
			//		rect.setChecked(true);
					int currentColor = rect.getColor();
					if(rect.getColor() == rootColor){
						rect.setColor(nextColor);
						if(!pointsToCheck.contains(currentPoint)){
							pointsToCheck.add(currentPoint);
							checkAdjacent(currentPoint, currentColor, nextColor);
						}
					}
					if(rect.getColor() == nextColor){
						if(!pointsToCheck.contains(currentPoint)){
							pointsToCheck.add(currentPoint);
							checkAdjacent(currentPoint, currentColor, nextColor);
						}
					}
				//}
			}			
		}
		
	}

	private void iterate(int x, int y, int rootColor, int nextColor){
		if(x >= 0 && x < matrix.size() && y >= 0 && y < matrix.size()){
			FloodRectangle rect = matrix.get(x).get(y);
			if(!rect.isChecked()){
				rect.setChecked(true);
				if(rect.getColor() == rootColor){
					rect.setColor(nextColor);
					for(int i = -1; i <= 1; i++){//[-1,0 0,-1 1,0 0,1]
						for(int j = -1; j <= 1; j++){
							if(Math.abs(i + j) == 1){
								iterate(x + i, y + j, rootColor, nextColor);
							}
						}
					}
				}
			}
		}
	}
		
	private boolean checkSuccess(int currentColor){
		for(int i = 0; i < size; i++){
			for(int j = 0; j < size; j++){
				FloodRectangle rect = matrix.get(i).get(j);
				if(rect.getColor() != currentColor){
					return false;
				}
			}
		}
		Toast.makeText(getContext(), "You Win", 10).show();
//		graphics.drawString("You Win", getWidth() / 2, getHeight() / 2);
    	success = true;
		return true;
	}
	
	public boolean checkSuccess(){
		return success;
	}
	
	public int getRootColor(){
		return matrix.get(0).get(0).getColor();
	}
}
