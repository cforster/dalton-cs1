package org.dalton;
/**
 * This Class is a simplified MIDI player.  Use it to create and play any song you like.
 * 
 * @author Charlie Forster
 */

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public class DaltonMidi {	
	private Sequencer player;
	private Sequence seq;
	private Track track;
	
	private int tempo;
	private int beatCounter; 	

	/**
	 * Constructor for the DaltonMidi Class.
	 */
	public DaltonMidi() {
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