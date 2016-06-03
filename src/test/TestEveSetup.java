package test;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;


/**
 * This class can be run and used to see if eve is connected corretly.
 * 
 * @author Kim
 *
 */
public class TestEveSetup {
	public static void main(String[] args) {
		LCD.drawString("Plugin Testing", 0, 4);
		Delay.msDelay(5000);
	}

}
