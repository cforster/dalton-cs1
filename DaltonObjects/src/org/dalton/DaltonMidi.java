package org.dalton;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
/**
 * A simplified MIDI player.  Use it to create and play any song you like.
 * @author Charlie Forster
 */
public class DaltonMidi {	
	public static void main(String[] args) {
		DaltonMidi dm = new DaltonMidi();
		dm.setInstrument(10);
		for (int i = 60; i < 70; i++) {
			dm.addNote(i, 1);
		}
		dm.removeAll();
		dm.close();
	}
	
	
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
	 * add a rest in the queue.
	 * @param length number of beats the rest will last.
	 */
	public void addRest(int length) {
		beatCounter +=length*tempo;
	}

	/**
	 * add a rest in the queue.
	 */
	public void addRest() {
		this.addRest(1);
	}

	/**
	 * add a note in the queue
	 * @param note pitch of the note (between 0 and 127)
	 */
	public void addNote(int note) {
		this.addNote(note, 1);
	}

	/**
	 * add a note in the queue
	 * @param note pitch of the note (between 0 and 127)
	 * @param length number of beats the note will last
	 */
	public void addNote(int note, int length) {
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
	public void removeAll() {
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