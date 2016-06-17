package main;

import lejos.hardware.motor.Motor;

public class MovementHandler {
	
	// 4.5 * Winkel des Motors
	// z.B. 4.5 * 1 bewirkt, dass der Motor sich in der realen Welt um 1 Grad bewegt
	private double ratio = 4.6;
	private double cmconstant = 35;
	private int gridDistance = 25;
	
	public MovementHandler () {
		
		Motor.A.setSpeed(180);
		Motor.B.setSpeed(270);
      	Motor.C.setSpeed(270);
		
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
	
	private int calculateAngle (int angle) {
		
		Double d = ratio*angle;
				
		return d.intValue();
	}
	
	private int cmToAngle (double cm) {
		
		Double strecke = cmconstant*cm;
		
		return strecke.intValue();
	}

}
