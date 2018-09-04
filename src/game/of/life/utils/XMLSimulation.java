package game.of.life.utils;


import javax.xml.bind.annotation.XmlRootElement;

import game.of.life.Simulation;

@XmlRootElement(name="simulation")
public class XMLSimulation {
	private XMLCellLine[] lines;
	public XMLSimulation(final Simulation data) {
		final boolean[][] cells=new boolean[data.getPixels().length][data.getPixels()[0].length];
		lines=new XMLCellLine[data.getPixels().length];
		for (int x = 0; x < cells.length; x++) {
			for (int y = 0; y < cells[0].length; y++) {
				cells[x][y]=data.isAlive(x, y);
			}
			lines[x]=new XMLCellLine(cells[x]);
		}
	}
	public XMLSimulation() {
		
	}
	public void setLines(final XMLCellLine[] lines) {
		this.lines=lines;
	}
	public XMLCellLine[] getLines() {
		return lines;
	}
	public void loadSimulation(final Simulation sim) {
		final boolean[][] cells=new boolean[lines.length][lines[0].size()];
		for (int i = 0; i < lines.length; i++) {
			cells[i]=lines[i].getCells();
		}
		sim.setCells(cells);
		
	}
}
