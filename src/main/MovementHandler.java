package main;

import lejos.hardware.motor.Motor;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.Color;
import lejos.robotics.SampleProvider;
import java.util.Arrays;

public class MovementHandler {
	
	// 4.5 * Winkel des Motors
	// z.B. 4.5 * 1 bewirkt, dass der Motor sich in der realen Welt um 1 Grad bewegt
	private double ratio = 4.6;
	private double cmconstant = 35;
	private int gridDistance = 25;
	
	private static Port colorSensorPort = SensorPort.S1;
	private static EV3ColorSensor colorSensor;
	private static SampleProvider sampleProvider;
	private static int sampleSize;

	
	public MovementHandler () {
		
		Motor.A.setSpeed(180);
		//Motor.B.setSpeed(270);
      	//Motor.C.setSpeed(270);
		Motor.B.setSpeed(90);
      	Motor.C.setSpeed(90);
      	
      	colorSensor = new EV3ColorSensor(colorSensorPort);
        sampleProvider = colorSensor.getRedMode();
        colorSensor.setFloodlight(Color.RED);
        sampleSize = sampleProvider.sampleSize();

		
	}
	
	public void stdPosition() throws InterruptedException{
		Motor.A.rotate(720);
      	Motor.A.waitComplete();
	}
	
	public void moveForward (double cm) throws InterruptedException{
		
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
	
	public void forwardTesting (double cm) throws InterruptedException{
		
		Motor.B.forward();
		Motor.C.forward();
		
		int turns = cmToAngle(cm);
		
		while (Motor.B.isMoving() && Motor.C.isMoving()) {
			
			int angleB = Motor.B.getTachoCount();
			int angleC = Motor.C.getTachoCount();
			
			if (angleB > turns) {
				break;
			}
			
			float[] sample = getSample();
            System.out.println("N=" +  " Sample=" + Arrays.toString(sample));
			
            float limit = 0.1f;
            int compi = Float.compare( getSample()[0], limit );
            
            System.out.println(compi);
            System.out.println(getSample()[0]);
            
			if (compi < 0 ) {
				//
				Motor.B.stop(true);
		    	Motor.C.stop();
		    	
		    	//kurskorrektur
		    	Motor.B.rotate(calculateAngle(20), true);
		    	Motor.C.rotate(calculateAngle(-20), true);
		    	Motor.B.waitComplete();
		    	Motor.C.waitComplete();
		    	
		    	//continue on your way
		    	turns = turns - angleB;
		    	Motor.B.forward();
				Motor.C.forward();
			}
		}
		
		Motor.B.stop(true);
    	Motor.C.stop();

				
		
        // Takes some samples and prints them
   /*     for (int i = 0; i < 4; i++) {
            float[] sample = getSample();
            System.out.println("N=" + i + " Sample=" + Arrays.toString(sample));
        }
     */ 
   
		
		
    	//Motor.C.stop();
		
	
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
	
	

}
