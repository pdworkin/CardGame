// CardGameGUI.java, Paul Dworkin, July 2018

// Server for a simple card game.
// Provides a Swing GUI and a network interface for
// client player connections.
// Run this server and then run "clients" to generate
// client threads that will attach to the server.
//
// When the GUI appears, you may select the winning score, the number of
// players and the number of cards for each player.  Then press the Start
// button to play the game.  At he end of the game, it will pause again
// so you can adjust your selections.
//
// Play consists of each player playing a card in turn and the trick is
// taken by whichever card is highest in rank.  That player then leads
// the next card.
// When hands are exhausted, the player with the most tricks is the
// winner of that round and gains one point. The lead moves to the next
// player and cards are shuffled and re-dealt.
// A player wins the game when they have reached winngingScore.
//
// The current AI is very simple: If a player can beat the highest card already
// played, then play their highest card.  Otherwise play their lowest.
// The AI is in clients/ClientTask.selectCard().

// To do: 
//   - propagate quit to clients
//   - remove flickering from HTML in labels
//   - improve AI
//   - robust handling of comm errors / resyncing
//   - MVC
//   - clean
//   - the freeze at 262
//   - test edge cases
//   - num widths, grid?
//   - start/resume button
//   - remove numPlayer, etc from Gui
//   - oldTurn update
//   - button responsiveness
//   - doc
//   - resize according to parameters
//   - log4j

package com.heyho.demo.cardgame;

import java.io.IOException;
import java.io.StringReader;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.stream.Collectors;

public class Server {	
	private Deck deck = new Deck();
	private List<ServerHand> hands = new ArrayList<>();
	private List<Card> played = new ArrayList<>();
	private Map<Card, ServerHand> playedOwner = new HashMap<>();
	private int lead;
	private int dealer;
	private int oldTurn;
	private int numCards;
	private int winningScore;
	private int numPlayers;
	final static String URL = "127.0.0.1";
	final static int PORT = 4000;
	final boolean DEBUG = false;

	AtomicInteger playersAttached = new AtomicInteger(0);
	AtomicReferenceArray<String> toClient;
	AtomicReferenceArray<String> fromClient;
	
	Gui gui;
	
	Server(Gui gui, int numPlayers, int numCards, int winningScore) {
		this.gui = gui;
		this.numCards = numCards;
		this.numPlayers = numPlayers;
		this.winningScore = winningScore;
	}
	
	int getNumPlayers() {
		return numPlayers;
	}
	
	Deck getDeck() {
		return deck;
	}
	List<ServerHand> getHands() {
		return hands;
	}
	List<Card> getPlayed() {
		return played;
	}
	Map<Card, ServerHand> getPlayedOwner() {
		return playedOwner;
	}
	
	
	ServerHand getHand(int i) {
		return hands.get(i);
	}


	public void shuffleCards() {
		getDeck().setCardPointer(0);
		Random random = new Random();
		
		for (int i=0; i<getDeck().getCards().size(); i++) {
			int j = random.nextInt(getDeck().getCards().size());
			Card t = getDeck().getCardAt(i);
			getDeck().setCardAt(i, getDeck().getCardAt(j));
			getDeck().setCardAt(j, t);
		}
	}
	
	public Card dealCard() {
		return getDeck().getCards().get(getDeck().incCardPointer());			
	}

	public void dealRound() {
		if (getDeck().getCards().size() - getDeck().getCardPointer() < getNumPlayers() * numCards)
			throw new IllegalArgumentException("Not enough cards in the deck");
				
		getHands().clear();
		for (int i=0; i<getNumPlayers(); i++) {
			ServerHand hand = new ServerHand();
			getHands().add(hand);
			for (int j=0; j<numCards; j++) {
				hand.add(dealCard());
			}
			hand.sort();
		}
	}
	
	// Play out one trick of cards
	int playTrick() {
		int turn = lead;
		boolean paused;
		
		getPlayed().clear();
		getPlayedOwner().clear();

		for (int i=0; i<getNumPlayers(); i++) {
			do {
				if (gui.isQuit())
					throw new RuntimeException("Quit");
				paused = false;
				while (gui.isPaused() && !gui.isQuit())
				try {
					Thread.sleep(100);
					paused = true;
				} catch (InterruptedException e1) {
					e1.printStackTrace();
					return -1;
				}
				if (gui.isSlow() && !paused)
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					return -1;
				}
			} while (gui.isPaused() || gui.isQuit());
			
			gui.setTurnLabel(turn);
			ServerHand hand = getHand(turn);

			sendToPlayer(turn, makeCardListString(getPlayed()));
			String line = readFromPlayer(turn);
			Scanner scanner = new Scanner(new StringReader(line));
			Suit suit = Suit.valueOf(scanner.next());
			Rank rank = Rank.valueOf(scanner.next());
			scanner.close();
			
			Card finalCard = null;
			for (Card card: hand.getCards())
				if (card.suit == suit && card.rank == rank) {
					finalCard = card;
					break;
				}
					
			hand.play(finalCard);
			gui.setPlayedLabel(played);
			gui.setPlayerHandLabel(turn, hand);
			if (i==0) 
				gui.setPlayerPlayedLabel(oldTurn, null);
			else
				gui.setPlayerPlayedLabel((turn+getNumPlayers()-1)%getNumPlayers(), null);
			oldTurn = turn;
			gui.setPlayerPlayedLabel(turn, finalCard);
			
			turn = (turn+1) % getNumPlayers();			
		}
		int tmp = determineWinnerTrick(getPlayed());
		if (tmp>=0) {
			lead = tmp;
			return lead;
		}
		return -1;
	}
	
	// Who won the trick?
	int determineWinnerTrick(List<Card> played) {
//		Card winningCard = played.stream().max(Comparator.comparing(c -> c.rank)).get();
		List<Card> sortedCards = played.stream().sorted(Comparator.comparing(Card::getRank).reversed()).collect(Collectors.toList());
		if (sortedCards.size() < 2
		     || sortedCards.get(0).rank != sortedCards.get(1).rank ) {
			Card winningCard = sortedCards.get(0);
			Hand winningHand = playedOwner.get(winningCard);
		for (int i=0; i<getNumPlayers(); i++) 
			if (getHand(i) == winningHand) {
				sendToAllPlayers(Integer.toString(i));
				System.out.println("Player #" + i + " won trick with " + winningCard);
				return i;
			}
		}
		String message = "No trick winner, tied with " + sortedCards.get(0).rank + "'s";
		System.out.println(message);
		sendToAllPlayers(Integer.toString(-1));
		return -1;
	}
	
	// Play out one hand of cards
	public int playRound() {
		int winner = 0;
		
		lead = dealer;
		gui.setPlayedLabel(" ");
		
		for (int i=0; i<getNumPlayers(); i++) {
			sendToPlayer(i, makeCardListString(getHand(i).getCards()));
			gui.setPlayerHandLabel(i, getHands().get(i));
			gui.setPlayerPlayedLabel(i, null);
			gui.setPlayerTrickLabel(i, 0);
		}
		
		Map<Integer, Integer> tricks = new HashMap<>();
		for (int i=0; i<numCards; i++) {
			System.out.print("Trick #" + i + ", Lead #" + lead + ", ");
			winner = playTrick();
//			System.out.println(playedToString());
//			System.out.println(handsToString());

			if (winner >= 0) {
				tricks.put(winner, tricks.get(winner) == null? 1: tricks.get(winner)+1);
				gui.setPlayerTrickLabel(winner, tricks.get(winner));
			}
		}
		List<Integer> keys =  tricks.keySet().stream()
				.sorted(Comparator.comparing(k -> tricks.get(k)).reversed())
				.collect(Collectors.toList());
		if (keys.size() != 0 && (keys.size()==1 || tricks.get(keys.get(0)) > tricks.get(keys.get(1)))) {
			String message = "Last round winner: Player #" + (keys.get(0)+1) + " with " + 
					tricks.get(keys.get(0)) + " tricks";
			System.out.print(message);
			gui.setMessageLabel(message);
			sendToAllPlayers(Integer.toString(keys.get(0)));
			return keys.get(0);
		}
		String message = "No round winner, tied with " + (keys.size()>0? tricks.get(keys.get(0)): 0) + " tricks";
		System.out.print(message);
		gui.setMessageLabel(message);
	
		sendToAllPlayers(Integer.toString(-1));
		return -1;
	}
	
	String makeCardListString (List<Card> cards) {
		String str = "";
		for (Card card: cards)
			str += card.getSuit().toString() + " "+ card.getRank().toString() + " ";
		return str;
	}
	
	// Main entry point.  Shuffles, deals, and plays until
	// some player accumulates winniingScore points
	public int playGame() {
		dealer = 0;
		Map<Integer, Integer> score = new HashMap<>();
		int winner;
		int roundNum = 0;
		int highestScore = 0;
		
		dealer = 0;
		oldTurn = 0;
		sendToAllPlayers(Integer.toString(winningScore));
		gui.setScoreLabel(0);
		for (int i=0; i<getNumPlayers(); i++)
			gui.setPlayerScoreLabel(i, 0);

		do {			
//			System.out.println(this.deck);
			shuffleCards();
//			System.out.println("\n" + this.deck);
			dealRound();
			System.out.println(handsToString());
			
			gui.setRoundLabel(roundNum++);
			winner = playRound();

			if (winner >= 0) {
				score.put(winner, score.get(winner) == null? 1: score.get(winner)+1);
				gui.setPlayerScoreLabel(winner, score.get(winner));
				if (score.get(winner) > highestScore) {
					highestScore = score.get(winner);
					gui.setScoreLabel(highestScore);
				}
			}
			
			dealer = (dealer+1) % getNumPlayers();
			System.out.println(", Score: " + score + "\n");
	
		} while (winner < 0 || score.get(winner) < winningScore);
		String message = ("Player #" + (winner+1) + " won game");
		System.out.println(message);
		gui.setMessageLabel(message);
		
		return winner;
	}
	
	// Represents a deck of card
	private class Deck {
		ArrayList<Card> cards;
		int cardPointer = 0;
		
		Deck() {
			cards = new ArrayList<>(Suit.values().length * Rank.values().length);
			for (Suit suit: Suit.values())
				for (Rank rank: Rank.values()) {
					cards.add(new Card(suit, rank));
//					System.out.println(deck.get(i-1));
				}
				
		}
		
		ArrayList<Card> getCards() {
			return cards;
		}
		void setCards(ArrayList<Card> cards) {
			this.cards = cards;
		}
		int getCardPointer() {
			return cardPointer;
		}
		void setCardPointer(int cardPointer) {
			this.cardPointer = cardPointer;
		}
		int incCardPointer() {
			return cardPointer++;
		}
		Card getCardAt(int i) {
			return cards.get(i);
		}
		void setCardAt(int i, Card card) {
			cards.set(i, card);
		}
		
		@Override public String toString() {
			String str = "";
			for (Card card: cards) {
				str += card + ", ";
			}
			return str;
		}
	}
	
	// Represents a hand of cards
	class ServerHand extends Hand {

			void add(Card card) {
				cards.add(card);
			}
			
			void play(Card card) {
				if (!cards.contains(card))
					throw new RuntimeException("Tried to play " + card + " when it is not in hand");
				cards.remove(card);
				played.add(card);
				playedOwner.put(card, this);
								
			}
			
			void play(int hand, int cardNum) {
				play(hands.get(hand).cards.get(cardNum));
			}
	}
				
		public String handsToString() {
			String str = "";
			for (int i=0; i<getNumPlayers(); i++) {
				str += "Player #" + i + ": ";
				str += getHands().get(i) + "\n";
				
			}
			return str;
		}

		public String playedToString() {
			String str = "Played: ";
			for (Card card: getPlayed())
				str += card + ", ";
			return str;
		}
				
		// Instantiate class CardGame and then call playGame() on it.
		// playGame() will generate a deck, shuffle it, and then deal numCards to
		// each of numPlayers. 
		public static void main(String... args) {
			ServerSocket serverSocket = null;

			boolean argsOK = true;
			int numPlayers = 0;
			int numCards = 0;
			int winningScore = 0;
			int gameCount = 0;
			int gameWinner = -1;
			Gui gui;
			
			if (args.length != 3) 
				argsOK = false;
			else
				try {
					numPlayers = Integer.parseInt(args[0]);
					numCards = Integer.parseInt(args[1]);
					winningScore = Integer.parseInt(args[2]);
				} catch (NumberFormatException e) {
					argsOK = false;
				}
			
			if (!argsOK) {
//				System.out.println("Usage: Server numPlayers numCards winningScore");
				numPlayers = 4;
				numCards = 5;
				winningScore = 5;
			}
			
			    gui = new Gui(numPlayers, numCards, winningScore);
								
				Server game = new Server(gui, numPlayers, numCards, winningScore);

				try {
mainloop:       while (true) {
					if (gameWinner >= 0) {
						gui.setMessageLabel("Player #" + (gameWinner+1) + " won game");
						gui.setMessage2Label("Select your choices at right, then press Resume");
					} else
						gui.setMessageLabel("Select your choices at right, then press Resume");


					while (true) {
					gui.setIsPaused();

					gui.setNumPlayersEnabled(true);
					gui.setNumCardsEnabled(true);
					gui.setWinningScoreEnabled(true);

					while (game.gui.isPaused()) {
					if (gui.isQuit())
						throw new RuntimeException("Quit");
					try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
							break mainloop;
						}
					}

					numPlayers = gui.getNumPlayersText();
					numCards = gui.getNumCardsText();
					winningScore = gui.getWinningScoreText();

					if (numPlayers < 1 || numCards < 1 || winningScore < 1
							|| numCards*numPlayers > game.getDeck().cards.size()) {
						gui.setMessageLabelRed("Inputs are outside range.  Please  adjust them");
						gui.setMessage2Label("");
					} else
						break;
					}
					gui.setMessageLabel("");
					gui.setMessage2Label("");

					gui.setNumPlayersEnabled(false);
					gui.setNumCardsEnabled(false);
					gui.setWinningScoreEnabled(false);

					game.numPlayers = numPlayers;
					game.numCards = numCards;
					game.winningScore = winningScore;
					
					gui.updatePlayers(numPlayers, numCards, winningScore);

					game.toClient = new AtomicReferenceArray<>(numPlayers);
					game.fromClient = new AtomicReferenceArray<>(numPlayers);
					
					try {
						serverSocket = null;
			            serverSocket = new ServerSocket(PORT);
					} catch (IOException e) {
			            e.printStackTrace();
			            break mainloop;
			        }
					
					System.out.println("\nReady for " + numPlayers + " clients on port: " + PORT);
					game.playersAttached.set(0);		
					for (int i=0; i<numPlayers; i++) {
						new ServePlayer(game, serverSocket, i, numPlayers).start();
					}

					while (game.playersAttached.get() < numPlayers) {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
							break mainloop;
						}
					}
					game.playersAttached.set(0);

					
					System.out.println("Game starting...");
					gui.setGameLabel(++gameCount);
					gameWinner = game.playGame();
					game.sendToAllPlayers("QUIT");
					
					try {
						serverSocket.close();
					} catch (IOException e) {
						e.printStackTrace();
						return;
					}
				}
				} catch (RuntimeException e) {
					if (!e.getMessage().equals("Quit")) {
						e.printStackTrace();
						throw e; 
					}
		        }
				System.out.println("\nQuitting...");
				if (game.toClient != null)
					game.sendToAllPlayers("QUIT");
				gui.quit();
				try {
					if (serverSocket != null)
						serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
		}
		
		void sendToPlayer(int player, String message) {
			while (toClient.get(player) != null)
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			toClient.set(player, message);
			if (DEBUG)
				System.out.println("Send to #"+player+": "+message);
		}
	
		void sendToAllPlayers(String message) {
			for (int i=0; i<getNumPlayers(); i++)
				sendToPlayer(i, message);
			if (DEBUG)
				System.out.println("Send to all players: "+message);
		}
		
		String readFromPlayer(int player) {
			while(fromClient.get(player) == null)
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			String tmp = fromClient.get(player);
			fromClient.set(player, null);
			if (DEBUG)
				System.out.println("Read from #"+player+": "+tmp);
			return tmp;
		}
	}	

