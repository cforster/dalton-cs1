package org.dalton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import org.wiigee.event.ButtonListener;
import org.wiigee.event.ButtonPressedEvent;
import org.wiigee.event.ButtonReleasedEvent;
import org.wiigee.event.GestureEvent;
import org.wiigee.event.GestureListener;
import org.wiigee.filter.HighPassFilter;
import org.wiigee.util.Log;
import org.wiimote.device.Wiimote;
import org.wiimote.filter.RotationThresholdFilter;

/*
 * The DaltonWii class helps you use the wiimote as a controller. <br>
 * <br>
 * IMPORTANT: needs to be run with the -d32 option for the VM
 */


public class DaltonWii {
	public static void main(String[] args) {
		//declarations:
		DaltonWii dw = new DaltonWii("00191DD44482");
		
		while(true) {
			String event = dw.next();
			System.out.println(event);
			if(event.equalsIgnoreCase("1")) System.out.println("you so square");
		}
		
	}
	private String lastEvent = "";
	private Wiimote wm;
	//L2CAPConnection l2;

	/**
	 * This function returns the next event the user submits.  There are two types, a button press or a gesture.  <br>
	 * <br>
	 * button presses are 1, 2, A, UP, DOWN, HOME, LEFT, RIGHT, MINUS, PLUS.<br>
	 * default gestures are up, down, left, right, poke, circle, square<br>
	 * the B button is used to trigger gestures.
	 * @return a String response based on the event.
	 */
	public String next() {
		lastEvent="none";
		while(lastEvent.equals("none")) { ;
//			try {
//				if(!l2.ready()){
//					wm.disconnect(); 
//					return "";
//				}
//			} catch (IOException e) {
//				wm.disconnect();
//				return "";
//			}
		}
		return lastEvent;
	}
	
	/**
	 * This function returns the next event the user submits.  It will only wait the given number of milliseconds.  There are two types, a button press or a gesture.  <br>
	 * <br>
	 * button presses are 1, 2, A, UP, DOWN, HOME, LEFT, RIGHT, MINUS, PLUS.<br>
	 * default gestures are up, down, left, right, poke, circle, square<br>
	 * the B button is used to trigger gestures.
	 * @param timeout the amount of time to wait in millisectonds.
	 * @return a String response based on the event.
	 */
	public String next(long timeout) {
		lastEvent = "none";
		long time = System.currentTimeMillis();
		while(lastEvent.equals("none")&&System.currentTimeMillis()-time<timeout) { ; }
		return lastEvent;
	}
	

	/**
	 * turn on the LEDs on the wiimote, 1 is on and 0 is off.
	 * @param a led 1
	 * @param b led 2
	 * @param c led 3
	 * @param d led 4
	 */
	public void setLED(int a, int b, int c, int d) {
		int value = a + b*2 + c*4 + d*8;
		setLED(value);
	}

	/**
	 * turn on the LEDs on the wiimote.  Enables one or more LEDs, where the value could be between 0 and 8. If value=1 only the left LED would light up, for value=2 the second led would light up, for value=3 the first and second led would light up, and so on...
	 * @param value from 0 to 8.
	 */
	public void setLED(int value) {
		try {
			wm.setLED(value);
		} catch (IOException e) {
			System.err.println("could not set LED");
			e.printStackTrace();
		}
	}

	/**
	 * With this method you gain access over the vibrate function of the wiimote. You got to try which time in milliseconds would fit your requirements.
	 * @param milliseconds Time the wiimote would approx. vibrate.
	 */
	public void vibrateForTime(long milliseconds) {
		try {
			wm.vibrateForTime(milliseconds);
		} catch (IOException e) {
			System.err.println("could not vibrate");
			e.printStackTrace();
		}
	}


	/**
	 * Construct the wiimote based on the bluetooth address for your wiimote
	 * @param address the bluetooth address for the wiimote
	 */
	public DaltonWii(String address) {
		this(address, "defaultgestures");
	}

	/**
	 * Construct the wiimote based on the bluetooth address for your wiimote and a gesture file
	 * @param address the bluetooth address for the wiimote
	 * @param gesturefile the gesture file.
	 */
	public DaltonWii(String address, String gesturefile) {
		System.setProperty("bluecove.jsr82.psm_minimum_off", "true");
		System.setProperty("PROPERTY_DEBUG_STDOUT", "false"	);

		try {
			wm = new Wiimote(address, true, true);
			wm.addAccelerationFilter(new HighPassFilter());
			wm.addRotationFilter(new RotationThresholdFilter(0.5));
			wm.addButtonListener(new ButtonListen());
			wm.addGestureListener(new GestureListen(gesturefile));
			//l2 = wm.getReceiveConnection();
			Log.setLevel(Log.OFF);
		} catch (IOException e) {
			System.err.println("could not set up wiimote");
			e.printStackTrace();
		}
	}

	class GestureListen implements GestureListener {
		private Vector<String> gestureMeanings = new Vector<String>();;

		@Override
		public void gestureReceived(GestureEvent event) {
			if(event.isValid()) {
				lastEvent = this.gestureMeanings.elementAt(event.getId());
			} else {
				System.err.println("No Gesture recognized!");
			}			
		}

		public GestureListen(String gesturefile) {
			BufferedReader in = null;
			try {
				// open the gesture set file
				File f = new File("gestures/" +gesturefile);
				in = new BufferedReader(new FileReader(f));

				// load the single gestures
				String line;
				while(in.ready()) {
					line = in.readLine();
					wm.loadGesture("gestures/"+ line);
					this.gestureMeanings.add(line);
				}
			} catch (IOException ex) {
				System.err.println("could not find gestures files");
			} finally {
				try {
					in.close();
				} catch (IOException ex) {
					System.err.println("error closing file");
				}
			}
		}
	}

	class ButtonListen implements ButtonListener {
		@Override
		public void buttonPressReceived(ButtonPressedEvent event) {
			switch(event.getButton()) {
			case Wiimote.BUTTON_1: lastEvent= "1"; break;
			case Wiimote.BUTTON_2: lastEvent= "2"; break;
			case Wiimote.BUTTON_A: lastEvent= "A"; break;
			case Wiimote.BUTTON_UP: lastEvent= "UP"; break;
			case Wiimote.BUTTON_DOWN: lastEvent= "DOWN"; break;
			case Wiimote.BUTTON_HOME: lastEvent= "HOME"; break;
			case Wiimote.BUTTON_LEFT: lastEvent= "LEFT"; break;
			case Wiimote.BUTTON_RIGHT: lastEvent= "RIGHT"; break;
			case Wiimote.BUTTON_MINUS: lastEvent= "MINUS"; break;
			case Wiimote.BUTTON_PLUS: lastEvent= "PLUS"; break;
			default: break;
			}
		}

		@Override
		public void buttonReleaseReceived(ButtonReleasedEvent event) {
			switch(event.getButton()) {
			case Wiimote.BUTTON_1: lastEvent= "1_release"; break;
			case Wiimote.BUTTON_2: lastEvent= "2_release"; break;
			case Wiimote.BUTTON_A: lastEvent= "A_release"; break;
			case Wiimote.BUTTON_UP: lastEvent= "UP_release"; break;
			case Wiimote.BUTTON_DOWN: lastEvent= "DOWN_release"; break;
			case Wiimote.BUTTON_HOME: lastEvent= "HOME_release"; break;
			case Wiimote.BUTTON_LEFT: lastEvent= "LEFT_release"; break;
			case Wiimote.BUTTON_RIGHT: lastEvent= "RIGHT_release"; break;
			case Wiimote.BUTTON_MINUS: lastEvent= "MINUS_release"; break;
			case Wiimote.BUTTON_PLUS: lastEvent= "PLUS_release"; break;
			default: break;

			}
		}
	}
}
