package chip8;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class OpcodeTest {
	
	Chip8 chip8 = new Chip8();
	int[] gpr;
	int index;

	@Before
	public void setUp() throws Exception {
		Chip8 chip8 = new Chip8();
		gpr = chip8.getGpr();
		index = chip8.getIndex();
	}

	@Test
	public void testReturnFromSubroutine() {
		chip8.pushStack(100);
		chip8.pushStack(200);
		Opcode.returnFromSubroutine(0x00EE, chip8);
		assertTrue(chip8.getPC() == 200);
		Opcode.returnFromSubroutine(0x00EE, chip8);
		assertTrue(chip8.getPC() == 100);
	}
	
	@Test
	public void testJumpToImmediate() {
		Opcode.jumpToImmediate(0x1555, chip8);
		assertTrue(chip8.getPC() == 0x555);
		Opcode.jumpToImmediate(0x1777, chip8);
		assertTrue(chip8.getPC() == 0x777);
	}
	
	@Test
	public void testCallSubroutine() {
		Opcode.callSubroutine(0x2555, chip8);
		assertTrue(chip8.getPC() == 0x555);
		Opcode.callSubroutine(0x2777, chip8);
		assertTrue(chip8.getPC() == 0x777);
		assertTrue(chip8.popStack() == 0x555);
		assertTrue(chip8.popStack() == 0x200);
	}
	
	@Test
	public void testSkipIfEqualsImmediate() {
		chip8.setGpr(5, 0x55);
		Opcode.skipIfEqualsImmediate(0x3554, chip8);
		assertTrue(chip8.getPC() == 0x200);
		Opcode.skipIfEqualsImmediate(0x3555, chip8);
		assertTrue(chip8.getPC() == 0x202);
	}
	
	@Test
	public void testSkipIfNotEqualsImmediate() {
		chip8.setGpr(5,  0x55);
		Opcode.skipIfNotEqualsImmediate(0x4555, chip8);
		assertTrue(chip8.getPC() == 0x200);
		Opcode.skipIfNotEqualsImmediate(0x4554, chip8);
		assertTrue(chip8.getPC() == 0x202);
	}
	
	@Test
	public void testSkipIfEqualsRegister() {
		chip8.setGpr(4, 0x100);
		chip8.setGpr(5, 0x200);
		chip8.setGpr(6, 0x100);
		
		Opcode.skipIfEqualsRegister(0x5450, chip8);
		assertTrue(chip8.getPC() == 0x200);
		Opcode.skipIfEqualsRegister(0x5460, chip8);
		assertTrue(chip8.getPC() == 0x202);
	}
	
	@Test
	public void testSetRegisterToImmediate() {
		chip8.setGpr(0, 0x200);
		assertTrue(chip8.getGpr()[0] == 0x200);
		Opcode.setRegisterToImmediate(0x6030, chip8);
		assertTrue(chip8.getGpr()[0] == 0x30);
	}
	
	@Test
	public void testAddImmediateToRegister() {
		chip8.setGpr(0xF, 0x100);
		Opcode.addImmediateToRegister(0x7F15, chip8);
		assertTrue(chip8.getGpr()[0xF] == 0x16);
		Opcode.addImmediateToRegister(0x7F15, chip8);
		assertTrue(chip8.getGpr()[0xF] == 0x2B);
	}
	
	@Test
	public void testSetRegisterToRegister() {
		chip8.setGpr(0x0, 0x100);
		chip8.setGpr(0x1, 0x200);
		chip8.setGpr(0x2, 0x300);
		Opcode.setRegisterToRegister(0x8200, chip8);
		System.out.println(chip8.getGpr()[2]);
		assertTrue(chip8.getGpr()[2] == 0x100);
		Opcode.setRegisterToRegister(0x8210, chip8);
		assertTrue(chip8.getGpr()[2] == 0x200);
		Opcode.setRegisterToRegister(0x8020, chip8);
		assertTrue(chip8.getGpr()[0] == 0x200);
	}
	
	@Test
	public void testAddRegisterToRegister() {
		//8XY4. Adds VY to VX. VF is set to 1 when there is a carry, otherwise 0.
		
	}
	
	@Test
	public void testSubtractRegisterXFromRegisterY() {
		//8XY5. VY is subtracted from VX. VF is set to 0 when there's a borrow, otherwise 1.
	}
	

}
