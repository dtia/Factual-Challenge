package Hangman;
import java.io.IOException;
import java.util.HashMap;

import Hangman.HangmanGame.Status;

public class PlayHangman {
	public static boolean DEBUG = false;
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String[] words = {"comaker", "monadism", "cumulate", "eruptive", "factual", "mus", "nagging", 
				"oses", "remembered", "spodumenes", "stereoisomers", "toxics", "trichromats", "triose", "uniformed"};
				
		System.out.println(playWords(words));
	}
	
	/*
	 * Play a new game of hangman for each of the words in the words array
	 */
	private static HashMap<String,Integer> playWords(String[] words) throws IOException {
		HashMap<String,Integer> wordScores = new HashMap<String,Integer>();
		
		for(int i=0; i<words.length; i++) {
			HangmanGame game = new HangmanGame(words[i], 8);
			GuessingStrategy strat = new WinningStrategy();
			wordScores.put(words[i].toUpperCase(), run(game, strat));
			System.out.println();
		}
		
		return wordScores;
	}
	
	/*
	 * Runs the strategy for the given game, then returns the score
	 */
	public static int run(HangmanGame game, GuessingStrategy strategy) {
	  while(game.gameStatus() == Status.KEEP_GUESSING) {
		  System.out.println(game);
		  Guess nextGuess = strategy.nextGuess(game);
		  if(DEBUG)
			  System.out.println(nextGuess);
		  nextGuess.makeGuess(game);
	  }
	  System.out.println(game);
	  return game.currentScore();
	}
}
