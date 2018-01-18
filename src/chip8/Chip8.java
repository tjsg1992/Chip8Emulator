package chip8;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javafx.scene.input.KeyEvent;


public class Chip8 {
	
	private static final int SCREEN_WIDTH = 64;
	private static final int SCREEN_HEIGHT = 32;
	private Memory memory = new Memory();
	private int[] gpr = new int[16];
	private int index = 0;
	private int pc = Memory.PROGRAM_START;
	private Keypad keypad = new Keypad();
	
	private Screen screen = new Screen();
	
	private int delayTimer = 0;
	private int soundTimer = 0;
	
	private boolean renderFlag;
	private boolean incrementPCFlag;
	
	private int[] stack = new int[16];
	private int stackPointer = 0;
	
	private boolean instructionHalted = false;
	
	private static final int[] FONT_SET = {
		0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
        0x20, 0x60, 0x20, 0x20, 0x70, // 1
        0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
        0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
        0x90, 0x90, 0xF0, 0x10, 0x10, // 4
        0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
        0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
        0xF0, 0x10, 0x20, 0x40, 0x50, // 7
        0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
        0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
        0xF0, 0x90, 0xF0, 0x90, 0x90, // A
        0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
        0xF0, 0x80, 0x80, 0x80, 0xF0, // C
        0xE0, 0x90, 0x90, 0x90, 0xE0, // D
        0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
        0xF0, 0x80, 0xF0, 0x80, 0x80  // F
};

	
	public Chip8() {
		
		//Clear stack.
		for (int i = 0; i < this.stack.length; i++) {
			this.stack[i] = 0;
		}
		
		//Clear general purpose registers.
		for (int i = 0; i < this.gpr.length; i++) {
			this.gpr[i] = 0;
		}
		
		//Load memory with font set.
		this.memory.loadFontSet(FONT_SET);
		
		this.renderFlag = false;
		this.incrementPCFlag = true;
	}
	
	public void loadROM(File rom) {
		try {
			FileInputStream fis = new FileInputStream(rom);
			BufferedInputStream bis = new BufferedInputStream(fis);
			DataInputStream dis = new DataInputStream(bis);
			
			byte[] romStream = new byte[(int) rom.length()];
			dis.read(romStream);
			this.memory.loadProgram(romStream);
			
			dis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void cycle() {
		System.out.println("Cycle");
		System.out.println("System State");
		for (int i = 0; i < gpr.length; i++) {
			System.out.println("Register " + Integer.toHexString(i) + ": " + Integer.toHexString(gpr[i]));
		}
		System.out.println("Index : " + Integer.toHexString(index));
		int keyPressed = keypad.getPressedKey();
		//if (keyPressed >= 0) System.out.println("Key Pressed: " + keyPressed);
		
		int opcode = this.memory.fetchOpcode(this.pc);
		decodeOpcode(opcode);
		
		if (this.renderFlag) {
			this.screen.render();
			this.renderFlag = false;
		}
		if (this.delayTimer > 0) {
			this.delayTimer--;
		}
		if (this.soundTimer > 0) {
			this.soundTimer--;
			if (this.soundTimer == 0) {
				System.out.println("Beep");
			}
		}
		
		if (this.incrementPCFlag) {
			incrementPC();
		}
		this.incrementPCFlag = true;
	}
	
	/*
	 * Instruction Set.
	 * Determines which opcode should be and calls that opcode, a static method in the Opcode class.
	 */
	private void decodeOpcode(int opcode) {
		System.out.println(pc);
		System.out.println(Integer.toHexString(opcode));
		//0-codes
		switch (opcode) {		
		case 0x00E0:
			//Clears the screen.
			Opcode.clearScreen(opcode, this);
			return;
		case 0x00EE:
			//Returns from a subroutine.
			Opcode.returnFromSubroutine(opcode, this);
			return;
		}
		
		//1-codes through 7-codes
		switch (opcode & 0xF000) {
		case 0x1000:
			//1NNN. Jumps to address NNN.
			Opcode.jumpToImmediate(opcode, this);
			return;
		case 0x2000:
			//2NNN. Calls subroutine at NNN.
			Opcode.callSubroutine(opcode, this);
			return;
		case 0x3000:
			//3XNN. Skips the next instruction if VX = NN.
			Opcode.skipIfEqualsImmediate(opcode, this);
			return;
		case 0x4000:
			//4XNN. Skips the next instruction if VX != NN.
			Opcode.skipIfNotEqualsImmediate(opcode, this);
			return;
		case 0x5000:
			//5XY0. Skips the next instruction if VX = VY.
			Opcode.skipIfEqualsRegister(opcode, this);
			return;
		case 0x6000:
			//6XNN. Sets VX to NN.
			Opcode.setRegisterToImmediate(opcode, this);
			return;
		case 0x7000:
			//7XNN. Adds NN to VX.
			Opcode.addImmediateToRegister(opcode, this);
		}
		
		//8-codes and 9-codes.
		switch (opcode & 0xF00F) {
		case 0x8000:
			//8XY0. Sets VX to the value of VY.
			Opcode.setRegisterToRegister(opcode, this);
			return;
		case 0x8001:
			//8XY1. Sets VX to the value of VX|VY.
			Opcode.setRegisterToRegisterOr(opcode, this);
			return;
		case 0x8002:
			//8XY2. Sets VX to the value of VX&VY.
			Opcode.setRegisterToRegisterAnd(opcode, this);
			return;
		case 0x8003:
			//8XY3. Sets VX to the value of VX^VY.
			Opcode.setRegisterToRegisterXor(opcode, this);
			return;
		case 0x8004:
			//8XY4. Adds VY to VX. VF is set to 1 when there is a carry, otherwise 0.
			Opcode.addRegisterToRegister(opcode, this);
			return;
		case 0x8005:
			//8XY5. VY is subtracted from VX. VF is set to 0 when there's a borrow, otherwise 1.
			Opcode.subtractRegisterXFromRegisterY(opcode, this);
			return;
		case 0x8006:
			//8XY6. Shifts VX right by one. VF is set to the value of the LSB before the shift.
			Opcode.shiftRegisterRight(opcode, this);
			return;
		case 0x8007:
			//8XY7. Sets VX to VY minus VX. VF is set to 0 when there's a borrow, otherwise 1.
			Opcode.subtractRegisterYFromRegisterX(opcode, this);
			return;
		case 0x800E:
			//8XYE. Shifts VX left by one. VF is set to the value of the MSB before the shift.
			Opcode.shiftRegisterLeft(opcode, this);
			return;
		case 0x9000:
			//9XY0. Skips the next instruction if VX != VY.
			Opcode.skipIfNotEqualsRegister(opcode, this);
			return;
		}
		
		//A-codes through D-codes.
		switch (opcode & 0xF000) {
		case 0xA000:
			//ANNN. Sets I to the address NNN.
			Opcode.setIndexToImmediate(opcode, this);
			return;
		case 0xB000:
			//BNNN. Jumps to the address NNN plus V0.
			Opcode.jumpToImmediatePlusRegister0(opcode, this);
			return;
		case 0xC000:
			//CXNN. Sets VX to the result of a rand(255)&NN.
			Opcode.setRegisterToRandom(opcode, this);
			return;
		case 0xD000:
			//DXYN. Draws a sprite at coordinate (VX, VY) that has a width of 8 pixels 
			//and a height of N pixels.
			Opcode.drawSprite(opcode, this);
			return;
		}
		
		//E-codes and F-codes.
		switch (opcode & 0xF0FF) {
		case 0xE09E:
			//EX9E. Skips the next instruction if the key stored in VX is pressed.
			Opcode.skipIfKeyPressed(opcode, this);
			return;
		case 0xE0A1:
			//EXA1. Skips the next instruction if the key stored in VX isn't pressed.
			Opcode.skipIfKeyNotPressed(opcode, this);
			return;
		case 0xF007:
			//FX07. Sets VX to the value of the delay timer.
			Opcode.setRegisterToDelay(opcode, this);
			return;
		case 0xF00A:
			//FX0A. A key press is awaited, and then stored in VX. Blocking operation.
			Opcode.awaitKeyPress(opcode, this);
			return;
		case 0xF015:
			//FX15. Sets the delay timer to VX.
			Opcode.setDelayToRegister(opcode, this);
			return;
		case 0xF018:
			//FX18. Sets the sound timer to VX.
			Opcode.setSoundToRegister(opcode, this);
			return;
		case 0xF01E:
			//FX1E. Adds VX to I.
			Opcode.addRegisterToIndex(opcode, this);
			return;
		case 0xF029:
			//FX29. Sets I to the location of the sprite for the character in VX.
			Opcode.setIndexToSpriteLocation(opcode, this);
			return;
		case 0xF033:
			//FX33. Stores the binary-coded decimal representation of VX.
			Opcode.storeBinaryRepresentation(opcode, this);
			return;
		case 0xF055:
			//FX55. Stores V0 to VX in memory starting at address I.
			Opcode.loadRegistersToMemory(opcode, this);
			return;
		case 0xF065:
			//FX65. Fills V0 to VX with values from memory starting at address I.
			Opcode.loadRegistersFromMemory(opcode, this);
			return;
		}
	}
	
	public void setPC(int i) {
		this.pc = i;
	}
	
	public int getPC() {
		return this.pc;
	}
	
	public void incrementPC() {
		this.pc += 2;
		if (pc >= Memory.PROGRAM_END) {
			this.pc = Memory.PROGRAM_START;
		}
	}
	
	public void setDelayTimer(int i) {
		this.delayTimer = i;
	}
	
	public int getDelayTimer() {
		return this.delayTimer;
	}
	
	public void setIndex(int i) {
		this.index = i;
	}
	
	public int getIndex() {
		return this.index;
	}
	
	public void setSoundTimer(int i) {
		this.soundTimer = i;
	}
	
	public int[] getGpr() {
		return this.gpr;
	}
	
	public int getMemoryValue(int location) {
		return this.memory.getValue(location);
	}
	
	public void setMemoryValue(int value, int location) {
		this.memory.setValue(value, location);
	}
	
	public int popStack() {
		return this.stack[stackPointer--];
	}
	
	public void pushStack(int a) {
		this.stack[++stackPointer] = a;
	}
	
	public void drawSprite(int x, int y, int h) {
		this.gpr[0xF] = 0;
		for (int yline = 0; yline < h; yline++) {
			int pixel = this.memory.getValue(this.index + yline);
			for (int xline = 0; xline < 8; xline++) {
				if ((pixel & (0x80 >> xline)) != 0) {
					int drawX = x + xline;
					int drawY = y + yline;
					
					if (drawX < 64 && drawY < 32) {
						if (this.screen.getPixel(drawX, drawY) == 1) {
							this.gpr[0xF] = 1;
							System.out.println("Collision: " + drawX + ", " + drawY);
						}
						
						this.screen.setPixel(drawX, drawY);
					}
				}
			}
		}
	}
	
	public void clearScreen() {
		this.screen.clear();
	}
	
	public Screen getScreen() {
		return this.screen;
	}
	
	public int getPressedKey() {
		return this.keypad.getPressedKey();
	}
	
	public boolean isKeyPressed(int k) {
		return this.keypad.isKeyPressed(k);
	}
	
	public void setInstructionHalted(boolean isHalted) {
		this.instructionHalted = isHalted;
	}

	public void handleKeyEvent(KeyEvent e) {
		if (e.getEventType() == KeyEvent.KEY_PRESSED) {
			keypad.pressKey(e.getCode());
		} else if (e.getEventType() == KeyEvent.KEY_RELEASED) {
			keypad.releaseKey(e.getCode());
		}
	}
	
	public void setRenderFlag(boolean b) {
		this.renderFlag = b;
	}
	
	public void setIncrementPCFlag(boolean b) {
		this.incrementPCFlag = b;
	}

	/*
	 * Test Only
	 */
	public void setGpr(int index, int value) {
		this.gpr[index] = value;
	}
}