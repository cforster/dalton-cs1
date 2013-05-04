package org.dalton;
/**
 * This Class is a simplified MIDI player.  Use it to create and play any song you like.
 * 
 * @author Charlie Forster
 */

import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public class DaltonSound {	
	private Sequencer player;
	private Sequence seq;
	private Track track;
	private String voice;
	
	private int tempo;
	private int beatCounter; 	

	/**
	 * Constructor for the DaltonMidi Class.
	 */
	public DaltonSound() {
		try {
			//create midi track:
			player = MidiSystem.getSequencer();
			player.open();
			seq = new Sequence(Sequence.PPQ, 4);
			track = seq.createTrack();
			
//			for(Object o : MidiSystem.getSynthesizer().getAvailableInstruments()) {
//				System.out.println(o);
//			}
			
			//set instrument:
			setInstrument(2);

			//set tempo
			setTempo(2);
			beatCounter = 1;
		} 
		catch (Exception ex) 
		{
			ex.printStackTrace();
		}
		
		voice = "Fred"; //default
	}	

	/**
	 * set tempo
	 * @param tempo the speed of the beats, 1 is the fastest.
	 */
	public void setTempo(int tempo)
	{
		this.tempo = tempo;
	}

	/**
	 * set instrument
	 * @param instrument the instrument you would like, between 1 and 127
	 */
	public void setInstrument(int instrument)
	{
		instrument--;
		try {
			//set instrument:
			ShortMessage first = new ShortMessage();
			first.setMessage(192,1,instrument,0);
			MidiEvent setInstrument = new MidiEvent(first,1);
			track.add(setInstrument);
		} catch (InvalidMidiDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * use the mac tts generator to speak
	 */
	public void say(String s)
	{
		try {
			Runtime.getRuntime().exec("say -v " + voice + " \"" + s + "\"");
		} catch (IOException e) {
			System.err.println("say failed");
			e.printStackTrace();
		}
	}
	
	/**
	 * set voice
	 * Agnes               en_US    # Isn't it nice to have a computer that will talk to you?<br>
	 * Albert              en_US    #  I have a frog in my throat. No, I mean a real frog!<br>
	 * Alex                en_US    # Most people recognize me by my voice.<br>
	 * Bad News            en_US    # The light you see at the end of the tunnel is the headlamp of a fast approaching train.<br>
	 * Bahh                en_US    # Do not pull the wool over my eyes.<br>
	 * Bells               en_US    # Time flies when you are having fun.<br>
	 * Boing               en_US    # Spring has sprung, fall has fell, winter's here and it's colder than usual.<br>
	 * Bruce               en_US    # I sure like being inside this fancy computer<br>
	 * Bubbles             en_US    # Pull the plug! I'm drowning!<br>
	 * Cellos              en_US    # Doo da doo da dum dee dee doodly doo dum dum dum doo da doo da doo da doo da doo da doo da doo<br>
	 * Deranged            en_US    # I need to go on a really long vacation.<br>
	 * Fred                en_US    # I sure like being inside this fancy computer<br>
	 * Good News           en_US    # Congratulations you just won the sweepstakes and you don't have to pay income tax again.<br>
	 * Hysterical          en_US    # Please stop tickling me!<br>
	 * Junior              en_US    # My favorite food is pizza.<br>
	 * Kathy               en_US    # Isn't it nice to have a computer that will talk to you?<br>
	 * Pipe Organ          en_US    # We must rejoice in this morbid voice.<br>
	 * Princess            en_US    # When I grow up I'm going to be a scientist.<br>
	 * Ralph               en_US    # The sum of the squares of the legs of a right triangle is equal to the square of the hypotenuse.<br>
	 * Trinoids            en_US    # We cannot communicate with these carbon units.<br>
	 * Vicki               en_US    # Isn't it nice to have a computer that will talk to you?<br>
	 * Victoria            en_US    # Isn't it nice to have a computer that will talk to you?<br>
	 * Whisper             en_US    # Pssssst, hey you, Yeah you, Who do ya think I'm talking to, the mouse?<br>
	 * Zarvox              en_US    # That looks like a peaceful planet.<br>
	 */
	public void setVoice(String voice) {
		this.voice = voice;
	}
	
	/**
	 * wait a given number of milliseconds
	 * @param millis the amount of time to wait
	 */
	public void sleep(int millis)
	{
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * push a rest in the song.
	 * @param length number of beats the rest will last.
	 */
	public void pushRest(int length) {
		beatCounter +=length*tempo;
	}

	/**
	 * push a rest in the song.
	 */
	public void pushRest() {
		this.pushRest(1);
	}

	/**
	 * push a note in the song
	 * @param note pitch of the note (between 0 and 127)
	 */
	public void pushNote(int note) {
		this.pushNote(note, 1);
	}

	/**
	 * push a note in the song
	 * @param note pitch of the note (between 0 and 127)
	 * @param length number of beats the note will last
	 */
	public void pushNote(int note, int length) {
		try {
			ShortMessage a = new ShortMessage();
			a.setMessage(144, 1, note, 100);
			MidiEvent noteOn = new MidiEvent(a, beatCounter);
			track.add(noteOn);

			beatCounter += tempo*length;

			ShortMessage b = new ShortMessage();
			b.setMessage(128, 1, note, 100);
			MidiEvent noteOff = new MidiEvent(b, beatCounter);
			track.add(noteOff);
		} 
		catch (Exception ex) 
		{
			ex.printStackTrace();
		}
	}

	/**
	 * play your song
	 */
	public void popAll() {
		try {
			player.setSequence(seq);
			player.start();
		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
		} 
	}


	/**
	 * close the midi player
	 */
	public void close()
	{
		try{
			while (player.isRunning()) {
				Thread.sleep(100);
			}
			Thread.sleep(1000);  //wait for sustain to finish
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		player.close();
	}

}