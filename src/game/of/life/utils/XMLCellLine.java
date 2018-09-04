package game.of.life.utils;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="line")
public class XMLCellLine {
	
	private boolean[] cells;

	public XMLCellLine(final boolean[] cells) {
		this.cells=cells;
	}
	public XMLCellLine() {
	}
	public boolean[] getCells() {
		return cells;
	}
	public void setCells(final boolean[] cells) {
		this.cells = cells;
	}
	public int size() {
		return cells.length;
	}
	
}
