package test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import runner.Calculator;

@RunWith(TestRunner.class)
public class CalculatorTest {
    Calculator calculator = new Calculator();
 
    @Test
    public void testAddition() {
//        System.out.println("in testAddition");
        assertEquals("addition", 8, calculator.add(5, 3));
    }
    @Test
    public void failTest() {
//        System.out.println("in fail test");
        assertEquals("addition", 8, calculator.add(2, 3));
    }
    @Test
    public void testZero() {
//        System.out.println("in zero test");
        assertEquals("addition", 0, calculator.add(0, 0));
    }
    @Test
    public void testFail2() {
//        System.out.println("in testFail2");
        assertEquals("addition", 8, calculator.add(7, 3));
    }
}
