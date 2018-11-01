// CardGame.java, Paul Dworkin, June 2018

// Implements a simple card game.
// Takes three args: numPlayers, numCards, winningScore
//
// Instantiate class CardGame and then call playGame() on it with args
// playGame() will generate a deck, shuffle it, and then deal numCards to
// each of numPlayers. 
// Play consists of each player playing a card in turn and the trick is
// taken by whichever card is highest in rank.  That player then leads
// the next card.
// When hands are exhausted, the player with the most tricks is the
// winner of that round and gains one point. The lead moves to the next
// player and cards are shuffled and re-dealt.
// A player wins the game when they have reached winngingScore.
//
// The AI is very simple: If a player can beat the highest card already
// played, then play their highest card.  Otherwise play their lowest.
// The AI is in Hand.selectCard().

package com.heyho.demo.cardgame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Clients {
		static AtomicInteger numPlayers = new AtomicInteger(0);

		public static void main(String... args) {
			
			while(true) {
			ExecutorService es = Executors.newCachedThreadPool();
			es.submit(new ClientTask(Server.URL, Server.PORT));
			while (numPlayers.get() == 0) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
			}
			System.out.println("numplayers: "+numPlayers.get());

			for (int i=0; i<numPlayers.get()-1; i++) {
				es.submit(new ClientTask(Server.URL, Server.PORT));
			}
			es.shutdown();
			try {
				es.awaitTermination(1, TimeUnit.HOURS);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
			numPlayers.set(0);
			}
		}
}
	
class ClientTask implements Runnable {
	String url;
	int port;
	Socket socket = null;
	PrintWriter out;
	BufferedReader in;
	final boolean DEBUG = false;
	
	private int lead;
	private int dealer;
	private int numPlayers;
	private int numCards;
	private int winningScore;
	private int playerNum;
	private Hand hand;

	ClientTask(String url, int port) {
		this.url = url;
		this.port = port;
		hand = new Hand();
	}

	// Play out one trick of cards
	Card playTrick() {
		List<Card> played;
		
		played = readCardListFromServer();
		Card card = selectCard(played);
		hand.getCards().remove(card);
		sendCardToServer(card);
		return card;
	}
		
	
// The AI function
Card selectCard(List<Card> played) {
	Card highestCardPlayed =  played.stream().max(Comparator.comparing(c -> c.rank)).orElse(null);
	Card highestCardHand =  hand.getCards().stream().max(Comparator.comparing(c -> c.rank)).get();
//	System.out.println("Played:"+highestCardPlayed+" Hand:"+highestCardHand);
	if (highestCardPlayed == null ||
			highestCardHand.rank.ordinal() > highestCardPlayed.rank.ordinal()) {
		return highestCardHand;
	}
	Card lowestCardHand = hand.getCards().stream().min(Comparator.comparing(c -> c.rank)).get();
//	System.out.println("* lowest:"+lowestCardHand);
	return lowestCardHand;

}

	// Play out one hand of cards
	public int playRound() {
		Map<Integer, Integer> tricks = new HashMap<>();
		int winner;
		
		hand.setCards(readCardListFromServer());
		numCards = hand.getCards().size();

		while (hand.getCards().size() > 0) {
			playTrick();
			winner = readIntFromServer();
			if (winner >= 0)
				tricks.put(winner, tricks.get(winner) == null? 1: tricks.get(winner)+1);
		}
		winner = readIntFromServer();
		if (winner == playerNum)
			System.out.println("Player #" + playerNum + ": I win round");

		return winner;
	}
	
	// Main entry point.  Shuffles, deals, and plays until
	// some player accumulates winniingScore points
	public int playGame() {
		dealer = 0;
		Map<Integer, Integer> score = new HashMap<>();
		int winner;
		
		winningScore = readIntFromServer();
		
		do {
			winner = playRound();
			if (winner >= 0)
				score.put(winner, score.get(winner) == null? 1: score.get(winner)+1);
		} while (winner < 0 || score.get(winner) < winningScore);
	
		System.out.println("Player #" + playerNum + ": " + 
						(winner==playerNum? "I win game": "I lose game"));
		return winner;
	}
		
	int readIntFromServer() {
		String str;
		try {
			str = in.readLine();
		} catch (Exception e) {
			return -1;
		}
		log("read int:"+str);
		return Integer.parseInt(str);
	}

	List<String> readStringListFromServer() {
		List<String> list = new ArrayList<>();
		String line;
		Scanner scanner;
		
		try {
			line = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			return(null);
		}
		scanner = new Scanner(new StringReader(line));
		log("read string list line:"+line);

		while (scanner.hasNext()) {
			list.add(scanner.next());
		}
			
		scanner.close();
		log("read string list list:"+list);		
		
		return list;
	}
	
	List<Card> readCardListFromServer() {
			List<String> strList;
			List<Card> cardList = new ArrayList<>();
			
			strList = readStringListFromServer();
			for (int i=0; i<strList.size(); i+=2)
				cardList.add(new Card(Suit.valueOf(strList.get(i)), 
						Rank.valueOf(strList.get(i+1))));
			log("read card listL:"+cardList);
			return cardList;
		}

		void sendCardToServer(Card card) {
			out.println(card.getSuit().toString() + " " + card.getRank().toString());
			log("send card:"+card.getSuit().toString() + " " + card.getRank().toString());
		}
	
		void log(String str) {
			if (DEBUG)
				System.out.println("Player:"+playerNum+", "+str);
		}
		
	public void run() {		
		while(true) {
			do {
			try {
				socket = new Socket(url, port);
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
				continue;
			} catch (IOException e1) {
//				System.out.println(e1+" / "+socket);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					continue;
				}
				socket = null;
			}
			} while (socket == null);
	
			try {
				out = new PrintWriter(socket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
				if (out != null)
					out.close();
				continue;
			}
//			System.out.println("Thread "+Thread.currentThread()+" socket "+socket);			
			playerNum = readIntFromServer();
			if (playerNum < 0) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				continue;
			}
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				socket = null;
				continue;
			}
			break;
		}
			System.out.println("Client #" + playerNum + ": Connection to " 
								+ socket.getRemoteSocketAddress() 
								+ ", Local=" + socket.getLocalPort()
								+ ", " + Thread.currentThread());

			numPlayers = readIntFromServer();
			Clients.numPlayers.set(numPlayers);
			
			playGame();

			try {
				in.close();
				out.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

	}
}