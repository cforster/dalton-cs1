package org.dalton;

import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.bluetooth.BluetoothConnectionException;

import org.wiigee.event.ButtonListener;
import org.wiigee.event.ButtonPressedEvent;
import org.wiigee.event.ButtonReleasedEvent;
import org.wiigee.event.GestureEvent;
import org.wiigee.event.GestureListener;
import org.wiigee.filter.HighPassFilter;
import org.wiigee.util.Log;
import org.wiimote.device.Wiimote;
import org.wiimote.filter.RotationThresholdFilter;

/**
 * use the wiimote as a controller.
 */


public class DaltonWii {
	public static void main(String[] args) {
		//declarations:
		DaltonWii dw = new DaltonWii("001B7A424FDD");
		dw.setLED(1,1,1,1);
		DaltonSay voice = new DaltonSay();

		while(true) {
			String event = dw.next();
			voice.say(event);
			System.out.println(event);
			if(event.equalsIgnoreCase("1")) System.out.println("you so square");
			if(event.equals("2")) {
				dw.close();
				return;
			}
		}
	}

	private String lastEvent = "";
	private Wiimote wm;
	private CountDownLatch lastEventLatch;
	private boolean releaseOn = false;
	//L2CAPConnection l2;

	/**
	 * choose whether to receive button release events
	 * @param on true if you want to receive release events, false if not.
	 */
	public void receiveReleaseEvents(boolean on) {
		releaseOn = on;
	}

	/**
	 * close the wiimote connection to end the program.
	 */
	public void close() {
		wm.disconnect();
	}

	/**
	 * This function returns the next event the user submits.  There are two types, a button press or a gesture.  <br>
	 * <br>
	 * button presses are 1, 2, A, UP, DOWN, HOME, LEFT, RIGHT, MINUS, PLUS.<br>
	 * default gestures are up, down, left, right, poke, circle, square<br>
	 * the B button is used to trigger gestures.
	 * @return a String response based on the event.
	 */
	public String next() {
		synchronized(this) { lastEventLatch = new CountDownLatch(1); }
		try {
			lastEventLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
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
		lastEvent = null;
		synchronized(this) { lastEventLatch = new CountDownLatch(1); }
		try {
			lastEventLatch.await(timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
		this(address, null);
	}

	/**
	 * Construct the wiimote based on the bluetooth address for your wiimote and a gesture file
	 * @param address the bluetooth address for the wiimote
	 * @param gesturefile the gesture file.
	 */
	public DaltonWii(String address, String gesturefile) {
		System.setProperty("bluecove.jsr82.psm_minimum_off", "true");
		System.setProperty("PROPERTY_DEBUG_STDOUT", "false"	);

		int tries = 3; //if an old version is running, the 
		//first time it breaks, the second time it works.
		while(tries-->0) {
			try {
				wm = new Wiimote(address, true, true);
				wm.addAccelerationFilter(new HighPassFilter());
				wm.addRotationFilter(new RotationThresholdFilter(0.5));
				wm.addButtonListener(new ButtonListen());
				if(gesturefile==null) wm.addGestureListener(new GestureListen());
				else wm.addGestureListener(new GestureListen(gesturefile));
				//l2 = wm.getReceiveConnection();
				Log.setLevel(Log.OFF);
				break;
			} catch (BluetoothConnectionException e) {
				System.err.println("bluetooth failure, try power cycling your bluetooth");
			} catch (IOException e) {
				if(tries==0) {
					System.err.println("could not set up wiimote");
					e.printStackTrace();
				}
			} 
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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
			lastEventLatch.countDown();
		}

		public GestureListen() {
			wm.loadGesture("gestures/square.txt");
			wm.loadGesture("gestures/circle.txt");
			wm.loadGesture("gestures/down.txt");
			wm.loadGesture("gestures/left.txt");
			wm.loadGesture("gestures/right.txt");
			wm.loadGesture("gestures/poke.txt");
			wm.loadGesture("gestures/up.txt");
			this.gestureMeanings.add("square");
			this.gestureMeanings.add("circle");
			this.gestureMeanings.add("down");
			this.gestureMeanings.add("left");
			this.gestureMeanings.add("right");
			this.gestureMeanings.add("poke");
			this.gestureMeanings.add("up");			
		}
		
		public GestureListen(String gestureDirectory) {
			this();
			addGestures(gestureDirectory);
		}
		
		public void addGestures(String gestureDirectory) {
			if(!gestureDirectory.endsWith("/")) gestureDirectory+="/";
			String[] names = (new File(gestureDirectory)).list();
			if(names.length==0) {
				System.err.println("could not find gestures in " + gestureDirectory);
				return;
			}
			for (int i = 0; i < names.length; i++) {
				int chop = names[i].indexOf(".txt");
				if(chop>0) { names[i] = names[i].substring(0, chop); }
				wm.loadGesture(gestureDirectory+ names[i] +".txt");
				this.gestureMeanings.add(names[i]);
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
			default: return;
			}
			if(lastEventLatch!=null) lastEventLatch.countDown();
		}

		@Override
		public void buttonReleaseReceived(ButtonReleasedEvent event) {
			if(!releaseOn) return; // only do this if we want release events.
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
			default: return;

			}
			if(lastEventLatch!=null) lastEventLatch.countDown();
		}
	}
}
