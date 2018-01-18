package chip8;
import java.util.Random;

public final class Opcode {
	
	private static Random r = new Random();
	
	private Opcode(Chip8 chip8) {
		//Do nothing.
	}
	
	//Clears the screen.
	public static void clearScreen(int opcode, Chip8 chip8) {
		chip8.clearScreen();
		chip8.setRenderFlag(true);
	}
	
	//Returns from a subroutine.
	public static void returnFromSubroutine(int opcode, Chip8 chip8) {
		chip8.setPC(chip8.popStack());
	}
	
	//1NNN. Jumps to address NNN.
	public static void jumpToImmediate(int opcode, Chip8 chip8) {
		System.out.println("Jumping to address " + getImmediate(opcode, 3));
		chip8.setPC(getImmediate(opcode, 3));
		chip8.setIncrementPCFlag(false);
	}
	
	//2NNN. Calls subroutine at NNN.
	public static void callSubroutine(int opcode, Chip8 chip8) {
		chip8.pushStack(chip8.getPC());
		chip8.setPC(getImmediate(opcode, 3));
		chip8.setIncrementPCFlag(false);
	}
	
	//3XNN. Skips the next instruction if VX = NN.
	public static void skipIfEqualsImmediate(int opcode, Chip8 chip8) {
		if (getImmediate(opcode, 2) == getRegisterValueX(opcode, chip8)) {
			chip8.incrementPC();
		}
	}
	
	//4XNN. Skips the next instruction if VX != NN.
	public static void skipIfNotEqualsImmediate(int opcode, Chip8 chip8) {
		if (getImmediate(opcode, 2) != getRegisterValueX(opcode, chip8)) {
			chip8.incrementPC();
		}
	}
	
	//5XY0. Skips the next instruction if VX = VY.
	public static void skipIfEqualsRegister(int opcode, Chip8 chip8) {
		if (getRegisterValueX(opcode, chip8) == getRegisterValueY(opcode, chip8)) {
			chip8.incrementPC();
		}
	}
	
	//6XNN. Sets VX to NN.
	public static void setRegisterToImmediate(int opcode, Chip8 chip8) {
		System.out.println("Set Register To Immediate");
		int r = getRegisterX(opcode);
		chip8.getGpr()[r] = getImmediate(opcode, 2);
	}
	
	//7XNN. Adds NN to VX.
	public static void addImmediateToRegister(int opcode, Chip8 chip8) {
		int sum = getImmediate(opcode, 2) + getRegisterValueX(opcode, chip8);
		
		if (sum > 0xFF) {
			sum &= 0xFF;
		}
		
		setRegisterX(opcode, chip8, sum);
	}
	
	//8XY0. Sets VX to the value of VY.
	public static void setRegisterToRegister(int opcode, Chip8 chip8) {
		int v = getRegisterValueY(opcode, chip8);
		setRegisterX(opcode, chip8, v);
	}
	
	//8XY1. Sets VX to the value of VX|VY.
	public static void setRegisterToRegisterOr(int opcode, Chip8 chip8) {
		int v = getRegisterValueX(opcode, chip8) | getRegisterValueY(opcode, chip8);
		setRegisterX(opcode, chip8, v);
	}
	
	//8XY2. Sets VX to the value of VX&VY.
	public static void setRegisterToRegisterAnd(int opcode, Chip8 chip8) {
		int v = getRegisterValueX(opcode, chip8) & getRegisterValueY(opcode, chip8);
		setRegisterX(opcode, chip8, v);
	}
	
	//8XY3. Sets VX to the value of VX^VY.
	public static void setRegisterToRegisterXor(int opcode, Chip8 chip8) {
		int v = getRegisterValueX(opcode, chip8) ^ getRegisterValueY(opcode, chip8);
		setRegisterX(opcode, chip8, v);
	}	
	
	//8XY4. Adds VY to VX. VF is set to 1 when there is a carry, otherwise 0.
	public static void addRegisterToRegister(int opcode, Chip8 chip8) {
		int sum = getRegisterValueX(opcode, chip8) + getRegisterValueY(opcode, chip8);
		
		if (sum > 0xFF) {
			sum &= 0xFF;
			setRegisterF(chip8, 1);
		} else {
			setRegisterF(chip8, 0);
		}
		
		setRegisterX(opcode, chip8, sum);
	}
	
	//8XY5. VY is subtracted from VX. VF is set to 0 when there's a borrow, otherwise 1.
	public static void subtractRegisterXFromRegisterY(int opcode, Chip8 chip8) {
		int difference = getRegisterValueX(opcode, chip8) - getRegisterValueY(opcode, chip8);
		
		if (difference < 0) {
			difference &= 0xFF;
			setRegisterF(chip8, 0);
		} else {
			setRegisterF(chip8, 1);
		}
		
		setRegisterX(opcode, chip8, difference);
	}
	
	//8XY6. Shifts VX right by one. VF is set to the value of the LSB before the shift.
	public static void shiftRegisterRight(int opcode, Chip8 chip8) {
		int x = getRegisterValueX(opcode, chip8);
		setRegisterF(chip8, x & 0x1);
		setRegisterX(opcode, chip8, x >> 1);
	}
	
	//8XY7. Sets VX to VY minus VX. VF is set to 0 when there's a borrow, otherwise 1.
	public static void subtractRegisterYFromRegisterX(int opcode, Chip8 chip8) {
		int difference = getRegisterValueY(opcode, chip8) - getRegisterValueX(opcode, chip8);
		
		if (difference < 0) {
			difference &= 0xFF;
			setRegisterF(chip8, 0);
		} else {
			setRegisterF(chip8, 1);
		}
		
		setRegisterX(opcode, chip8, difference);
	}
	
	//8XYE. Shifts VX left by one. VF is set to the value of the MSB before the shift.
	public static void shiftRegisterLeft(int opcode, Chip8 chip8) {
		int x = getRegisterValueX(opcode, chip8);
		setRegisterF(chip8, (x & 0xFFFF) >> 15);
		setRegisterX(opcode, chip8, x << 1);
	}
	
	//9XY0. Skips the next instruction if VX != VY.
	public static void skipIfNotEqualsRegister(int opcode, Chip8 chip8) {
		if (getRegisterValueX(opcode, chip8) != getRegisterValueY(opcode, chip8)) {
			chip8.incrementPC();
		}
	}
	
	//ANNN. Sets I to the address NNN.
	public static void setIndexToImmediate(int opcode, Chip8 chip8) {
		chip8.setIndex(getImmediate(opcode, 3));
	}
	
	//BNNN. Jumps to the address NNN plus V0.
	public static void jumpToImmediatePlusRegister0(int opcode, Chip8 chip8) {
		chip8.setPC(getImmediate(opcode, 3) + chip8.getGpr()[0]);
		chip8.setIncrementPCFlag(false);
	}
	
	//CXNN. Sets VX to the result of a rand(255)&NN.
	public static void setRegisterToRandom(int opcode, Chip8 chip8) {
		int randomValue = r.nextInt(256) & getImmediate(opcode, 3);
		setRegisterX(opcode, chip8, randomValue);
	}
	
	//DXYN. Draws a sprite at coordinate (VX, VY) that has a width of 8 pixels 
	//and a height of N pixels.
	public static void drawSprite(int opcode, Chip8 chip8) {
		int x = getRegisterValueX(opcode, chip8);
		int y = getRegisterValueY(opcode, chip8);
		int h = getImmediate(opcode, 1);
		System.out.println("Draw: " + x + ", " + y + ", " + h);
		chip8.drawSprite(x, y, h);
		chip8.setRenderFlag(true);
	}
	
	//EX9E. Skips the next instruction if the key stored in VX is pressed.
	public static void skipIfKeyPressed(int opcode, Chip8 chip8) {
		int keyNum = getRegisterValueX(opcode, chip8);
		if (chip8.isKeyPressed(keyNum)) {
			chip8.incrementPC();
		}
	}
	
	//EXA1. Skips the next instruction if the key stored in VX isn't pressed.
	public static void skipIfKeyNotPressed(int opcode, Chip8 chip8) {
		int keyNum = getRegisterValueX(opcode, chip8);
		if (!chip8.isKeyPressed(keyNum)) {
			chip8.incrementPC();
		}
	}
	
	//FX07. Sets VX to the value of the delay timer.
	public static void setRegisterToDelay(int opcode, Chip8 chip8) {
		System.out.println("Delay Timer: " + chip8.getDelayTimer());
		setRegisterX(opcode, chip8, chip8.getDelayTimer());
	}
	
	//FX0A. A key press is awaited, and then stored in VX. Blocking operation.
	public static void awaitKeyPress(int opcode, Chip8 chip8) {
		int key = chip8.getPressedKey();
		if (key >= 0) {
			setRegisterX(opcode, chip8, key);
			chip8.setInstructionHalted(false);
		} else {
			chip8.setInstructionHalted(true);
		}
	}
	
	//FX15. Sets the delay timer to VX.
	public static void setDelayToRegister(int opcode, Chip8 chip8) {
		chip8.setDelayTimer(getRegisterValueX(opcode, chip8));
	}
	
	//FX18. Sets the sound timer to VX.
	public static void setSoundToRegister(int opcode, Chip8 chip8) {
		chip8.setSoundTimer(getRegisterValueX(opcode, chip8));
	}
	
	//FX29. Sets I to the location of the sprite for the character in VX.
	public static void setIndexToSpriteLocation(int opcode, Chip8 chip8) {
		int spriteLocation = getRegisterValueX(opcode, chip8) * 5;
		chip8.setIndex(spriteLocation);
	}
	
	//FX33. Stores the binary-coded decimal representation of VX, with the most significant 
	//of three digits at the address in I, the middle digit at I plus 1, and the least 
	//significant digit at I plus 2.
	public static void storeBinaryRepresentation(int opcode, Chip8 chip8) {
		int x = getRegisterValueX(opcode, chip8);
		int i = chip8.getIndex();
		
		chip8.setMemoryValue(x / 100, i);
		chip8.setMemoryValue((x / 10) % 10, i + 1);
		chip8.setMemoryValue((x % 100) % 10, i + 2);
	}
	
	//FX55. Stores V0 to VX in memory starting at address I.
	public static void loadRegistersToMemory(int opcode, Chip8 chip8) {
		int i = chip8.getIndex();
		for (int r = 0; r <= (opcode & 0x0F00); r++) {
			chip8.setMemoryValue(chip8.getGpr()[r], i++);
		}
	}
	
	//FX65. Fills V0 to VX with values from memory starting at address I.
	public static void loadRegistersFromMemory(int opcode, Chip8 chip8) {
		int i = chip8.getIndex();
		int x = getRegisterX(opcode);
		for (int r = 0; r <= x; r++) {
			chip8.getGpr()[r] = chip8.getMemoryValue(i++);
		}
	}
	
	//FX1E. Adds VX to I.
	public static void addRegisterToIndex(int opcode, Chip8 chip8) {
		int sum = getRegisterValueX(opcode, chip8) + chip8.getIndex();
		
		if (sum > 0xFFF) {
			sum -= 0xFFF;
			setRegisterF(chip8, 1);
		} else {
			setRegisterF(chip8, 0);
		}
		
		chip8.setIndex(sum);
	}
	
	
	//For opcode ZNNN, return the last 1-3 values determined by length.
	private static int getImmediate(int opcode, int length) {
		if (length == 1) return (opcode & 0x000F);
		else if (length == 2) return (opcode & 0x00FF);
		else return (opcode & 0x0FFF);
	}
	
	//For opcode ZXYZ, return the value of X.
	private static int getRegisterX(int opcode) {
		return (opcode & 0x0F00) >>> 8;
	}
	
	//For opcode ZXYZ, return the value of Y.
	private static int getRegisterY(int opcode) {
		return (opcode & 0x00F0) >>> 4;
	}
	
	//For opcode ZXYZ, return the value of VX.
	private static int getRegisterValueX(int opcode, Chip8 chip8) {
		return chip8.getGpr()[getRegisterX(opcode)];
	}
	
	//For opcode ZXYZ, return the value of VY.
	private static int getRegisterValueY(int opcode, Chip8 chip8) {
		return chip8.getGpr()[getRegisterY(opcode)];
	}
	
	private static void setRegisterX(int opcode, Chip8 chip8, int value) {
		chip8.getGpr()[getRegisterX(opcode)] = value;
	}
	
	private static void setRegisterY(int opcode, Chip8 chip8, int value) {
		chip8.getGpr()[getRegisterY(opcode)] = value;
	}
	
	private static void setRegisterF(Chip8 chip8, int value) {
		chip8.getGpr()[0xF] = value;
	}
	
}