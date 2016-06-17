package main;

import lejos.hardware.motor.Motor;

public class MovementHandler {
	
	// 4.5 * Winkel des Motors
	// z.B. 4.5 * 1 bewirkt, dass der Motor sich in der realen Welt um 1 Grad bewegt
	private double ratio = 4.5;
	private double cmconstant = 35;
	
	public MovementHandler () {
		
		Motor.A.setSpeed(180);
		Motor.B.setSpeed(270);
      	Motor.C.setSpeed(270);
		
	}
	
	
	public void moveForward (double cm) throws InterruptedException{
		Motor.B.rotate(cmToAngle(cm), true);
		Motor.C.rotate(cmToAngle(cm), true);
    	//Motor.B.stop(true);
    	//Motor.C.stop();
	}
	
	public void moveBackward (double cm) throws InterruptedException {
		
		Motor.B.rotate(-cmToAngle(cm), true);
		Motor.C.rotate(-cmToAngle(cm), true);
    	//Motor.B.stop(true);
    	//Motor.C.stop();
		
	}
	
	public void turnRight (int angle) throws InterruptedException {
		
		Motor.B.rotate(calculateAngle(angle), true);
    	Motor.C.rotate(calculateAngle(-angle), true);
    	Thread.sleep(3000);
		
	}
	
	public void turnLeft (int angle) throws InterruptedException {
		
		Motor.B.rotate(calculateAngle(-angle), true);
    	Motor.C.rotate(calculateAngle(angle), true);
    	Thread.sleep(3000);
		
	}
	
	public void grab() throws InterruptedException{
		
		//open claw
      	Motor.A.rotate(-720);
      	
      	//drive forward a bit
      	Motor.B.rotate(180, true);
    	Motor.C.rotate(180, true);
    	
    	//close claw
    	Motor.A.rotate(720);
      	Thread.sleep(1000);
      	
      	Motor.B.rotate(-180, true);
    	Motor.C.rotate(-180, true);
	}
	
	public void drop() throws InterruptedException{
		
		//Motor.A.rotate(-720);
		//drive backward a bit
      	Motor.B.rotate(180, true);
    	Motor.C.rotate(180, true);
    	
    	//open claw
    	Motor.A.rotate(-720);
		Thread.sleep(1000);
		
		//drive backward a bit
		Motor.B.rotate(-180, true);
    	Motor.C.rotate(-180, true);
	}
	
	private int calculateAngle (int angle) {
		
		Double d = ratio*angle;
				
		return d.intValue();
	}
	
	private int cmToAngle (double cm) {
		
		Double strecke = cmconstant*cm;
		
		return strecke.intValue();
	}

}
