package com.buggame.server;

import java.util.Random;

public class MapGenerator {
	Random rd;
	
	public static void main(String[] args) {
		MapGenerator gen = new MapGenerator(010501);
		gen.printSampleMap(10, 10);
	}
	
	public MapGenerator (int seed) {
		rd = new Random();
		rd.setSeed(seed);
	}
	
	public void setSeed(int seed) {
		rd.setSeed(seed);
	}
	public int[][] generateMap(int x, int y) {
		int[][] map = new int[x][y];
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				map[i][j] = rd.nextBoolean() ? 1 : 0;
			}
		}
		return map;
	}
	
	public String getMapAsString(int x, int y) {
		String result = "";
		int[][] map = generateMap(x, y);
		int i = 0;
		while (true) {
			for (int j = 0; j < y; j++) {
				result = result + String.valueOf(map[i][j]);
			}
			i++;
			if (i == x)
				break;
			result = result + '\n';
		}
		return result;
	}
	
	public void printSampleMap(int x, int y) {
		int[][] map = generateMap(x, y);
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				System.out.print(map[i][j]);
			}
			System.out.print("\n");
		}
	}
}
