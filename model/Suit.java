package cs3500.freecell.model.hw02;

/**
 * Represents a Suit object for a playing card.
 */
public enum Suit {
  DIAMOND("♦"), CLUB("♣"), SPADE("♠"), HEART("♥");
  private final String suit;

  /**
   * Creates a suit for playing cards.
   *
   * @param suit represents the suit
   */

  Suit(String suit) {
    this.suit = suit;
  }

  // returns the suit as a string
  public String getSuit() {
    return this.suit;
  }
}
