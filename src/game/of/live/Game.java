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
	public static int boxSize=10;
	private static int numBoxesX=50;//how many lines?
	private static int numBoxesY=100;//how many colums
	
	private Thread gameThread=null;
	private Simulation sim=null;
	private Rectangle[][] rects=null;
	private Stage stage=null;
	private Screen screen=Screen.getPrimary();
	private Stage controller=null;

	public static void main(String[] args) {
		launch(args);
	}
	@Override
	public void start(Stage primaryStage) throws Exception {
		stage=primaryStage;
		
		for (Screen localscreen : Screen.getScreens()) {
			Rectangle2D screenBounds=screen.getVisualBounds();
			Rectangle2D localBounds=localscreen.getVisualBounds();
			if (localBounds.getWidth()*localBounds.getHeight()>screenBounds.getWidth()+screenBounds.getHeight()) {
				screen=localscreen;
			}
		}
		
		Rectangle2D primaryScreenBounds = screen.getVisualBounds();
		
		numBoxesX=(int) (primaryScreenBounds.getHeight()/boxSize)-2;
		numBoxesY=(int) (primaryScreenBounds.getWidth()/boxSize)-2;
		
		sim=new Simulation(numBoxesX,numBoxesY);
		rects=sim.getPixels();
		moveToScreen(screen);
		
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
	private void rescale(Screen screen) {
		boolean show=stage.isShowing();
		if (show) {
			stage.hide();
		}
		
		Rectangle2D screenBounds = screen.getVisualBounds();
		if (screenBounds.getHeight()>screenBounds.getWidth()) {
			boxSize=(int) ((screenBounds.getWidth())/rects[0].length);
		}
		else {
			boxSize=(int) ((screenBounds.getHeight())/rects.length);
		}
		for (int x = 0; x < rects.length; x++) {
			for (int y = 0; y < rects[x].length; y++) {
				rects[x][y].setWidth(boxSize);
				rects[x][y].setHeight(boxSize);
			}
		}
		
		stage.setScene(getSimulationScene());//stattdessen mit scene.setRoot() ??
		stage.sizeToScene();
		if (show) {
			stage.show();
		}
		
	}
	public void reloadRects() {
		rects=sim.getPixels();
		moveToScreen(screen);
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
	private void moveToScreen(Screen screen) {
		rescale(screen);
		Rectangle2D primaryScreenBounds = screen.getVisualBounds();
		stage.setX(primaryScreenBounds.getMinX()-boxSize);
		stage.setY(primaryScreenBounds.getMinY()-boxSize);
	}
	private Scene getControllerScene() {
		final int numScreens=Screen.getScreens().size();
		Button[] screenButton=new Button[numScreens];
		
		for (int i = 0; i < screenButton.length; i++) {
			final int num=i;
			screenButton[i]=getButton("goto Screen "+(i+1), e->{
				if (numScreens!=Screen.getScreens().size()) {
					controller.setScene(getControllerScene());
					return;
				}
				screen=Screen.getScreens().get(num);
				moveToScreen(screen);
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
							reloadRects();
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
