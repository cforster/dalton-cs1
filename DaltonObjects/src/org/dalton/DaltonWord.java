package org.dalton;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.jeremybrooks.knicker.AccountApi;
import net.jeremybrooks.knicker.Knicker;
import net.jeremybrooks.knicker.KnickerException;
import net.jeremybrooks.knicker.WordApi;
import net.jeremybrooks.knicker.WordsApi;
import net.jeremybrooks.knicker.dto.Definition;
import net.jeremybrooks.knicker.dto.Example;
import net.jeremybrooks.knicker.dto.Phrase;
import net.jeremybrooks.knicker.dto.TokenStatus;
import net.jeremybrooks.knicker.dto.Word;


/**
 * get a random word and other information about it.
 * @author cforster
 *
 */
public class DaltonWord {
	public static void main(String[] args) {
		DaltonWord dw = new DaltonWord();
		System.out.println(dw.getWord() + ": " + dw.getDefinition());
	}

	private List<Definition> myDefs;
	private Word myWord;
	private String myExam="";
	private List<Phrase> myPhrases;
	public static long backoff;

	/**
	 * Fetch a random word from the Wordnik corpus.
	 *
	 * default values are: (true, null, null, 5, 0, 2, 0, 6, 10)
	 */
	public DaltonWord()
	{
		this(true, null, null, 5, 0, 2, 0, 6, 10);
	}

	private boolean badWord()
	{
		if(myDefs.get(0).getText().toLowerCase().contains(myWord.getWord().toLowerCase().substring(0, myWord.getWord().length()-4))) return true;
		if(myDefs.get(0).getText().toLowerCase().contains("plural")) return true;
		if(myDefs.get(0).getText().toLowerCase().contains(myWord.getWord().toLowerCase().substring(3))) return true;
		if(myWord.getWord().contains("-")) return true;
		if(myExam.equals("")) return true;
		return false;
	}

	/**
	 * Fetch a random word from the Wordnik corpus.
	 *
	 * default values are: (true, null, null, 5, 0, 2, 0, 6, 10)
	 *
	 * @param hasDictionaryDef if true, only return words with dictionary definitions.
	 * @param includePartOfSpeech part of speech values to include. If this parameter
	 *        is null, it will be ignored.
	 * @param excludePartOfSpeech part of speech values to exclude. If this parameter
	 *        is null, it will be ignored.
	 * @param minCorpusCount minimum corpus frequency for terms. If this parameter
	 *        is less than 1, it will be ignored.
	 * @param maxCorpusCount maximum corpus frequence for terms. If this parameter
	 *        is less than 1, it will be ignored.
	 * @param minDictionaryCount minimum dictionary count. If this parameter
	 *        is less than 1, it will be ignored.
	 * @param maxDictionaryCount maximum dictionary count. If this parameter
	 *        is less than 1, it will be ignored.
	 * @param minLength minimum word length. If this parameter
	 *        is less than 1, it will be ignored.
	 * @param maxLength maximum word length. If this parameter
	 *        is less than 1, it will be ignored.
	 * 
	 * @throws KnickerException if there are any errors, or if a word cannot be
	 *         found that matches the parameters.
	 */
	public DaltonWord(boolean hasDictionaryDef, Set<Knicker.PartOfSpeech> includePartOfSpeech, Set<Knicker.PartOfSpeech> excludePartOfSpeech, int minCorpusCount, int maxCorpusCount, int minDictionaryCount, int maxDictionaryCount, int minLength, int maxLength)
	{
		System.setProperty("WORDNIK_API_KEY", "ef08baad19780f8c890080e9789030b97b3ac7085a04e3974");

		//only allow a word every 4 seconds:
		while(System.currentTimeMillis()-backoff<4000) { }
		backoff = System.currentTimeMillis();

		// check the status of the API key
		TokenStatus status;
		try {
			status = AccountApi.apiTokenStatus();
			if (!status.isValid()) {
				System.out.println("API key is invalid!");
				System.exit(1);
			}

			do{ 
				myWord = WordsApi.randomWord(hasDictionaryDef, includePartOfSpeech, excludePartOfSpeech, minCorpusCount, maxCorpusCount, minDictionaryCount, maxDictionaryCount, minLength, maxLength); 
				myDefs = WordApi.definitions(myWord.getWord());
				Example ex = WordApi.topExample(myWord.getWord());
				if(ex!=null) myExam = ex.getText().replace(myWord.getWord(), "_______") + " (" + ex.getTitle().replace(myWord.getWord(), "_______") + ", " + ex.getYear() +")";
			} while(badWord());
		} catch (KnickerException e) {
			//e.printStackTrace();
			System.err.println("bad connection, simulating...");
			myWord = new Word();
			myWord.setWord("internet");
			Definition tempDef = new Definition();
			tempDef.setText("the thing that connects us all together");
			tempDef.setPartOfSpeech("noun");
			myDefs = new ArrayList<Definition>();
			myDefs.add(tempDef);
			myExam = "The ________ was invented by Al Gore";
		}

	}

	/**
	 * override the default toString function
	 * @return the word
	 */
	public String toString() {
		return getWord();
	}
	
	/**
	 * Get the word string
	 * @return the word
	 */
	public String getWord()
	{
		return myWord.getWord();
	}

	/**
	 * Get the first definition
	 * @return the definition
	 */
	public String getDefinition()
	{
		return getDefinition(0).getText();
	}

	/**
	 * get the part of speech for the first definition
	 * @return the part of speech
	 */
	public String getPartOfSpeech()
	{
		return getDefinition(0).getPartOfSpeech();
	}

	/**
	 * get the number of definitions available
	 * @return a count
	 */
	public int getDefinitionCount()
	{
		return myDefs.size();
	}

	/**
	 * get a specific definition of a word
	 * @param i get the ith defintion, starting from zero
	 * @see #getDefinitionCount()
	 * @return the Definition object of a word
	 */
	public Definition getDefinition(int i)
	{
		return myDefs.get(i%myDefs.size());
	}

	/**
	 * Get an example of the word use with the word hidden
	 * @return the example
	 */
	public String getExample()
	{
		return myExam;
	}

	/**
	 * get the definition of any word
	 * @param word the word to define
	 * @return the definition
	 */
	public static String definition(String word)
	{
		try {
			return WordApi.definitions(word.toLowerCase()).get(0).getText();
		} catch (KnickerException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return "no definition found";	
	}

	/*
	 * bad: define the syllable at the top, iterate in some meaningful way 
	 *
	public String syllable(int i) {
		try {
			return WordApi.hyphenation(myWord.getWord()).get(i).getText();
		} catch (KnickerException e) {
			// TODO Auto-generated catch block
			return "bad";
		}


	}*/

}
