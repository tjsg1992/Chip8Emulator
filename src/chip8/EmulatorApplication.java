package chip8;
import java.io.File;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class EmulatorApplication extends Application {
	
	private static final int SCREEN_WIDTH = 800;
	private static final int SCREEN_HEIGHT = 450;
	private Chip8 chip8 = new Chip8();
	private Timeline gameLoop;
	
	private EventHandler<KeyEvent> keyEventHandler = new EventHandler<KeyEvent>() {
    	@Override
    	public void handle(KeyEvent e) {
    		chip8.handleKeyEvent(e);
    	}
    };
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
    public void start(Stage primaryStage) {
		primaryStage.setTitle("Chip-8 Emulator");
		
		MenuBar menuBar = new MenuBar();
		Menu menuFile = new Menu("File");
		MenuItem loadRomItem = new MenuItem("Load ROM");
		loadRomItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				FileChooser fc = new FileChooser();
				fc.setTitle("Open ROM");
				File selectedFile = fc.showOpenDialog(primaryStage);
				if (selectedFile != null) {
					chip8.loadROM(selectedFile);
					gameLoop.play();
				}
			}
		});
		
		menuFile.getItems().add(loadRomItem);
		menuBar.getMenus().add(menuFile);
		
		VBox root = new VBox();
		root.getChildren().add(menuBar);
		root.getChildren().add(chip8.getScreen());
		
        Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT);
        scene.setOnKeyPressed(keyEventHandler);
        scene.setOnKeyReleased(keyEventHandler);
        
        primaryStage.setScene(scene);
        
        gameLoop = new Timeline();
        gameLoop.setCycleCount(Timeline.INDEFINITE);
        // Construct the keyframe telling the application what to happen inside the game loop.
        KeyFrame kf = new KeyFrame(
        		//Duration.seconds(1),
                Duration.seconds(.0025),
                actionEvent -> {
                	chip8.cycle();
                });

        gameLoop.getKeyFrames().add(kf);
        primaryStage.show();
    }
}
