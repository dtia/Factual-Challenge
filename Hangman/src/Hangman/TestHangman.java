package Hangman;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

public class TestHangman {
	/*
	 * I was considering adding tests for strings with special characters and
	 * non-string input, but the premise as stated in the directions is that
	 * all the words will come from the dictionary file. 
	 */

	/*
	 * Assert that score is 0 for an empty word
	 */
	@Test
	public void testEmptyWord() throws IOException {
		HangmanGame game = new HangmanGame("", 1);
		GuessingStrategy strat = new WinningStrategy();		
		int score = PlayHangman.run(game, strat);
		Assert.assertTrue(score == 0);
	}
}
