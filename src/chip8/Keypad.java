package chip8;
import javafx.scene.input.KeyCode;


public class Keypad {
	private boolean[] keys = new boolean[16];
	
	public Keypad() {
		for (int i = 0; i < keys.length; i++) {
			keys[i] = false;
		}
	}
	
	public int getPressedKey() {
		for (int i = 0; i < this.keys.length; i++) {
			if (this.keys[i]) return i;
		}
		return -1;
	}
	
	public boolean isKeyPressed(int k) {
		return this.keys[k];
	}

	public void pressKey(KeyCode keyCode) {
		int i = getKeyIndex(keyCode);
		if (i >= 0) keys[i] = true;
	}

	public void releaseKey(KeyCode keyCode) {
		int i = getKeyIndex(keyCode);
		if (i >= 0) keys[i] = false;
	}
	
	private int getKeyIndex(KeyCode keyCode) {
		switch (keyCode) {
		case DIGIT1:
			return 0;
		case DIGIT2:
			return 1;
		case DIGIT3:
			return 2;
		case DIGIT4:
			return 3;
		case Q:
			return 4;
		case W:
			return 5;
		case E:
			return 6;
		case R:
			return 7;
		case A:
			return 8;
		case S:
			return 9;
		case D:
			return 10;
		case F:
			return 11;
		case Z:
			return 12;
		case X:
			return 13;
		case C:
			return 14;
		case V:
			return 15;
		default:
			return -1;
		}
	}
}
