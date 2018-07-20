package game.of.live;

import java.io.File;

import game.of.live.utils.XMLController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Game extends Application{
	public static int BOX_SIZE=10;
	public static int NUM_BOXES_X=50;//wie viele Zeilen?
	public static int NUM_BOXES_Y=100;//wie viele Spalten?
	
	private Thread gameThread=null;
	private Simulation sim=null;
	private Rectangle[][] rects=null;
	private Stage stage=null;
	
	public static void main(String[] args) {
		launch(args);
	}
	private void rescale(Screen screen) {//TODO
		boolean show=stage.isShowing();
		if (show) {
			stage.hide();
		}
		
		Rectangle2D screenBounds = screen.getVisualBounds();
		if (screenBounds.getHeight()>screenBounds.getWidth()) {
			BOX_SIZE=(int) ((screenBounds.getWidth()-1)/rects[0].length);
		}
		else {
			BOX_SIZE=(int) ((screenBounds.getHeight()-1)/rects.length);
		}
		for (int x = 0; x < rects.length; x++) {
			for (int y = 0; y < rects[x].length; y++) {
				rects[x][y].setWidth(BOX_SIZE);
				rects[x][y].setHeight(BOX_SIZE);
			}
		}
		
		stage.setScene(getSimulationScene());//stattdessen mit scene.setRoot() ??
		stage.sizeToScene();
		if (show) {
			stage.show();
		}
		
	}
	Stage controller=null;
	@Override
	public void start(Stage primaryStage) throws Exception {
		stage=primaryStage;
		Screen screen=Screen.getPrimary();
		for (Screen localscreen : Screen.getScreens()) {
			Rectangle2D screenBounds=screen.getVisualBounds();
			Rectangle2D localBounds=localscreen.getVisualBounds();
			if (localBounds.getWidth()*localBounds.getHeight()>screenBounds.getWidth()+screenBounds.getHeight()) {
				screen=localscreen;
			}
		}
		
		Rectangle2D primaryScreenBounds = screen.getVisualBounds();
		
		NUM_BOXES_X=(int) (primaryScreenBounds.getHeight()/BOX_SIZE)-2;
		NUM_BOXES_Y=(int) (primaryScreenBounds.getWidth()/BOX_SIZE)-2;
		
		
		sim=new Simulation(NUM_BOXES_X,NUM_BOXES_Y);
		rects=sim.getPixels();
		moveToScreen(screen);
		
//		primaryStage.setFullScreen(true);
		
		primaryStage.setResizable(false);
		primaryStage.setScene(getSimulationScene());
		primaryStage.initStyle(StageStyle.UTILITY);
		primaryStage.setOnCloseRequest(e->Platform.exit());
		primaryStage.setTitle("Conways Game of Live");
		//primaryStage.setAlwaysOnTop(true);
		primaryStage.show();
		
		loadControllerStage();
		controller.show();
		
		
		gameThread=new Thread(sim);
		gameThread.start();
	}
	private void loadControllerStage() {
		if (controller==null) {
			controller=new Stage();
		}
		
		controller.setOnCloseRequest(e->Platform.exit());
		controller.setResizable(false);
		controller.setScene(getControllerScene());
		controller.initStyle(StageStyle.UTILITY);
		controller.setTitle("Controller - Conways Game of Live");
		controller.setAlwaysOnTop(true);
	}
	private void moveToScreen(Screen screen) {//TODO
		rescale(screen);
		Rectangle2D primaryScreenBounds = screen.getVisualBounds();
		stage.setX(primaryScreenBounds.getMinX()-BOX_SIZE);
		stage.setY(primaryScreenBounds.getMinY()-BOX_SIZE);
//		if (primaryScreenBounds.getHeight()<primaryScreenBounds.getWidth()) {
//			BOX_SIZE=(int) (primaryScreenBounds.getWidth()/100);
//		}
//		else {
//			BOX_SIZE=(int) (primaryScreenBounds.getHeight()/100);
//		}
//		NUM_BOXES_X=(int) (primaryScreenBounds.getHeight()/BOX_SIZE);
//		NUM_BOXES_Y=(int) (primaryScreenBounds.getWidth()/BOX_SIZE);
		
	}
	private Scene getControllerScene() {
		final int numScreens=Screen.getScreens().size();
		Button[] screenButton=new Button[numScreens];
		
		for (int i = 0; i < screenButton.length; i++) {
			final int num=i;
			screenButton[i]=getButton("goto Screen "+(i+1), e->{
				if (numScreens!=Screen.getScreens().size()) {
					controller.setScene(getControllerScene());
				}
				moveToScreen(Screen.getScreens().get(num));
			});
		}
		HBox[] lines=new HBox[] {
				
				new HBox(
						getButton("clear", e->sim.clear()),
						getButton("randomize", e->sim.randomize())
						),
				new HBox(
						getButton("pause/wakeup", e->pause()),
						getButton("forward 1 generation", e->sim.generation())
						),
				new HBox(
						getButton("exit", e->Platform.exit())
						),
				new HBox(screenButton),
				new HBox(
						getButton("Save State", e->XMLController.saveState(sim, new File("./state.xml"))),
						getButton("Load State", e->{
							XMLController.loadState(new File("./state.xml"),sim);
							
						})
						)
		};
		VBox root=new VBox(lines);
		
		Scene scene=new Scene(root);
		
		return scene;
	}
	private Button getButton(String name,EventHandler<? super MouseEvent> onClick) {
		Button button=new Button(name);
		button.setOnMouseClicked(onClick);
		return button;
	}
	
	private Scene getSimulationScene() {
		
		
		HBox[] lines=new HBox[rects.length];
		
		for(int x=0;x<rects.length;x++) {
			
			
//			for(int y=0;y<rects[0].length;y++) {
//					
//			}
			try {
				lines[x]=new HBox(rects[x]);
			} catch (NullPointerException e) {
				
			}
			
		}
		
		VBox root=new VBox(lines);
		Scene scene=new Scene(root);
		
		scene.setOnKeyPressed(k->{
			if (k.getCode()==KeyCode.P||k.getCode()==KeyCode.SPACE) {
				pause();
			}
			else if (k.getCode()==KeyCode.C) {
				sim.clear();
			}
			else if (k.getCode()==KeyCode.R) {
				sim.randomize();
			}
			else if (k.getCode()==KeyCode.ENTER) {
				sim.generation();
			}
			else if (k.getCode()==KeyCode.F11||k.getCode()==KeyCode.F12) {
				stage.setFullScreen(!stage.isFullScreen());
			}
			else if (k.getCode()==KeyCode.ESCAPE) {
				if (!stage.isFullScreen()) {
					Platform.exit();
				}
			}
		});
		return scene;
	}
	private void pause() {
		synchronized (sim) {
			if (gameThread.isAlive()) {
				gameThread.interrupt();
				
			}
			else {
				sim.notify();
			}
		}
	}
	@Override
	public void stop() throws Exception {
		System.exit(0);
	}
	
}
