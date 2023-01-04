package cs3500.freecell.model.hw02;

import java.util.Objects;

/**
 * Represents a Playing Card object.
 */
public class Card {

  protected final int value;
  protected final Suit suit;

  /**
   * Constructs a Card.
   *
   * @param value represents the value of a Card
   * @param suit  represents the suit of a Card
   */

  public Card(int value, Suit suit) {
    if (value > 13 || value < 1) {
      throw new IllegalArgumentException("Card value can only be between 1 and 13");
    }
    this.value = value;
    this.suit = Objects.requireNonNull(suit);
  }

  /**
   * checks if the given card is valid.
   * @return true if valid
   */
  public boolean cardValid() {
    boolean val = false;
    for (int i = 1; i < 14; i++) {
      if (this.value == i)  {
        val = true;
        break;
      }
    }
    boolean suit = false;
    for (Suit s : Suit.values()) {
      if (this.suit.equals(s)) {
        suit = true;
        break;
      }
    }
    return val && suit;
  }

  // returns the suit of the card
  public Suit getSuit() {
    return this.suit;
  }

  // returns the value of the card as an integer
  public int getValue() {
    return this.value;
  }

  // returns the numerical value of the card as a String
  private String getValueString() {
    String val = "";
    if (this.value > 1 && this.value < 11) {
      return Integer.toString(this.value);
    }
    else {
      switch (this.value) {
        case 1:
          return "A";
        case 11:
          return "J";
        case 12:
          return "Q";
        case 13:
          return "K";
        default:
          throw new IllegalArgumentException("Invalid value");
      }
    }
  }

  // returns the card as a string
  public String toString() {
    return this.getValueString() + this.suit.getSuit();
  }

  // returns true if that card is the same color as this card
  public boolean sameColor(Card card) {
    return (this.suit == Suit.CLUB || this.suit == Suit.SPADE) == (card.suit == Suit.CLUB
        || card.suit == Suit.SPADE);
  }
}
