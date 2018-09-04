package game.of.life;

import java.util.Random;

import javafx.scene.shape.Rectangle;

/**
 * Core-Class for the Game
 * @author Daniel Schmid
 */
public class Simulation implements Runnable{
	private Cell[][] cells;
	private static Random rand=new Random();
	
	public Simulation(final int numBoxesX,final int numBoxesY) {
		cells=new Cell[numBoxesX][numBoxesY];
		for (int x = 0; x < cells.length; x++) {
			for (int y = 0; y < cells[x].length; y++) {
				cells[x][y]=new Cell(rand.nextBoolean(),new Rectangle(Game.boxSize, Game.boxSize));
			}
		}
	}
	/**
	 * gets the Pixels as an {@link Rectangle Rectangle[][]}
	 * @return the Pixels
	 */
	public Rectangle[][] getPixels() {
		final Rectangle[][] pixels=new Rectangle[cells.length][cells[0].length];
		for (int x = 0; x < cells.length; x++) {
			for (int y = 0; y < cells[x].length; y++) {
				pixels[x][y]=cells[x][y].getPixel();
			}
		}
		return pixels;
	}
	/**
	 * test if a Cell is alive
	 * @param x the x-Coordinate of the Cell
	 * @param y the y-Coordinate of the Cell
	 * @return a boolean if the Cell is alive or not
	 */
	public boolean isAlive(final int x,final int y) {
		return cells[x][y].isAlive();
	}
	/**
	 * loads the next generation<br>
	 * waits 100ms
	 * restarts 
	 */
	@Override
	public void run() {
		
		while (true) {
			try {
				generation();
				try {
					Thread.sleep(100);
				} catch (final InterruptedException e) {
					synchronized (this) {
						try {
							this.wait();
							
						} catch (final InterruptedException e1) {
						}
					}
				}
			} catch (final Exception e) {
			}
		}
	}
	/**
	 * forwards a Generation
	 */
	public synchronized void generation() {
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
	/**
	 * loads the mext Frame for all Cells
	 */
	private void nextFrame() {
		for (int x = 0; x < cells.length; x++) {
			for (int y = 0; y < cells[x].length; y++) {
				cells[x][y].nextFrame();
			}
		}
	}
	/**
	 * kills all Cells
	 */
	public void clear() {
		for (int x = 0; x < cells.length; x++) {
			for (int y = 0; y < cells[x].length; y++) {
				cells[x][y].setAlive(false);
			}
		}
		nextFrame();
	}
	/**
	 * randomize all Cells(if they are alive or not)
	 */
	public void randomize() {
		for (int x = 0; x < cells.length; x++) {
			for (int y = 0; y < cells[x].length; y++) {
				cells[x][y].setAlive(rand.nextBoolean());
			}
		}
		nextFrame();
	}
	/**
	 * sets all Cells to a specified status
	 * @param cells An boolean[][] with the statuses of the Cells<br>
	 * (<code>true</code>...alive<br>
	 * <code>false</code>...dead)
	 */
	public void setCells(final boolean[][] cells) {
		this.cells=new Cell[cells.length][cells[0].length];
		for (int x = 0; x < cells.length; x++) {
			for (int y = 0; y < cells[x].length; y++) {
				this.cells[x][y]=new Cell(cells[x][y], new Rectangle(Game.boxSize, Game.boxSize));
				this.cells[x][y].nextFrame();
			}
		}
	}
	/**
	 * how many Cells are alive and dead
	 */
	@Override
	public String toString() {
		int alive=0;
		int dead=0;
		for (int x = 0; x < cells.length; x++) {
			for (int y = 0; y < cells[x].length; y++) {
				if (cells[x][y].isAlive()) {
					alive++;
				}
				else {
					dead++;
				}
			}
		}
		return "alive Cells: "+alive+", dead Cells: "+dead;
	}
}
