package test;

import java.util.Arrays;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.NXTColorSensor;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.robotics.Color;
import lejos.robotics.SampleProvider;

public class NXTColorSensorTest2 {

    // Modes and samples are explained in LeJOS wiki:
    // http://sourceforge.net/p/lejos/wiki/Sensor%20Framework/
    private static Port colorSensorPort = SensorPort.S2;
    private static NXTColorSensor colorSensor;
    private static SampleProvider sampleProvider;
    private static int sampleSize;

    private static float[] getSample() {
        // Initializes the array for holding samples
        float[] sample = new float[sampleSize];

        // Gets the sample an returns it
        sampleProvider.fetchSample(sample, 0);
        return sample;
    }

    public static void main(String[] args) throws InterruptedException {
        // Initializes the sensor & sensor mode
        colorSensor = new NXTColorSensor(colorSensorPort);
        sampleProvider = colorSensor.getRedMode();
        colorSensor.setFloodlight(Color.RED);
        sampleSize = sampleProvider.sampleSize();

        // Takes some samples and prints them
        for (int i = 0; i < 4; i++) {
            float[] sample = getSample();
            System.out.println("N=" + i + " Sample=" + Arrays.toString(sample));
            Thread.sleep(1000);
        }
    }

}