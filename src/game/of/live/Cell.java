package game.of.live;


import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
public class Cell {
	private boolean alive;
	private boolean nextFrame;
	private Rectangle pixel;
	private static final Color COLOR_ALIVE=Color.GREEN;
	private static final Color COLOR_DEAD=Color.BLACK;
	public static boolean autoAdd;
	public Cell() {
		
	}
	public Cell(boolean alive,Rectangle pixel) {
		this.alive=alive;
		nextFrame=alive;
		this.pixel=pixel;
		
		nextFrame();
		pixel.setStrokeWidth(0);
		pixel.setOnMousePressed(e->{
			onMousePressed(e);
		});
//		pixel.setOnMouseEntered(e->onMousePressed(e));
		pixel.setOnMouseMoved(e->onMouseMoved(e));
		
		//pixel.setOnDragOver()
		//pixel.setOnMouseEntered(e->onMouseEvent(e));
		//pixel.setOnMouseDragged(e->onMouseEvent(e));
	}
	private void onMouseMoved(MouseEvent e) {
		if (e.isShiftDown()) {
			this.nextFrame=true;
		}
		else if (e.isAltDown()) {
			this.nextFrame=false;
		}
		nextFrame();
	}
	private void onMousePressed(MouseEvent e) {
//		System.out.println("enter");
		
		if (e.getButton()==MouseButton.NONE) {
//			System.out.println("no-mouse");
			return;
		}
		//System.out.println(e.getButton());
		if (e.getButton()==MouseButton.PRIMARY) {
//			System.out.println("mouse 1");
			this.nextFrame=true;
		}
		else if (e.getButton()==MouseButton.SECONDARY) {
			this.nextFrame=false;
			
		}
		nextFrame();
	}
	public boolean isAlive() {
		return alive;
	}
	public void setAlive(boolean alive) {
		this.nextFrame = alive;
	}
	public void nextFrame(){
		alive=nextFrame;
		if (alive) {
			//pixel.setStroke(COLOR_ALIVE);
			pixel.setFill(COLOR_ALIVE);
		}
		else {
			pixel.setFill(COLOR_DEAD);
			
		}
	}
	public Rectangle getPixel() {
		return pixel;
	}
	@Override
	public String toString() {
		return "alive: "+alive+"next Frame: "+nextFrame;
	}
}
