package chip8;

public class Memory {
	
	private static final int MEMORY_SIZE = 4096;
	
	public static final int FONT_SET_START = 0x000;
	public static final int FONT_SET_END = 0x0A0;
	public static final int PROGRAM_START = 0x200;
	public static final int PROGRAM_END = 0xFFF;
	
	private int[] memory;
	
	public Memory() {
		this.memory = new int[MEMORY_SIZE];
	}

	public void loadFontSet(int[] fontSet) {
		for (int i = 0; i < fontSet.length; i++) {
			memory[FONT_SET_START + i] = fontSet[i];
		}
	}
	
	public void loadProgram(byte[] rom) {
		for (int i = 0; i < rom.length; i++) {
			memory[PROGRAM_START + i] = (rom[i] & 0xFF);
		}
	}
	
	public int fetchOpcode(int pc) {
		return this.memory[pc] << 8 | this.memory[pc + 1];
	}
	
	public int getValue(int location) {
		return this.memory[location];
	}
	
	public void setValue(int value, int location) {
		this.memory[location] = value;
	}
	
}
