package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

import lejos.hardware.Sound;

public class Ev3Controller {
  public static final int PORT = 80;
  private ServerSocket ss;
  private Socket sock;
  private MovementHandler handler;

  public Ev3Controller() throws IOException {
    ss = new ServerSocket(PORT);
    handler = new MovementHandler();
  }

  public void run() throws IOException, InterruptedException {
    for (;;) {
      sock = ss.accept();
      InputStream is = sock.getInputStream();
      OutputStream os = sock.getOutputStream();

      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      PrintStream ps = new PrintStream(os);

      for (;;) {
        String cmd = br.readLine();
        if (cmd == null)
          break;
        String reply = execute(cmd);
        if (reply != null)
          ps.println(reply);
        else {
          br.close();
          ps.close();
          break;
        }
      }

    }
  }

  public String execute(String cmd) throws InterruptedException {
    String[] tokens = cmd.split(" ");
    
    System.out.println(tokens[0] + " " + tokens[1]);
    String answer = "";

    if (tokens.length > 1 && tokens[0].equals("GET")) {
    	
      if (tokens[1].equals("/Hello")) {
        Sound.beepSequenceUp();
       
      }
      
  	  else if (tokens[1].equals("/backward")) {
  		
  		handler.moveBackward(0);
  		answer = "moving backward";
  		
  	  }
	  else if (tokens[1].equals("/forward")) {
		handler.moveForward(0);
		answer = "moving forward";
	  }
	  else if (tokens[1].equals("/turnright")) {
		handler.turnRight(90);
		answer = "turning right 90 degrees";
	  }
	  else if (tokens[1].equals("/turnleft")) {
		handler.turnLeft(90);
		answer = "turning left 90 degrees";
	  }
	  else if (tokens[1].equals("/grab")) {
		handler.grab();
		answer = "grabbing container";
	  }
	  else if (tokens[1].equals("/drop")) {
		handler.drop();
		answer = "dropping container";
	  }
      
      return "HTTP/1.1 200 OK\r\n\r\nOK: " + answer + "\r\n";
    }
    return null;
  }

  public static void main(String[] args) throws IOException {
    try {
		Sound.beepSequenceUp();
    	new Ev3Controller().run();
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
}