package com.danlogan.pegsandjokers.domain;

import java.util.ArrayList;
import java.util.Random;

public class DeckOfCards {
	
	private ArrayList<Card> cards = new ArrayList<Card>();

	public DeckOfCards() {
		
		for(Suit suit : Suit.values())
		{
			for(CardRank rank : CardRank.values()) {
				Card card = new Card(rank, suit);
				cards.add(card);
			}
		}
		
		cards.add(new Joker());
		cards.add(new Joker());
	}
	
	public int cardsRemaining() {
		return cards.size();
	}
	
	public DeckOfCards combineDecks(DeckOfCards deck) {
		
		this.cards.addAll(deck.cards);
		return this;
	
	}
	
	public Card draw()
	{
		return cards.remove(0);
	}
	
	//shuffle the deck and return a reference to newly shuffled self
	public DeckOfCards shuffle()
	{
        
        //shuffle through the deck 3 times
       for(int shuffle=1;shuffle<4;shuffle++)
       {

    	   Random rand = new Random(); 

    	   int n = cards.size();

    	   for (int i = 0; i < n; i++) 
    	   { 
    		   // Random for remaining positions. 
    		   int r = i + rand.nextInt(n - i); 

    		   //swapping the elements 
    		   Card temp = cards.get(r); 
    		   cards.set(r, cards.get(i)); 
    		   cards.set(i,temp); 

    	   } 
       }

       return this;
 
	}

}
