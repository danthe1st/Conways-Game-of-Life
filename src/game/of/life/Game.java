package game.of.life;

import java.io.File;

import game.of.life.utils.XMLController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
/**
 * Main-Class for the Game of Life
 * @author Daniel Schmid
 */
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

	public static void main(final String[] args) {
		launch(args);
	}
	
	@Override
	public void start(final Stage primaryStage) throws Exception {
		stage=primaryStage;
		//get Screen with highest Resolution
		for (final Screen localscreen : Screen.getScreens()) {
			final Rectangle2D screenBounds=screen.getVisualBounds();
			final Rectangle2D localBounds=localscreen.getVisualBounds();
			if (localBounds.getWidth()*localBounds.getHeight()>screenBounds.getWidth()+screenBounds.getHeight()) {
				screen=localscreen;
			}
		}
		//load best/max number of Boxes in x/y coordinate
		final Rectangle2D primaryScreenBounds = screen.getVisualBounds();
		numBoxesX=(int) (primaryScreenBounds.getHeight()/boxSize)-2;
		numBoxesY=(int) (primaryScreenBounds.getWidth()/boxSize)-2;
		//initialize simulation
		sim=new Simulation(numBoxesX,numBoxesY);
		rects=sim.getPixels();
		moveToScreen(screen);
		
		primaryStage.setResizable(false);
		primaryStage.setScene(getSimulationScene());
		primaryStage.initStyle(StageStyle.UTILITY);
		primaryStage.setOnCloseRequest(e->Platform.exit());
		primaryStage.setTitle("Conways Game of Life");
		primaryStage.show();
		
		//initialize controller
		loadControllerStage();
		controller.show();
		
		//start simulation
		gameThread=new Thread(sim);
		gameThread.start();
	}
	/**
	 * fits pixelsize
	 * @param screen the {@link Screen} where the pixels should be fit
	 */
	private void rescale(final Screen screen) {
		final boolean show=stage.isShowing();
		if (show) {
			stage.hide();
		}
		
		final Rectangle2D screenBounds = screen.getVisualBounds();
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
	/**
	 * loads the Pixels to the local Variable {@link Game#rects} and moves the Screen to the right position
	 */
	public void reloadRects() {
		rects=sim.getPixels();
		moveToScreen(screen);
	}
	/**
	 * loads the Controller
	 */
	private void loadControllerStage() {
		if (controller==null) {
			controller=new Stage();
		}
		
		controller.setOnCloseRequest(e->Platform.exit());
		controller.setResizable(false);
		controller.setScene(getControllerScene());
		controller.initStyle(StageStyle.UTILITY);
		controller.setTitle("Controller - Conways Game of Life");
		controller.setAlwaysOnTop(true);
	}
	/**
	 * moves the simulation Stage to a {@link Screen}
	 * @param screen the {@link Screen} where the {@link Stage} should be moved
	 */
	private void moveToScreen(final Screen screen) {
		rescale(screen);
		final Rectangle2D primaryScreenBounds = screen.getVisualBounds();
		stage.setX(primaryScreenBounds.getMinX()-boxSize);
		stage.setY(primaryScreenBounds.getMinY()-boxSize);
	}
	/**
	 * loads the Controller Scene with all {@link Button}s
	 * @return the Controller Scene
	 */
	private Scene getControllerScene() {
		final int numScreens=Screen.getScreens().size();
		final Button[] screenButton=new Button[numScreens];
		
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
		final File statesDir=new File("./states");
		if (!statesDir.exists()) {
			statesDir.mkdirs();
		}
		final TextInputControl stateNameArea=new TextField("state");
		//init FileChoser for Save As/Load From
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(statesDir);
        final FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);
		final HBox[] lines=new HBox[] {
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
				stateNameArea,
				getButton("Save State", e->XMLController.saveState(sim, new File("./states/"+stateNameArea.getText()+".xml"))),
				getButton("Load State", e->{
					XMLController.loadState(new File("./states/"+stateNameArea.getText()+".xml"),sim);
					reloadRects();
				})
			),
			new HBox(
				getButton("save As", e->{
			        final File file = fileChooser.showSaveDialog(controller);
			        if (file!=null) {
			        	XMLController.saveState(sim,file);
					}
				}),
				getButton("load From", e->{
			        final File file = fileChooser.showOpenDialog(controller);
			        if (file!=null) {
			        	XMLController.loadState(file,sim);
				        reloadRects();
					}
				})
			)
		};
		final VBox root=new VBox(lines);
		final Scene scene=new Scene(root);
		return scene;
	}
	/**
	 * loads a {@link Button} with a name and an ClickEvent
	 * @param name the name of the {@link Button}
	 * @param onClick the ClickEvent
	 * @return the Button loaded
	 */
	private Button getButton(final String name,final EventHandler<? super MouseEvent> onClick) {
		final Button button=new Button(name);
		button.setOnMouseClicked(onClick);
		return button;
	}
	/**
	 * loads the Simulation Scene
	 * @return the Simulation Scene
	 */
	private Scene getSimulationScene() {
		final HBox[] lines=new HBox[rects.length];
		for(int x=0;x<rects.length;x++) {
			try {
				lines[x]=new HBox(rects[x]);
			} catch (final NullPointerException e) {
				
			}
		}
		final VBox root=new VBox(lines);
		final Scene scene=new Scene(root);
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
	/**
	 * pauses/wakes the simulation thread up
	 */
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
	/**
	 * when the Game stopps--> stop the whole Application
	 */
	@Override
	public void stop() throws Exception {
		System.exit(0);
	}
}
