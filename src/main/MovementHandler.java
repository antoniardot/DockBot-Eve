package main;

import lejos.hardware.motor.Motor;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.NXTColorSensor;
import lejos.robotics.Color;
import lejos.robotics.SampleProvider;

public class MovementHandler {

	private static final int maxBackwardFromCell = 8;

	private static final int closeClaw = 580;

	// Motor C is right, Motor B is left
	// 4.5 * Winkel des Motors
	// z.B. 4.5 * 1 bewirkt, dass der Motor sich in der realen Welt um 1 Grad
	// bewegt
	// private double ratio = 4.6*(5*360)/(5*360+55)*(5*360+8)/(5*360);
	private double ratio = 4.483450134;

	private double cmconstant = 35;
	private int gridDistance = 25;

	private double blackValueRight = 0.10;
	private double blackValueLeft = 0.27;

	private int normalSpeed = 140;
	private int fastSpeed = 170;

	private static Port rightColorSensorPort = SensorPort.S4;
	private static Port leftColorSensorPort = SensorPort.S2;
	private static EV3ColorSensor rightColorSensor;
	private static NXTColorSensor leftColorSensor;
	private static SampleProvider rightSampleProvider;
	private static SampleProvider leftSampleProvider;
	private static int sampleSize;

	public MovementHandler() {
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

	public void stdPosition() throws InterruptedException {
		Motor.A.rotate(closeClaw);
		Motor.A.waitComplete();
	}

	public void turnRight(int angle) throws InterruptedException {

		Motor.B.rotate(calculateAngle(angle), true);
		Motor.C.rotate(calculateAngle(-angle), true);

		Motor.B.waitComplete();
		Motor.C.waitComplete();

	}

	public void turnLeft(int angle) throws InterruptedException {

		Motor.B.rotate(calculateAngle(-angle), true);
		Motor.C.rotate(calculateAngle(angle), true);

		Motor.B.waitComplete();
		Motor.C.waitComplete();
	}

	public void grab() throws InterruptedException {

		// open claw
		Motor.A.rotate(-720);
		Motor.A.waitComplete();

		// move forward until hitting black stripe, but at least 20cm
		Motor.B.resetTachoCount();
		Motor.C.resetTachoCount();
		int turns = cmToAngle(30);

		Motor.B.forward();
		Motor.C.forward();

		outerLoop:
		while (true) {
			// if 20 cm are reached, stop
			if (Motor.B.getTachoCount() > turns || Motor.C.getTachoCount() > turns) {
				break;
			}

			GroundColor ground = getGroundColor();

			switch (ground) {
			case BLACK_RIGHT:
				// if black stripe is breached, stop and wait til other motor reaches black
				Motor.C.stop(true);
				Motor.C.setSpeed(10);
				Motor.C.backward();
				while (getLeftSample()> blackValueLeft)
				{
				}
				break outerLoop;
			// if black stripe is breached, stop and wait til other motor reaches black
			case BLACK_LEFT:
				Motor.B.stop(true);
				Motor.B.setSpeed(10);
				Motor.B.backward();
				while (getRightSample()> blackValueRight)
				{
				}
				break outerLoop;
				
			// else keep going in the loop
			case GREY:
			}
		}
		// stop motors when while loop is left
		Motor.B.stop(true);
		Motor.C.stop();
		Motor.B.setSpeed(normalSpeed);
		Motor.C.setSpeed(normalSpeed);

		// close claw
		Motor.A.rotate(closeClaw);
		Motor.A.waitComplete();

		// move back out of the cell
		Motor.B.rotate(-cmToAngle(maxBackwardFromCell), true);
		Motor.C.rotate(-cmToAngle(maxBackwardFromCell), true);

		Motor.B.waitComplete();
		Motor.C.waitComplete();
	}

	public void drop() throws InterruptedException {

		// approach cell
		Motor.B.resetTachoCount();
		Motor.C.resetTachoCount();
		int turns = cmToAngle(30);
		
		Motor.B.forward();
		Motor.C.forward();
		
		outerLoop:
		while (true) {
			// if 20 cm are reached, stop
			if (Motor.B.getTachoCount() > turns || Motor.C.getTachoCount() > turns) {
				break;
			}

			GroundColor ground = getGroundColor();

			switch (ground) {
			case BLACK_RIGHT:
				// if black stripe is breached, stop and wait til other motor reaches black
				Motor.C.stop(true);
				Motor.C.setSpeed(10);
				Motor.C.backward();
				while (getLeftSample()> blackValueLeft)
				{
				}
				break outerLoop;
			// if black stripe is breached, stop as well
			case BLACK_LEFT:
				Motor.B.stop(true);
				Motor.B.setSpeed(10);
				Motor.B.backward();
				while (getRightSample()> blackValueRight)
				{
				}
				break outerLoop;
			// else keep going in the loop
			case GREY:
			}
		}
		// stop motors when while loop is left
		Motor.B.stop(true);
		Motor.C.stop();
		Motor.B.setSpeed(normalSpeed);
		Motor.C.setSpeed(normalSpeed);

		// open claw
		Motor.A.rotate(-720);
		Motor.A.waitComplete();
		
		//move back out of cell
		Motor.B.rotate(-cmToAngle(maxBackwardFromCell), true);
		Motor.C.rotate(-cmToAngle(maxBackwardFromCell), true);
		Motor.B.waitComplete();
		Motor.C.waitComplete();
		
		// close the claw
		Motor.A.rotate(closeClaw);
	}

	public void openClaw() {
		Motor.A.rotate(-720);
	}

	public void closeClaw() {
		Motor.A.rotate(closeClaw);
	}

	public void moveBackward(double cm) throws InterruptedException {

		if (cm > gridDistance) {
			Motor.B.setSpeed(540);
			Motor.C.setSpeed(540);
		}

		Motor.B.rotate(-cmToAngle(cm), true);
		Motor.C.rotate(-cmToAngle(cm), true);
		Motor.B.waitComplete();
		Motor.C.waitComplete();
		// Motor.B.stop(true);
		// Motor.C.stop();

		Motor.B.setSpeed(270);
		Motor.C.setSpeed(270);
	}

	public void moveForward(double cm) throws InterruptedException {

		// calculate how many times the motor has to turn
		Motor.B.resetTachoCount();
		Motor.C.resetTachoCount();
		int turns = cmToAngle(cm);

		// if (cm > gridDistance) {
		// Motor.B.setSpeed(540);
		// Motor.C.setSpeed(540);
		// }

		// start moving forward
		Motor.B.forward();
		Motor.C.forward();

		while (true) {
			// check if distance was reached and stop if so
			int angleB = Motor.B.getTachoCount();
			int angleC = Motor.C.getTachoCount();

			if (angleB > turns && angleC > turns) {
				break;
			}

			// if not reached yet, check if still on track
			GroundColor ground = getGroundColor();

			switch (ground) {
			case GREY:
				// still on track, do nothing
				System.out.println("IN LINE");
				Motor.B.setSpeed(normalSpeed);
				Motor.C.setSpeed(normalSpeed);
				Motor.B.forward();
				Motor.C.forward();
				break;
			case BLACK_RIGHT:
				// turned to inner line
				System.out.println("RIGHT BORDER");
				Motor.C.setSpeed(fastSpeed);
				Motor.C.forward();
				break;

			case BLACK_LEFT:
				// turned to outer
				System.out.println("LEFT BORDER");
				Motor.B.setSpeed(fastSpeed);
				Motor.B.forward();
				break;
			}
		}

		// when while loop is done, stop moving
		Motor.B.stop(true);
		Motor.C.stop();
	}

	public String printColor() {
		String result = "LEFT: " + getLeftSample() + " RIGHT: " + getRightSample();
		System.out.println(result);
		return result;
	}

	public void setBlackValueRight(double color) {
		blackValueRight = color;
	}

	public void setBlackValueLeft(double color) {
		blackValueLeft = color;
	}

	private int calculateAngle(int angle) {

		Double d = ratio * angle;

		return d.intValue();
	}

	private int cmToAngle(double cm) {

		Double strecke = cmconstant * cm;

		return strecke.intValue();
	}

	private static double getRightSample() {
		// Initializes the array for holding samples
		float[] sample = new float[sampleSize];

		// Gets the sample an returns it
		rightSampleProvider.fetchSample(sample, 0);

		float sum = 0;
		for (int i = 0; i < sample.length; i++) {
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
		for (int i = 0; i < sample.length; i++) {
			sum = sum + sample[i];
		}
		// calculate average
		double average = sum / sample.length;
		return average;
	}

	private GroundColor getGroundColor() {
		double right = getRightSample();
		double left = getLeftSample();

		if (right < blackValueRight) {
			return GroundColor.BLACK_RIGHT;
		} else if (left < blackValueLeft) {
			return GroundColor.BLACK_LEFT;
		}
		return GroundColor.GREY;
	}

	protected enum GroundColor {
		BLACK_LEFT, GREY, BLACK_RIGHT
	}

}
