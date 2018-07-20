package game.of.life;

import java.util.Random;


import javafx.scene.shape.Rectangle;
public class Simulation implements Runnable{
	private Cell[][] cells;
	private static Random rand=new Random();
	
	public Simulation(int numBoxesX,int numBoxesY) {
		cells=new Cell[numBoxesX][numBoxesY];
		for (int x = 0; x < cells.length; x++) {
			for (int y = 0; y < cells[x].length; y++) {
				cells[x][y]=new Cell(rand.nextBoolean(),new Rectangle(Game.boxSize, Game.boxSize));
			}
		}
	}
	public Rectangle[][] getPixels() {
		Rectangle[][] pixels=new Rectangle[cells.length][cells[0].length];
		for (int x = 0; x < cells.length; x++) {
			for (int y = 0; y < cells[x].length; y++) {
				pixels[x][y]=cells[x][y].getPixel();
			}
		}
		return pixels;
	}
	public boolean isAlive(int x,int y) {
		return cells[x][y].isAlive();
	}
	//final Object LOCK = new Object();
	@Override
	public void run() {
		
		while (true) {
			try {
				generation();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					synchronized (this) {
						try {
							this.wait();
						} catch (InterruptedException e1) {
						}
					}
				}
			} catch (Exception e) {
			}
			
		}
	}
	public void generation() {
		for (int x = 0; x < cells.length; x++) {
			for (int y = 0; y < cells[x].length; y++) {
				int aliveCount=0;
				for (int x1 = x-1; x1 <= x+1; x1++) {
					for (int y1 = y-1; y1 <= y+1; y1++) {
						if (x==x1&&y==y1) {
							continue;
						}
						int otherCellX=x1%cells.length;
						int otherCellY=y1%cells[0].length;
						if (x1<0) {
							otherCellX+=cells.length;
						}
						if (y1<0) {
							otherCellY+=cells[0].length;
						}
						if (cells[otherCellX][otherCellY].isAlive()) {
							aliveCount++;
						}
						
					}
				}
				
				if (aliveCount==2) {
				}
				else if (aliveCount==3) {
					cells[x][y].setAlive(true);
				}
				else {
					cells[x][y].setAlive(false);
				}
			}
		}
		nextFrame();
	}
	private void nextFrame() {
		for (int x = 0; x < cells.length; x++) {
			for (int y = 0; y < cells[x].length; y++) {
				cells[x][y].nextFrame();
			}
		}
	}
	public void wakeUp() {
		synchronized (this) {
			this.notify();
		}
	}
	public void clear() {
		for (int x = 0; x < cells.length; x++) {
			for (int y = 0; y < cells[x].length; y++) {
				cells[x][y].setAlive(false);
			}
		}
		nextFrame();
	}
	public void randomize() {
		for (int x = 0; x < cells.length; x++) {
			for (int y = 0; y < cells[x].length; y++) {
				cells[x][y].setAlive(rand.nextBoolean());
			}
		}
		nextFrame();
	}
	public void setCells(boolean[][] cells) {
		this.cells=new Cell[cells.length][cells[0].length];
		for (int x = 0; x < cells.length; x++) {
			for (int y = 0; y < cells[x].length; y++) {
				this.cells[x][y]=new Cell(cells[x][y], new Rectangle(Game.boxSize, Game.boxSize));
				this.cells[x][y].nextFrame();
			}
		}
		
	}
}
