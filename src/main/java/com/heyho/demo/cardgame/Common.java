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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

	enum Suit {
		CLUBS("\u2663"), DIAMONDS("<font color='red'>\u2666</font>"), 
		HEARTS("<font color='red'>'\u2764</font>"), SPADES("\u2660");

		String shortName;
		
		Suit(String shortName) {
			this.shortName = shortName;
		}
		
		String getShort() {
			return shortName;
		}
	}
	enum Rank {
		TWO("2"), THREE("3"), FOUR("4"), FIVE("5"), SIX("6"), 
		SEVEN("7"), EIGHT("8"), NINE("9"), TEN("10"), JACK("J"),
		QUEEN("Q"), KING("K"), ACE("A");
		
		String shortName;
		
		Rank(String shortName) {
			this.shortName = shortName;
		}
		
		String getShort() {
			return shortName;
		}
	}
	
	// Represents one card
	class Card implements Comparable<Card>{
		Suit suit;
		Rank rank;
		
		Card(Suit suit, Rank rank) {
			this.suit = suit;
			this.rank = rank;
		}
		
		Suit getSuit() {
			return suit;
		}
		Rank getRank() {
			return rank;
		}
		
		@Override public String toString() {
			return "" + rank + " of " + suit;
		}

		public String toStringShort() {
			return this.getRank().getShort() + this.getSuit().getShort();			
		}
		
		@Override
		public int compareTo(Card other) {
			int i;
			if ((i=other.rank.ordinal() - rank.ordinal()) != 0)
				return i;
			return other.suit.ordinal() - suit.ordinal();
		}
		
		@Override
		public boolean equals(Object other) {
			if (other==null)
				return false;
			if (!(other instanceof Card))
				return false;
			Card card = (Card) other;
			return this.suit == card.suit && this.rank == card.rank;
		}

		@Override
	    public int hashCode() {
			return this.suit.ordinal() * this.rank.ordinal();
		}
	}
	
	
	// Represents a hand of cards
	class Hand {
			List<Card> cards = new ArrayList<>();
	
			Hand sort() {	
				Collections.sort(cards);
				return this;
			}
			
			protected List<Card> getCards() {
				return cards;
			}
	
			void setCards(List<Card> cards) {
				this.cards = cards;
			}
			
			@Override public String toString() {
				String str = "[";
				for (Card card: cards)
					str += card + ", ";
				str += "]";
				return str;
			}
		}
