package Hangman;
import java.io.IOException;
import java.util.HashMap;

import Hangman.HangmanGame.Status;

public class PlayHangman {
	public static boolean DEBUG = false;
	
	/**
	 * Play hangman for each of the words in the array 
	 */
	public static void main(String[] args) throws IOException {
		String[] words = {"comaker", "monadism", "cumulate", "eruptive", "factual", "mus", "nagging", 
				"oses", "remembered", "spodumenes", "stereoisomers", "toxics", "trichromats", "triose", "uniformed"};
		
		System.out.println("Playing hangman...");
		
		HashMap<String,Integer> scores = playWords(words);
		System.out.println(scores);
		System.out.println("Average score: " + calcAverageScore(scores));
	}
	
	/*
	 * Play a new game of hangman for each of the words in the words array
	 * Output a map of word to score
	 */
	public static HashMap<String,Integer> playWords(String[] words) throws IOException {
		HashMap<String,Integer> wordScores = new HashMap<String,Integer>();
		
		for(int i=0; i<words.length; i++) {
			HangmanGame game = new HangmanGame(words[i], 5);
			GuessingStrategy strat = new WinningStrategy();
			wordScores.put(words[i].toUpperCase(), run(game, strat));
			if(DEBUG)
				System.out.println();
		}
		
		return wordScores;
	}
	
	private static float calcAverageScore(HashMap<String,Integer> scores) {
		int sum = 0;
		int numVals = scores.keySet().size();
		for(int score : scores.values()) {
			sum += score;
		}		
		return (float)sum / numVals;
	}
	
	/*
	 * Runs the strategy for the given game, then returns the score
	 */
	public static int run(HangmanGame game, GuessingStrategy strategy) {
	  while(game.gameStatus() == Status.KEEP_GUESSING) {
		  if(DEBUG)
			  System.out.println(game);
		  Guess nextGuess = strategy.nextGuess(game);
		  if(DEBUG)
			  System.out.println(nextGuess);
		  nextGuess.makeGuess(game);
	  }
	  if(DEBUG)
		  System.out.println(game);
	  return game.currentScore();
	}
}
