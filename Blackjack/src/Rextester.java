
//'main' method must be in a class 'Rextester'.
//Compiler version 1.8.0_72

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author mknight
 *
 */
class Card 
{
	public enum Suit {SPADES, CLUBS, HEARTS, DIAMONDS};
	public enum Value {
		ACE(1,"Ace"),
		TWO(2,"Two"),
		THREE(3,"Three"),
		FOUR(4,"Four"),
		FIVE(5,"Five"),
		SIX(6,"Six"),
		SEVEN(7,"Seven"),
		EIGHT(8,"Eight"),
		NINE(9,"Nine"),
		TEN(10,"Ten"),
		JACK(10,"Jack"),
		KING(10,"King"),
		QUEEN(10,"Queen");
		
		public int value;
		public String name;
		private Value (int value, String name) {
			this.value = value;
			this.name = name;
		}
	};
	
	private Suit suit;
	private Value value;
	public int newValue = 0; // for ace value change
	public boolean changed;
	public Card(Value val, Suit suit) {
		this.setHouse(suit);
		this.setValue(val);
	}
	public boolean isAce() {
		boolean bAce = false;
		bAce = this.value.toString().toLowerCase().contains("ace");
		return bAce;
	}
	@Override
	public String toString() {
		String res = "";
		res = String.format("%s of %s", this.value.toString(), this.suit.toString());
		return res;
	}
	public Value getValue() {
		return value;
	}
	public void setValue(Value value) {
		this.value = value;
	}
	public Suit getHouse() {
		return suit;
	}
	public void setHouse(Suit suit) {
		this.suit = suit;
	}	
}

class DeckOfCards 
{
	public ArrayList<Card> cards = new ArrayList<Card>();
	public DeckOfCards() {
		// full sets of each suit
		for (Card.Suit suit : Card.Suit.values()) {
			// for each value in the suit
			for (Card.Value val : Card.Value.values()) {
				Card card = new Card(val,suit);
				//System.out.println("adding " + card.toString());
				cards.add(card);
			}
		}
	}
	
	public void shuffle() {
		Collections.shuffle(cards);
	}
	
	public Card dealFromTheTop() throws Exception {
		Card topCard = null;
		int lastCardIndex = this.cards.size() - 1;
		if (lastCardIndex <= 0 ) {
			throw new Exception("No more cards");
		} else {
			topCard = cards.remove(lastCardIndex);
		}
		return topCard;
	}
	
	@Override
	public String toString() {
		String res = "";
		for (Card card : cards) {
			//System.out.println(card.toString());
			res += card.toString() + "\n";
		}
		return res;
	}
}


class Player {
	public String name;
	// the player's hand
	public ArrayList<Card> cards = new ArrayList<Card>();
	public Player(String name) {
		this.name = name;
	}
	public void giveCard(Card c) {
		cards.add(c);
	}
	
	public int cardCount() {
		int numCards = 0;
		numCards = cards.size();
		return numCards;
	}
	// change one ace's value
	public boolean changeAnAce() {
		boolean bChanged = false;
		for (Card card : this.cards) {
			if (card.isAce()) {
				if (card.getValue().value == 1) {
					card.newValue = 11; 
				} else {
					card.newValue = 1;
				}
				card.changed = true;
				bChanged = true;
				break;
			}
		}
		return bChanged;
	}
	
	public int sumHand() {
		int sum = 0;
		for (Card card : cards) {
			int val = 0 ;
			if (card.newValue == 0) {
				val = card.getValue().value;
			} else {
				val = card.newValue;
			}
			//System.out.println("Eval:" + card.toString() + " value: " + val);
			sum += val;
		}
		return sum;
	}
	
}

class Dealer extends Player {
	DeckOfCards deck;
	public Dealer () {
		super("dealer");
	}
	public Card dealCard() throws Exception {
		Card card = this.deck.dealFromTheTop();
		return card;
	}
	public boolean dealToPlayer(Player player, Card card) throws Exception {
		boolean bBust = false;
		player.giveCard(card);
		int sum = player.sumHand();
		// if the card is an ACE then change sum accordingly
		System.out.println(player.name + " got " + card.toString() + " sum: " + sum);
		// if sum > 21 then look for aces and change sum accordingly
		boolean bChanged = true;
		while ((sum > 21) && bChanged) {
			// change the value of the ace and sum again
			bChanged = player.changeAnAce();
			sum = player.sumHand();
		}
		//player.clearChanged();
		while ((sum <= 10) && bChanged) {
			// change the value of the ace and sum again
			bChanged = player.changeAnAce();
			sum = player.sumHand();
		}
		if (sum > 21) {
			bBust = true;
		}
		return bBust;
	}
	public boolean dealToSelf(Card card) throws Exception {
		return dealToPlayer(this,card);
	}
	public void setDeck(DeckOfCards deck) {
		this.deck = deck;
	}
}

class Rextester
{  
	public static enum Result {WIN,LOSE,DRAW};
	
	// processes the rules and prints results
	public static void evaluateRules(Dealer dealer, Player player) {
		int sumPlayer = 0;
		int sumDealer = 0;
		sumPlayer = player.sumHand();
		sumDealer = dealer.sumHand();
		System.out.println("Dealer score: " + sumDealer);
		// 1 - Any player whose hand consists of 5 cards without exceeding 21 beats the dealer
		if ((player.cardCount() == 5) && (sumPlayer <= 21)) {
			// player automatically wins
			System.out.println(player.name + " beats dealer with " + sumPlayer + " (5 cards < 21)");
		}
		// 2 - Any player whose hand exceeds the dealer's hand without and is less than or equal to 21 beats
		//	   the dealer
		if ((sumPlayer > sumDealer) && (sumPlayer <= 21)) {
			System.out.println(player.name + " beats dealer with " + sumPlayer);
		}
		// 3 - if player hand over 21 
		if (sumPlayer > 21) {
			System.out.println(player.name + " bust with " + sumPlayer);
		}
	}
    public static void main(String args[])
    {
        System.out.println("Hello, Doug Crawford!");

        Dealer dealer = new Dealer();
        Player Billy = new Player("Billy");
        Player Andrew = new Player("Andrew");
        Player Carla = new Player("Carla");
        
        try {
			// dealers cards
        	dealer.dealToSelf(new Card(Card.Value.JACK,Card.Suit.SPADES));
        	dealer.dealToSelf(new Card(Card.Value.SEVEN,Card.Suit.DIAMONDS));
        	
        	// Billy's cards
        	dealer.dealToPlayer(Billy,new Card(Card.Value.TWO,Card.Suit.SPADES));
			dealer.dealToPlayer(Billy,new Card(Card.Value.TWO,Card.Suit.DIAMONDS));
			dealer.dealToPlayer(Billy,new Card(Card.Value.TWO,Card.Suit.HEARTS));
			dealer.dealToPlayer(Billy,new Card(Card.Value.FOUR,Card.Suit.DIAMONDS));
			dealer.dealToPlayer(Billy,new Card(Card.Value.FIVE,Card.Suit.CLUBS));
			
			// Andrew's cards
			dealer.dealToPlayer(Andrew,new Card(Card.Value.KING,Card.Suit.DIAMONDS));
			dealer.dealToPlayer(Andrew,new Card(Card.Value.FOUR,Card.Suit.SPADES));
			dealer.dealToPlayer(Andrew,new Card(Card.Value.FOUR,Card.Suit.CLUBS));
			
			// Carla's cards
			dealer.dealToPlayer(Carla,new Card(Card.Value.QUEEN,Card.Suit.CLUBS));
			dealer.dealToPlayer(Carla,new Card(Card.Value.SIX,Card.Suit.SPADES));
			dealer.dealToPlayer(Carla,new Card(Card.Value.NINE,Card.Suit.DIAMONDS));
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        int sum = Billy.sumHand();
        System.out.println("Total: " + sum);

        // evaluate the round - each of the players in turn
        evaluateRules(dealer,Billy);
        evaluateRules(dealer,Andrew);
        evaluateRules(dealer,Carla);
        
        // Simulate a random game from a deck of cards
//      DeckOfCards deck = new DeckOfCards();
//      deck.shuffle();
//      dealer.setDeck(deck);
//      // 'Stand' or 'hit'
//      // since there is no actual user input we will select the number of 'hits' randomly.
//      // use for testing
//      Random rand = new Random();
//      int numHitsPlayer = 1; //rand.nextInt(5) + 1;
//      for (int i = 0; i < numHitsPlayer; i++) {
//      	try {
//				boolean bBust = dealer.dealToPlayer(Billy);
//				if (bBust) {
//					break;
//				}
//			} catch (Exception e) {
//				// no more cards
//				e.printStackTrace();
//				System.err.println(e);
//			}
//      }
//      
//      int numHitsDealer = rand.nextInt(5) + 1;
//      for (int i = 0; i < numHitsDealer; i++) {
//      	try {
//				dealer.dealToSelf();
//			} catch (Exception e) {
//				// no more cards
//				e.printStackTrace();
//				System.err.println(e);
//			}
//      }
    }
}
