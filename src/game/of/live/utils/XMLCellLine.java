package game.of.live.utils;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="line")
public class XMLCellLine {//TODO
	
	private boolean[] cells;

	public XMLCellLine(boolean[] cells) {
		this.cells=cells;
	}
	public XMLCellLine() {
		// TODO Auto-generated constructor stub
	}
	public boolean[] getCells() {
		return cells;
	}

	public void setCells(boolean[] cells) {
		this.cells = cells;
	}
	public int size() {
		return cells.length;
	}
	
}
