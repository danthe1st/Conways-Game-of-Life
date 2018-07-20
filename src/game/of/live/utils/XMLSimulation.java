package game.of.live.utils;


import javax.xml.bind.annotation.XmlRootElement;

import game.of.live.Simulation;

@XmlRootElement(name="simulation")
public class XMLSimulation {//TODO
	private XMLCellLine[] lines;
	public XMLSimulation(Simulation data) {
		boolean[][] cells=new boolean[data.getPixels().length][data.getPixels()[0].length];
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
	public void setLines(XMLCellLine[] lines) {
		this.lines=lines;
	}
	public XMLCellLine[] getLines() {
		return lines;
	}
	public void loadSimulation(Simulation sim) {
		boolean[][] cells=new boolean[lines.length][lines[0].size()];
		for (int i = 0; i < lines.length; i++) {
			cells[i]=lines[i].getCells();
		}
		sim.setCells(cells);
	}
}
