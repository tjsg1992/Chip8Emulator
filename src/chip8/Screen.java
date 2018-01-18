package chip8;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Screen extends Canvas {
	
	private static final int PIXEL_WIDTH = 64;
	private static final int PIXEL_HEIGHT = 32;
	private static final int SCREEN_WIDTH = 800;
	private static final int SCREEN_HEIGHT = 400;
	private static final int SCALE = 12;
	
	private GraphicsContext gc;
	private int[][] pixels = new int[PIXEL_WIDTH][PIXEL_HEIGHT];
	
	public Screen() {
		super(SCREEN_WIDTH, SCREEN_HEIGHT);
		setFocusTraversable(true);
		
		gc = this.getGraphicsContext2D();
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, 800, 400);
		clear();
	}
	
	public void render() {
		for (int y = 0; y < PIXEL_HEIGHT; y++) {
			for (int x = 0; x < PIXEL_WIDTH; x++) {
				
				if (pixels[x][y] == 1) {
					gc.setFill(Color.WHITE);
				}
				else {
					gc.setFill(Color.BLACK);
				}
				gc.fillRect(x * SCALE, y * SCALE, SCALE, SCALE);
			}
		}
	}
	
	public void clear() {
		for (int y = 0; y < PIXEL_HEIGHT; y++) {
			for (int x = 0; x < PIXEL_WIDTH; x++) {
				pixels[x][y] = 0;
			}
		}
	}
	
	public void setPixel(int x, int y) {
		pixels[x][y] ^= 1;
	}
	
	public int getPixel(int x, int y) {
		return pixels[x][y];
	}
}
