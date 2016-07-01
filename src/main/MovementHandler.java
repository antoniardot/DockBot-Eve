package main;

import lejos.hardware.motor.Motor;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.Color;
import lejos.robotics.SampleProvider;
import java.util.Arrays;

public class MovementHandler {

	//Motor C is right, Motor B is left
	// 4.5 * Winkel des Motors
	// z.B. 4.5 * 1 bewirkt, dass der Motor sich in der realen Welt um 1 Grad bewegt
	//private double ratio = 4.6*(5*360)/(5*360+55)*(5*360+8)/(5*360);
	private double ratio = 4.483450134;

	private double cmconstant = 35;
	private int gridDistance = 25;
	
	private double blackValue = 0.10;
	private double whiteValue = 0.60;
	
	private int normalSpeed = 270;
	private int fastSpeed = 250;
	
	
	private static Port colorSensorPort = SensorPort.S1;
	private static EV3ColorSensor colorSensor;
	private static SampleProvider sampleProvider;
	private static int sampleSize;

	
	public MovementHandler () {
		Motor.A.setSpeed(180);
		Motor.B.setSpeed(normalSpeed);
      	Motor.C.setSpeed(normalSpeed);
      	
      	colorSensor = new EV3ColorSensor(colorSensorPort);
        sampleProvider = colorSensor.getRedMode();
        colorSensor.setFloodlight(Color.RED);
        sampleSize = sampleProvider.sampleSize();
        
        System.out.println("Faktor=" + ratio);
	}
	
	public void stdPosition() throws InterruptedException{
		Motor.A.rotate(720);
      	Motor.A.waitComplete();
	}
	
/*	public void moveForward (double cm) throws InterruptedException{
		
		if (cm > gridDistance) {
			Motor.B.setSpeed(540);
			Motor.C.setSpeed(540);
		}
		
		Motor.B.rotate(cmToAngle(cm), true);
		Motor.C.rotate(cmToAngle(cm), true);
		
		Motor.B.waitComplete();
		Motor.C.waitComplete();
    	//Motor.B.stop(true);
		
        // Takes some samples and prints them
        for (int i = 0; i < 4; i++) {
            float[] sample = getSample();
            System.out.println("N=" + i + " Sample=" + Arrays.toString(sample));
        }
      
    	//Motor.C.stop();
		Motor.B.setSpeed(270);
      	Motor.C.setSpeed(270);
	}
*/	
	public void moveBackward (double cm) throws InterruptedException {
		
		if (cm > gridDistance) {
			Motor.B.setSpeed(540);
			Motor.C.setSpeed(540);
		}
		
		Motor.B.rotate(-cmToAngle(cm), true);
		Motor.C.rotate(-cmToAngle(cm), true);
		Motor.B.waitComplete();
		Motor.C.waitComplete();
    	//Motor.B.stop(true);
    	//Motor.C.stop();
		
		Motor.B.setSpeed(270);
      	Motor.C.setSpeed(270);
	}
	
	public void turnRight (int angle) throws InterruptedException {
		
		Motor.B.rotate(calculateAngle(angle), true);
    	Motor.C.rotate(calculateAngle(-angle), true);
    	
    	Motor.B.waitComplete();
		Motor.C.waitComplete();
		
	}
	
	public void turnLeft (int angle) throws InterruptedException {
		
		Motor.B.rotate(calculateAngle(-angle), true);
    	Motor.C.rotate(calculateAngle(angle), true);
    	
    	Motor.B.waitComplete();
		Motor.C.waitComplete();
	}
	
	public void grab() throws InterruptedException{
		
		//open claw
      	Motor.A.rotate(-720);
      	Motor.A.waitComplete();
      	
      	
      	Motor.B.rotate(cmToAngle(16), true);
    	Motor.C.rotate(cmToAngle(16), true);
      	
    	Motor.B.waitComplete();
		Motor.C.waitComplete();
    	
    	//close claw
    	Motor.A.rotate(600);
     //	Thread.sleep(1000);
    	Motor.A.waitComplete();
    	
      	
      	Motor.B.rotate(-cmToAngle(16), true);
    	Motor.C.rotate(-cmToAngle(16), true);
    	
    	Motor.B.waitComplete();
		Motor.C.waitComplete();
	}
	
	public void drop() throws InterruptedException{
		
		//Motor.A.rotate(-720);
		
		Motor.B.rotate(cmToAngle(16), true);
    	Motor.C.rotate(cmToAngle(16), true);
    	
    	Motor.B.waitComplete();
		Motor.C.waitComplete();
    	
    	//open claw
		Motor.A.setSpeed(260);
    	Motor.A.rotate(-720);
		//Thread.sleep(1000);
    	Motor.A.waitComplete();
    	Motor.A.setSpeed(180);
    			
		
		Motor.B.rotate(-cmToAngle(16), true);
    	Motor.C.rotate(-cmToAngle(16), true);
    	
    	Motor.B.waitComplete();
		Motor.C.waitComplete();
		Motor.A.rotate(700);
	}
	
	public void openClaw() {
		Motor.A.rotate(-720);
	}
	
	public void closeClaw() {
		Motor.A.rotate(720);
	}
	
	public void moveForward (double cm) throws InterruptedException{
		
		//calculate how many times the motor has to turn
		Motor.B.resetTachoCount();
		Motor.C.resetTachoCount();
		int turns = cmToAngle(cm);
		
		Motor.B.setSpeed(normalSpeed);
		Motor.C.setSpeed(normalSpeed);
		
		if (cm > gridDistance) {
			Motor.B.setSpeed(540);
			Motor.C.setSpeed(540);
		}
		
		//start moving forward
		Motor.B.forward();
		Motor.C.forward();
		
		//Thread.sleep(1000);
		
		while (true) {
			
			//check if distance was reached and stop if so
			int angleB = Motor.B.getTachoCount();
			int angleC = Motor.C.getTachoCount();
			
			if (angleB > turns || angleC > turns) {
				break;
			}
			
			
			// if not reached yet, check if still on track
			GroundColor ground = getGroundColor();
			
			switch(ground){
			case GREY: 
				//still on track, do nothing
				System.out.println("GREY");
				Motor.B.setSpeed(normalSpeed);
				Motor.C.setSpeed(normalSpeed);
				break;
			case WHITE:
				//turned to inner line
				System.out.println("WHITE");
				Motor.B.setSpeed(fastSpeed);
				
//				Motor.B.stop(true);
//				Motor.C.stop();
//				
//				//kurs korrektur
//				Motor.B.rotate(calculateAngle(20), true);
//				Motor.C.rotate(calculateAngle(-20), true);
//				Motor.B.waitComplete();
//				Motor.C.waitComplete();
//				
//				//if the way was found again
//				
//				//continue on your way
//				Motor.B.resetTachoCount();
//				Motor.A.resetTachoCount();
//				turns = turns - angleB;
//				Motor.B.forward();
//				Motor.C.forward();
				break;
			
			case BLACK:
				//turned to outer
				System.out.println("BLACK");
				Motor.C.setSpeed(fastSpeed);
//				Motor.B.stop(true);
//				Motor.C.stop();
//				
//				//kurs korrektur
//				Motor.B.rotate(calculateAngle(20), true);
//				Motor.C.rotate(calculateAngle(-20), true);
//				Motor.B.waitComplete();
//				Motor.C.waitComplete();
//				
//				//if the way was found again
//				//continue on your way
//				Motor.B.resetTachoCount();
//				Motor.A.resetTachoCount();
//				turns = turns - angleB;
//				Motor.B.forward();
//				Motor.C.forward();
				break;
			}
		}
		
		//when while loop is done, stop moving
		Motor.B.stop(true);
    	Motor.C.stop();
	}
	
	public String printColor()
	{
		float[] sample = getSample();
		System.out.println("Sample= " + Arrays.toString(sample));
		return Float.toString(sample[0]);
	}
	
	public void setBlackValue(double color)
	{
		blackValue  = color;
	}
	
	public void setWhiteValue(double color)
	{
		whiteValue  = color;
	}
	
	
	private int calculateAngle (int angle) {
		
		Double d = ratio*angle;
				
		return d.intValue();
	}
	
	private int cmToAngle (double cm) {
		
		Double strecke = cmconstant*cm;
		
		return strecke.intValue();
	}
	
	private static float[] getSample() {
		// Initializes the array for holding samples
		float[] sample = new float[sampleSize];

		// Gets the sample an returns it
		sampleProvider.fetchSample(sample, 0);
		return sample;
	}
	
	private GroundColor getGroundColor()
	{
		float[] sample = getSample();
		
		if(sample[0] < blackValue)
		{
			return GroundColor.GREY;
		}
		else if (sample[0] > whiteValue)
		{
			return GroundColor.GREY;
		}
		return GroundColor.GREY;
	}
	
	protected enum GroundColor
	{
		WHITE, GREY, BLACK
	}

}
