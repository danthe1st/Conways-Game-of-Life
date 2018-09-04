package game.of.life;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
/**
 * reprecents a Cell<br>
 * a Cell can be alive or dead
 * @author Daniel Schmid
 */
public class Cell {
	private boolean alive;
	private boolean nextFrame;
	private Rectangle pixel;
	private static final Color COLOR_ALIVE=Color.GREEN;
	private static final Color COLOR_DEAD=Color.BLACK;
	public Cell(final boolean alive,final Rectangle pixel) {
		this.alive=alive;
		nextFrame=alive;
		this.pixel=pixel;
		
		nextFrame();
		pixel.setStrokeWidth(0);
		pixel.setOnMousePressed(e->{
			onMousePressed(e);
		});
		pixel.setOnMouseMoved(e->onMouseMoved(e));
	}
	//shift...set cells alive
	//alt...set cells dead
	private void onMouseMoved(final MouseEvent e) {
		if (e.isShiftDown()) {
			this.nextFrame=true;
		}
		else if (e.isAltDown()) {
			this.nextFrame=false;
		}
		nextFrame();
	}
	//left-click/primary...set cell alive
	//right-click/secondary...set cell dead
	private void onMousePressed(final MouseEvent e) {
		if (e.getButton()==MouseButton.NONE) {
			return;
		}
		if (e.getButton()==MouseButton.PRIMARY) {
			this.nextFrame=true;
		}
		else if (e.getButton()==MouseButton.SECONDARY) {
			this.nextFrame=false;
		}
		nextFrame();
	}
	/**
	 * is a Cell alive in this Frame?
	 * @return true if the Cell is alive
	 */
	public boolean isAlive() {
		return alive;
	}
	/**
	 * sets a Cell alive or dead the next Frame
	 * @param alive will it be alive?
	 */
	public void setAlive(final boolean alive) {
		this.nextFrame = alive;
	}
	/**
	 * loads the next Frame
	 */
	public void nextFrame(){
		alive=nextFrame;
		if (alive) {
			pixel.setFill(COLOR_ALIVE);
		}
		else {
			pixel.setFill(COLOR_DEAD);
		}
	}
	/**
	 * 
	 * @return the {@link Rectangle} representing the current status of the Cell
	 */
	public Rectangle getPixel() {
		return pixel;
	}
	@Override
	public String toString() {
		return "alive: "+alive+"next Frame: "+nextFrame;
	}
}
