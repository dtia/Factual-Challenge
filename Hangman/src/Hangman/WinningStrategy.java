package Hangman;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class WinningStrategy implements GuessingStrategy {
	private static String fileName = "/Users/dtia/Development/eclipse workspace/factual challenge/Hangman/src/words.txt";
	
	private List<Character> mainVowels = new ArrayList<Character>(Arrays.asList('E', 'A', 'I', 'O',  'U'));
	private List<Character> consonants = new ArrayList<Character>(Arrays.asList('T', 'N', 'H', 'R', 'S', 'D', 'M', 'L', 'F', 'C', 'G', 'Y', 'P', 'W', 'B', 'V', 'K', 'J', 'X', 'Z', 'Q'));
	
	private static HashMap<Integer, List<String>> dict = new HashMap<Integer, List<String>>();
	private List<String> sameLengths = new ArrayList<String>();
	
	public WinningStrategy() throws IOException {
		loadDictionary();
	}
	
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
		
		// remove non matches from list
		removeNonMatches(game.getGuessedSoFar());
		System.out.println("same lengths size: " + sameLengths.size());		
		//System.out.println("same length words: " + sameLengths);
		// guess word if there are 3 possibilities
		if (sameLengths.size() <= 4) {
			// guess a random word
			int min = 0;
			int max = sameLengths.size()-1;
			int randIndex = min + (int)(Math.random() * ((max - min) + 1));
			String randomWord = sameLengths.remove(randIndex);			
			return new GuessWord(randomWord);
		}
		
		if (mainVowels.size() > 0) {
			// guess another vowel
			return new GuessLetter(mainVowels.remove(0));
		}		
		else {
			// guess a common letter
			return new GuessLetter(consonants.remove(0));
		}
	}
	
	private void removeNonMatches(String currentGuess) {
		List<String> potentialMatches = new ArrayList<String>();
		for(int i=0; i<sameLengths.size(); i++) {
			if(isPotentialMatch(currentGuess, sameLengths.get(i)))
				potentialMatches.add(sameLengths.get(i));
		}
		sameLengths = potentialMatches;
	}
	
	private boolean isPotentialMatch(String currentGuess, String dictWord) {
		for(int i=0; i<dictWord.length(); i++) {			
			if(currentGuess.charAt(i) != HangmanGame.MYSTERY_LETTER &&
				currentGuess.charAt(i) != dictWord.toUpperCase().charAt(i)) { 
				//System.out.println(currentGuess + " " + dictWord + " " + currentGuess.charAt(i) + " " + dictWord.toUpperCase().charAt(i));
				return false;			
			}
		}		
		return true;
	}

}
