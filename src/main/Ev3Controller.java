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
    String status = "200 OK\r\n\r\nOK: ";

    if (tokens.length > 1 && tokens[0].equals("GET")) {
    	
    	String command = tokens[1];
    	
    	String[] arr = command.split("-");
    	
      if (arr[0].equals("/Hello")) {
        Sound.beepSequenceUp();
       
      }
      
  	  else if (arr[0].equals("/backward")) {
  		try {
			double cm = Integer.parseInt(arr[1]);
			handler.moveBackward(cm);
			answer = "moving " + cm + " backwards";
			}
		
		catch (NumberFormatException e) {
			System.out.println("could not parse double parameter");
			answer = "forward request failed";
			status = "400 bad_request\r\n\r\nbad_request: ";
			Sound.buzz();
		}
  		
  		catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("no distance in cm was given");
			answer = "no distance in cm was given, backward request failed";
			status = "400 bad_request\r\n\r\nbad_request: ";
			Sound.buzz();
		}
  	  }
      
	  else if (arr[0].equals("/forward")) {
		try {
			double cm = Integer.parseInt(arr[1]);
			handler.moveForward(cm);
			answer = "moving " + cm + " forwards";
			}
		
		catch (NumberFormatException e) {
			System.out.println("could not parse double parameter");
			answer = "forward request failed";
			status = "400 bad_request\r\n\r\nbad_request: ";
			Sound.buzz();
		}
		
		catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("no distance in cm was given");
			answer = "no distance in cm was given, forward request failed";
			status = "400 bad_request\r\n\r\nbad_request: ";
			Sound.buzz();
		}
	  }
      
	  else if (arr[0].equals("/turnright")) {
		try {
			int cm = Integer.parseInt(arr[1]);
			handler.turnRight(cm);
			answer = "turning " + cm + " degrees to the right";
			}
		
		catch (NumberFormatException e) {
			System.out.println("could not parse integer parameter");
			answer = "turning right request failed ";
			status = "400 bad_request\r\n\r\nbad_request: ";
			Sound.buzz();
		}
		
		catch (InterruptedException e) {
			System.out.println("was interrupted");
			status = "400 bad_request\r\n\r\nbad_request: ";
			Sound.buzz();
		}
		
		catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("no angle in degrees was given");
			answer = "no angle in degrees was given, turning right request failed";
			status = "400 bad_request\r\n\r\nbad_request: ";
			Sound.buzz();
		}
		
	  }
      
	  else if (arr[0].equals("/turnleft")) {
		try {
			int cm = Integer.parseInt(arr[1]);
			handler.turnLeft(cm);
			answer = "turning " + cm + " degrees to the left";
			}
		
		catch (NumberFormatException e) {
			System.out.println("could not parse integer parameter");
			answer = "turning left request failed";
			status = "400 bad_request\r\n\r\nbad_request: ";
			Sound.buzz();
		}
		  
		catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("no angle in degrees was given");
			answer = "no angle in degrees was given, turning left request failed";
			status = "400 bad_request\r\n\r\nbad_request: ";
			Sound.buzz();
		}
	  }
      
	  else if (arr[0].equals("/grab")) {
		handler.grab();
		answer = "grabbing container";
	  }
      
	  else if (arr[0].equals("/drop")) {
		handler.drop();
		answer = "dropping container";
	  }
      
	  else {
		  answer = "You know nothing John Snow";
		  status = "418 I'm a teapot\r\n\r\nI'm a teapot ";
		  Sound.buzz();
	  }
      
      return "HTTP/1.1 " + status + answer + "\r\n";
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