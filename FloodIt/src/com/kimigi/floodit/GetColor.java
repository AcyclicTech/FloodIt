package com.kimigi.floodit;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.graphics.Color;

public class GetColor {

	private static Random random = new Random(System.currentTimeMillis());
	private static List<Integer> colors;
	private static Map<String, Integer> colorMap;
	private static GetColor instance = new GetColor();;

	public static GetColor newInstance(){
		return instance;
	}
	
	private GetColor(){
		colors = Arrays.asList(new Integer[]{Color.RED, Color.rgb(255, 165, 0), Color.YELLOW, 
				Color.rgb(0, 200, 0), Color.BLUE, Color.CYAN});
		colorMap = new HashMap<String, Integer>();
		colorMap.put("RED", Color.RED);
		colorMap.put("ORANGE", Color.rgb(255, 165, 0)); 
		colorMap.put("YELLOW", Color.YELLOW); 
		colorMap.put("GREEN", Color.rgb(0, 200, 0)); 
		colorMap.put("BLUE", Color.BLUE); 
		colorMap.put("CYAN", Color.CYAN);
	}
	
	public List<Integer> getColors(){
		return colors;
	}
	
	public int getColor(){
		int c = colors.get(random.nextInt(colors.size()));
		return c;
	}

	public int getColor(String name){
		return colorMap.get(name);
	}
}
