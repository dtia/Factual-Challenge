package Hangman;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class WinningStrategy implements GuessingStrategy {
	// ***NOTE: This file path must be modified to point to the words.txt file on your machine***
	public static String fileName = "/Users/dtia/Development/eclipse workspace/factual challenge/Hangman/src/words.txt";
	
	private static char[] VOWELS = {'E', 'A', 'I', 'O', 'U'}; 
	
	// vowels and consonants arranged by letter frequency order
	private List<Character> vowels = new ArrayList<Character>(Arrays.asList('E', 'A', 'I', 'O', 'U'));
	private List<Character> consonants = new ArrayList<Character>(Arrays.asList('T', 'N', 'S', 'H', 'R', 'M', 'D', 'L', 'C', 'F', 'G', 'Y', 'P', 'W', 'B', 'V', 'K', 'J', 'X', 'Z', 'Q'));
	
	private HashMap<Integer, List<String>> dict = new HashMap<Integer, List<String>>();
	private List<String> sameLengths = new ArrayList<String>();
	
	public WinningStrategy() throws IOException {
		loadDictionary();
	}
	

	/*
	 * Read text file into dictionary
	 */
	private void loadDictionary() throws IOException {
	   BufferedReader br = new BufferedReader(new FileReader(fileName));
	    try {
	        String line = br.readLine();

	        while (line != null) {
	        	int key = line.length();
	        	List<String> words = dict.get(key);
	        	
	        	if (words != null) {
	        		words.add(line);
	        	}
	        	else {
	        		words = new ArrayList<String>();
	        		words.add(line);
	        		dict.put(key, words);	
	        	}
	        	
	            line = br.readLine();
	        }
	    } finally {
	        br.close();
	    }
	}
	
	@Override
	public Guess nextGuess(HangmanGame game) {
		// filter by length of word
		if (sameLengths.isEmpty()) {
			sameLengths = dict.get(game.getSecretWordLength());
		}
		
		// filter non matches from list
		filterNonMatches(game.getGuessedSoFar());
		
		if (PlayHangman.DEBUG) {
			System.out.println("filtered words size: " + sameLengths.size());		
			System.out.println("filtered words: " + sameLengths);
		}
				
		// guess random word if there are 4 or less possibilities
		if (sameLengths.size() <= 4) {
			int min = 0;
			int max = sameLengths.size()-1;
			int randIndex = min + (int)(Math.random() * ((max - min) + 1));
			String randomWord = sameLengths.remove(randIndex);			
			return new GuessWord(randomWord);
		}
		// start guessing vowels
		else if (vowels.size() > 0 && initialVowelLogic(game)) {
			// guess vowel in frequency order
			return new GuessLetter(vowels.remove(0));
		}
		// RE and ER are common word pairs so guessing R when there is an E
		else if (game.getCorrectlyGuessedLetters().contains('E')) {
			if (!game.getAllGuessedLetters().contains(new Character('R'))) {
				consonants.remove(new Character('R'));
				return new GuessLetter('R');
			}
		}
		// add a G if the word is ___IN_ for words like "RUNNING"
		else if (game.getGuessedSoFar().contains("IN" + HangmanGame.MYSTERY_LETTER) && 
				game.getGuessedSoFar().charAt(game.getSecretWordLength()-1) == HangmanGame.MYSTERY_LETTER) {
			if (!game.getAllGuessedLetters().contains(new Character('G'))) {
				consonants.remove(new Character('G'));
				return new GuessLetter('G');
			}
		}
		// consider whether another vowel should be added
		else if (vowels.size() > 0 && secondaryVowelLogic(game)) {
			return new GuessLetter(vowels.remove(0));
		}
		
		// guess a common letter in frequency order
		return new GuessLetter(consonants.remove(0));
	}
	
	/*
	 * Count the number of letters remaining to be guessed
	 */
	private int numMysteryLetters(String guessedSoFar) {
		int numMysteryLetters = 0;
		for(int i=0; i<guessedSoFar.length(); i++) {
			if (guessedSoFar.charAt(i) == HangmanGame.MYSTERY_LETTER)
				numMysteryLetters++;
		}
		return numMysteryLetters;
	}
	
	/*
	 * Guess a vowel if:
	 * 		1) There are less than 2 vowels guessed already AND
	 * 		2) The number of vowels guessed is less than 1/3 the length of the word
	 */
	private boolean initialVowelLogic(HangmanGame game) {
		return uniqueVowelCount(game.getCorrectlyGuessedLetters()) < 2 &&
				(float)wordVowelCount(game.getGuessedSoFar()) / game.getSecretWordLength() < .3;				
	}
	
	/*
	 * Guess a vowel if:
	 * 		1) The number of guesses left are less than 1/4 remaining guesses AND
	 * 		2) The number of missing letters is more than half the length of the word
	 */
	private boolean secondaryVowelLogic(HangmanGame game) {
		return game.numWrongGuessesRemaining() < 0.25 * game.getMaxWrongGuesses() &&
				numMysteryLetters(game.getGuessedSoFar()) / game.getSecretWordLength() > 0.5;
	}
	
	/*
	 * Count how many vowels are in the currently guessed word
	 */
	private int wordVowelCount(String guessedSoFar) {
		int vowelCount = 0;
		for (int i=0; i<guessedSoFar.length(); i++) {
			if (isVowel(guessedSoFar.charAt(i))) 
				vowelCount++;
		}
		return vowelCount;
	}
	
	/*
	 * Determine if this letter is a vowel
	 */
	private boolean isVowel(char letter) {
		boolean isVowel = false;
		for(int i=0; i<VOWELS.length; i++) {
			if(VOWELS[i] == letter)
				isVowel = true;
		}
		return isVowel;
	}
	
	/*
	 * Determine how many unique vowels are in the set of letters
	 */
	private int uniqueVowelCount(Set<Character> letters) {
		int vowelCount = 0;
		if(letters.contains('A'))
			vowelCount++;
		if(letters.contains('E'))
			vowelCount++;
		if(letters.contains('I'))
			vowelCount++;
		if(letters.contains('O'))
			vowelCount++;
		if(letters.contains('U'))
			vowelCount++;
		return vowelCount;
	}
	
	/*
	 * Filter words that don't have the same letters in the same positions
	 */
	private void filterNonMatches(String currentGuess) {
		List<String> potentialMatches = new ArrayList<String>();
		for(int i=0; i<sameLengths.size(); i++) {
			if(isPotentialMatch(currentGuess, sameLengths.get(i)))
				potentialMatches.add(sameLengths.get(i));
		}
		sameLengths = potentialMatches;
	}
	
	/*
	 * Determine if the guessed word has the same letters in the same positions
	 */
	private boolean isPotentialMatch(String currentGuess, String dictWord) {
		for(int i=0; i<dictWord.length(); i++) {			
			if(currentGuess.charAt(i) != HangmanGame.MYSTERY_LETTER &&
				currentGuess.charAt(i) != dictWord.toUpperCase().charAt(i)) { 
				return false;			
			}
		}		
		return true;
	}	
}