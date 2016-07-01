package main;

import lejos.hardware.motor.Motor;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.NXTColorSensor;
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
	
	private double blackValueRight = 0.14;
	private double blackValueLeft = 0.35;
	
	private int normalSpeed = 140;
	private int fastSpeed = 170;
	
	
	private static Port rightColorSensorPort = SensorPort.S1;
	private static Port leftColorSensorPort = SensorPort.S2;
	private static EV3ColorSensor rightColorSensor;
	private static NXTColorSensor leftColorSensor;
	private static SampleProvider rightSampleProvider;
	private static SampleProvider leftSampleProvider;
	private static int sampleSize;
	
	
	public MovementHandler () {
		Motor.A.setSpeed(180);
		Motor.B.setSpeed(normalSpeed);
      	Motor.C.setSpeed(normalSpeed);
      	
      	rightColorSensor = new EV3ColorSensor(rightColorSensorPort);
        rightSampleProvider = rightColorSensor.getRedMode();
        rightColorSensor.setFloodlight(Color.RED);
        sampleSize = rightSampleProvider.sampleSize();
        
        leftColorSensor = new NXTColorSensor(leftColorSensorPort);
        leftSampleProvider = leftColorSensor.getRedMode();
        leftColorSensor.setFloodlight(Color.RED);
        
	}
	
	public void stdPosition() throws InterruptedException{
		Motor.A.rotate(720);
      	Motor.A.waitComplete();
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

	public void moveForward (double cm) throws InterruptedException{
		
		//calculate how many times the motor has to turn
		Motor.B.resetTachoCount();
		Motor.C.resetTachoCount();
		int turns = cmToAngle(cm);
		
//		if (cm > gridDistance) {
//			Motor.B.setSpeed(540);
//			Motor.C.setSpeed(540);
//		}
		
		//start moving forward
		Motor.B.forward();
		Motor.C.forward();
		
		while (true) {
			//check if distance was reached and stop if so
			int angleB = Motor.B.getTachoCount();
			int angleC = Motor.C.getTachoCount();
			
			if (angleB > turns && angleC > turns) {
				break;
			}
			
			// if not reached yet, check if still on track
			GroundColor ground = getGroundColor();
			
			switch(ground){
			case GREY: 
				//still on track, do nothing
				System.out.println("IN LINE");
				Motor.B.setSpeed(normalSpeed);
				Motor.C.setSpeed(normalSpeed);
				Motor.B.forward();
				Motor.C.forward();
				break;
			case BLACK_RIGHT:
				//turned to inner line
				System.out.println("RIGHT BORDER");
				Motor.C.setSpeed(fastSpeed);
				Motor.C.forward();
				break;
			
			case BLACK_LEFT:
				//turned to outer
				System.out.println("LEFT BORDER");
				Motor.B.setSpeed(fastSpeed);
				Motor.B.forward();
				break;
			}
		}
		
		//when while loop is done, stop moving
		Motor.B.stop(true);
    	Motor.C.stop();
	}
	
	public String printColor()
	{
		String result = "LEFT: " + getLeftSample() + " RIGHT: " + getRightSample() ;
		System.out.println(result);
		return result;
	}
	
	public void setBlackValueRight(double color)
	{
		blackValueRight  = color;
	}
	
	public void setBlackValueLeft(double color)
	{
		blackValueLeft  = color;
	}
	
	
	private int calculateAngle (int angle) {
		
		Double d = ratio*angle;
				
		return d.intValue();
	}
	
	private int cmToAngle (double cm) {
		
		Double strecke = cmconstant*cm;
		
		return strecke.intValue();
	}
	
	private static double getRightSample() {
		// Initializes the array for holding samples
		float[] sample = new float[sampleSize];

		// Gets the sample an returns it
		rightSampleProvider.fetchSample(sample, 0);
		
		float sum = 0;
	    for (int i = 0; i < sample.length; i++){
	      sum = sum + sample[i];
	    }
	    // calculate average
	    double average = sum / sample.length;
		return average;
	}
	
	private static double getLeftSample() {
		// Initializes the array for holding samples
		float[] sample = new float[sampleSize];

		// Gets the sample an returns it
		leftSampleProvider.fetchSample(sample, 0);
		
		float sum = 0;
	    for (int i = 0; i < sample.length; i++){
	      sum = sum + sample[i];
	    }
	    // calculate average
	    double average = sum / sample.length;
		return average;
	}
	
	private GroundColor getGroundColor()
	{
		double right = getRightSample();
		double left =getLeftSample();
		
		if(right < blackValueRight)
		{
			return GroundColor.BLACK_RIGHT;
		}
		else if (left < blackValueLeft)
		{
			return GroundColor.BLACK_LEFT;
		}
		System.out.println("Right: " + right + " left: " + left);
		return GroundColor.GREY;
	}
	
	protected enum GroundColor
	{
		BLACK_LEFT, GREY, BLACK_RIGHT
	}

}
