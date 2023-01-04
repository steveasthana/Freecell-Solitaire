package cs3500.freecell.model.hw02;

import cs3500.freecell.model.FreecellModel;
import cs3500.freecell.model.PileType;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Represents a model that contains the rules and workings of a freecell game.
 */
public class SimpleFreecellModel implements FreecellModel<Card> {

  protected ArrayList<List<Card>> open;
  protected ArrayList<List<Card>> cascade;
  protected ArrayList<List<Card>> foundation;
  protected boolean gameStarted;

  /**
   * Constructs a Simple Freecell Model.
   */

  public SimpleFreecellModel() {
    this.open = new ArrayList<List<Card>>();
    this.cascade = new ArrayList<List<Card>>();
    this.foundation = new ArrayList<List<Card>>();
    this.gameStarted = false;

  }

  // returns a new deck of playing cards
  @Override
  public List<Card> getDeck() {
    List<Card> deck = new ArrayList<Card>();
    for (Suit i : Suit.values()) {
      for (int k = 1; k <= 13; k++) {
        deck.add(new Card(k, i));
      }
    }
    return deck;
  }

  // deals the cascade piles with the given deck into the amount of piles specified
  private void dealCascade(int pileAmount, List<Card> deck) {
    for (int k = 0; k < pileAmount; k++) {
      this.cascade.add(new ArrayList<Card>());
    }
    int cascadePile = 0;
    for (Card card : deck) {
      this.cascade.get(cascadePile).add(card);
      if (cascadePile == pileAmount - 1) {
        cascadePile = 0;
      } else {
        cascadePile++;
      }
    }
  }


  // returns true if the given deck does have duplicate cards
  private boolean areDuplicates(List<Card> deck) {
    boolean duplicates = false;
    for (int i = 0; i < deck.size(); i++) {
      for (int k = i + 1; k < deck.size(); k++) {
        if (deck.get(i).equals(deck.get(k))) {
          duplicates = true;
          break;
        }
      }
    }
    return duplicates;
  }

  // returns false if the deck contains an invalid card
  private boolean validCardDeck(List<Card> deck) {
    boolean validity = true;
    for (Card c : deck) {
      if (!c.cardValid()) {
        validity = false;
        break;
      }
    }
    return validity;
  }

  // starts a game of Freecell by dealing out cascade piles, initializing the number of open piles,
  // and shuffling the deck if necessary
  @Override
  public void startGame(List<Card> deck, int numCascadePiles, int numOpenPiles, boolean shuffle)
      throws IllegalArgumentException {
    if (deck == null || deck.size() != 52 || areDuplicates(deck) || !validCardDeck(deck)) {
      throw new IllegalArgumentException("Invalid deck");
    }
    if (numCascadePiles < 4) {
      throw new IllegalArgumentException("There must be over 4 cascade piles");
    }
    if (numOpenPiles < 1) {
      throw new IllegalArgumentException("There must be over 1 open pile");
    }
    if (this.areDuplicates(deck)) {
      throw new IllegalArgumentException("Deck contains duplicate cards");
    }
    if (!this.validCardDeck(deck)) {
      throw new IllegalArgumentException("Invalid card in deck");
    }
    if (gameStarted) {
      this.open = new ArrayList<List<Card>>();
      this.foundation = new ArrayList<List<Card>>();
      this.cascade = new ArrayList<List<Card>>();
    }
    if (shuffle) {
      Collections.shuffle(deck);
    }
    this.dealCascade(numCascadePiles, deck);
    for (int i = 0; i < numOpenPiles; i++) {
      this.open.add(new ArrayList<Card>());
    }
    for (int k = 0; k < 4; k++) {
      this.foundation.add(new ArrayList<Card>());
    }
    this.gameStarted = true;

  }

  // returns a card that is desired
  private Card cardFromSource(PileType source, int pileNumber, int cardIndex) {
    Card cardWanted = null;
    switch (source) {
      case FOUNDATION:
        throw new IllegalArgumentException("Cannot move cards from foundation pile.");
      case CASCADE:
        if (cardIndex != this.cascade.get(pileNumber).size() - 1) {
          throw new IllegalArgumentException("Can only move last card in a cascade pile");
        } else {
          cardWanted = this.getCascadeCardAt(pileNumber, cardIndex);
        }
        break;
      case OPEN:
        cardWanted = this.getOpenCardAt(pileNumber);
        break;
      default:
        cardWanted = null;
    }
    return cardWanted;
  }

  // returns true if a card can be placed in a cascade pile
  private boolean cascadeValid(Card toMove, int pileNumber) {
    Card before = this.getCascadeCardAt(pileNumber, cascade.get(pileNumber).size() - 1);
    return !toMove.sameColor(before) && (before.getValue() - toMove.getValue() == 1);
  }

  // returns the index of foundation pile based on a given card
  private boolean foundationValid(Card card, int pile) {
    if (foundation.get(pile).size() == 0 && card.value == 1) {
      return true;
    }
    if (card.value > 1) {
      return (foundation.get(pile).size() + 1 == card.value && (
          foundation.get(pile).get(card.value - 2).value == card.value - 1));
    } else {
      return false;
    }
  }

  // removes the given card from the pile type given
  private void removeFrom(Card card, PileType pile, int pileNumber) {
    switch (pile) {
      case OPEN:
        open.get(pileNumber).remove(card);
        break;
      case CASCADE:
        cascade.get(pileNumber).remove(card);
        break;
      default:
        throw new IllegalArgumentException("Invalid pile type");
    }
  }

  // carries out a move based on the directions provided
  @Override
  public void move(PileType source, int pileNumber, int cardIndex, PileType destination,
      int destPileNumber)
      throws IllegalArgumentException, IllegalStateException {
    if (!gameStarted) {
      throw new IllegalStateException("Game has yet to start");
    }
    Card toMove = this.cardFromSource(source, pileNumber, cardIndex);
    switch (destination) {
      case OPEN:
        if (this.open.get(destPileNumber).size() == 1) {
          throw new IllegalArgumentException("Open pile is full");
        } else {
          this.open.get(destPileNumber).add(toMove);
        }
        break;
      case CASCADE:
        if (!this.cascadeValid(toMove, destPileNumber)) {
          throw new IllegalArgumentException("Move is not valid");
        } else {
          this.cascade.get(destPileNumber).add(toMove);
        }
        break;
      case FOUNDATION:
        if (foundationValid(toMove, destPileNumber)) {
          this.foundation.get(destPileNumber).add(toMove);
        } else {
          throw new IllegalArgumentException("Move is not valid");
        }
        break;
      default:
        throw new IllegalArgumentException("Invalid pile type");
    }
    this.removeFrom(toMove, source, pileNumber);
  }

  // returns true if the game is over, or all foundation piles are full
  @Override
  public boolean isGameOver() {
    return this.foundation.get(0).size() == 13 && this.foundation.get(1).size() == 13
        && this.foundation.get(2).size() == 13 && this.foundation.get(3).size() == 13;
  }

  // returns the number of cards in the given foundation pile
  // throws exceptions for invalid indices and if the game has yet to start
  @Override
  public int getNumCardsInFoundationPile(int index)
      throws IllegalArgumentException, IllegalStateException {
    if (!this.gameStarted) {
      throw new IllegalStateException("Game has not yet started");
    }
    if (index < 0 || index > 3) {
      throw new IllegalArgumentException("Index must be between 0 and 3");
    } else {
      return foundation.get(index).size();
    }
  }

  // returns the number of cascade piles in the game or -1 if the game has yet to start
  @Override
  public int getNumCascadePiles() {
    if (!gameStarted) {
      return -1;
    } else {
      return cascade.size();
    }
  }

  // returns the number of cards in the given cascade pile
  // throws exceptions when the given index is out of range or the game has yet to start
  @Override
  public int getNumCardsInCascadePile(int index)
      throws IllegalArgumentException, IllegalStateException {
    if (!gameStarted) {
      throw new IllegalStateException("Game has not yet started");
    }
    if (index < 0 || index > (cascade.size() - 1)) {
      throw new IllegalArgumentException("Index is out of range");
    } else {
      return cascade.get(index).size();
    }
  }

  // returns the number of cards in the given open pile
  // throws exceptions when the given index is out f range or the game has yet to start
  @Override
  public int getNumCardsInOpenPile(int index)
      throws IllegalArgumentException, IllegalStateException {
    if (!gameStarted) {
      throw new IllegalStateException("Game has not yet started");
    }
    if (index < 0 || index > (open.size() - 1)) {
      throw new IllegalArgumentException("Index is out of range");
    } else {
      return this.open.get(index).size();
    }
  }

  // returns the total number of cards in the open piles
  private int numOpenCards() {
    int cards = 0;
    for (int i = 0; i < this.open.size(); i++) {
      cards += getNumCardsInOpenPile(i);
    }
    return cards;
  }

  // returns the number of open piles in the game, or -1 if the game has yet to start
  @Override
  public int getNumOpenPiles() {
    if (!gameStarted) {
      return -1;
    } else {
      return this.open.size();
    }
  }

  // gets the card at the provided index of the provided foundation pile
  @Override
  public Card getFoundationCardAt(int pileIndex, int cardIndex)
      throws IllegalArgumentException, IllegalStateException {
    if (!gameStarted) {
      throw new IllegalStateException("Game has not yet started");
    }
    if (pileIndex < 0 || pileIndex > (this.foundation.size() - 1)) {
      throw new IllegalArgumentException("Pile index is out of range");
    }
    if (cardIndex < 0 || cardIndex > (this.foundation.get(pileIndex).size() - 1)) {
      throw new IllegalArgumentException("Card index is out of range");
    } else {
      return this.foundation.get(pileIndex).get(cardIndex);
    }
  }

  // gets the card at the provided index in the provided cascade pile
  @Override
  public Card getCascadeCardAt(int pileIndex, int cardIndex)
      throws IllegalArgumentException, IllegalStateException {
    if (!this.gameStarted) {
      throw new IllegalStateException("Game has not yet started");
    }
    if (pileIndex < 0 || pileIndex > (this.cascade.size() - 1)) {
      throw new IllegalArgumentException("Pile index is out of range");
    }
    if (cardIndex < 0 || cardIndex > (this.cascade.get(pileIndex).size() - 1)) {
      throw new IllegalArgumentException("Card index is out of range");
    }
    if (this.cascade.size() == 0) {
      throw new IllegalStateException("Game has not yet started");
    } else {
      return this.cascade.get(pileIndex).get(cardIndex);
    }
  }

  // gets the card from the provided open pile
  @Override
  public Card getOpenCardAt(int pileIndex) {
    if (!this.gameStarted) {
      throw new IllegalStateException("Game has not yet started");
    }
    if (pileIndex < 0 || pileIndex > (this.open.size() - 1)) {
      throw new IllegalArgumentException("Index is out of range");
    }
    if (this.open.get(pileIndex).size() == 0) {
      return null;
    } else {
      return this.open.get(pileIndex).get(0);
    }
  }
}