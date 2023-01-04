package cs3500.freecell.controller;

import cs3500.freecell.model.FreecellModel;
import cs3500.freecell.view.FreecellTextView;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import cs3500.freecell.model.PileType;

/**
 * Represents the controller that the user utilizes to make moves in a freecell game.
 *
 * @param <Card> represents the freecell Card the model is based off of
 */

public class SimpleFreecellController<Card> implements FreecellController<Card> {

  private FreecellModel<Card> model;
  // INVARIANT: model is non null
  private Readable rd;
  // INVARIANT: rd is non null
  private Appendable ap;
  // INVARIANT: ap is non null

  /**
   * Constructs a controller for a freecell game.
   *
   * @param model represents the model that the controller runs the game through
   * @param rd    represents a readable object that provides user input
   * @param ap    represents an appendable object that relays messages to the user
   */
  public SimpleFreecellController(FreecellModel<Card> model, Readable rd, Appendable ap) {
    if (rd == null || ap == null || model == null) {
      throw new IllegalArgumentException("Cannot be null");
    }
    this.model = model;
    this.rd = rd;
    this.ap = ap;

  }

  @Override
  public void playGame(List<Card> deck, int numCascades, int numOpens, boolean shuffle)
      throws IllegalArgumentException, IllegalStateException {
    Scanner scan = new Scanner(this.rd);
    if (deck == null || model == null) {
      throw new IllegalArgumentException("Cannot start game with empty model or deck");
    }
    FreecellTextView view = new FreecellTextView(model, ap);
    try {
      this.model.startGame(deck, numCascades, numOpens, shuffle);
    } catch (IllegalArgumentException e) {
      try {
        view.renderMessage("Could not start game.");
      } catch (IOException ioException) {
        ioException.printStackTrace();
      }
      return;
    }
    while (!model.isGameOver()) {
      PileType source;
      int sourcePileNumber;
      int cardIndex;
      PileType destination;
      int destPileNumber;
      
      try {
        view.renderBoard();
      } catch (IOException e) {
        throw new IllegalStateException("Invalid board");
      }
      try {
        view.renderMessage("\n");
      } catch (IOException e) {
        e.printStackTrace();
      }
      if (!scan.hasNext()) {
        throw new IllegalStateException("Readable complete.");
      }

      String sourceString = scan.next();
      while (!isValidPile(sourceString)) {
        try {
          view.renderMessage("\nInvalid move. Try again.");
        } catch (IOException e) {
          e.printStackTrace();
        }
        sourceString = scan.next();
      }
      
      if (isStringQuitSignal(sourceString)) {
        try {
          view.renderMessage("\nGame quit prematurely.");
        } catch (IOException e) {
          e.printStackTrace();
        }
        return;
      }
      source = getPileType(sourceString);
      sourcePileNumber = getPileNumber(sourceString) - 1;

      String cardIndexS = scan.next();
      while (!isValidCardIndex(cardIndexS)) {
        try {
          view.renderMessage("\nInvalid move. Try again.");
        } catch (IOException e) {
          e.printStackTrace();
        }
        cardIndexS = scan.next();
      }

      if (isStringQuitSignal(cardIndexS)) {
        try {
          view.renderMessage("\nGame quit prematurely.");
        } catch (IOException e) {
          e.printStackTrace();
        }
        return;
      }
      cardIndex = Integer.valueOf(cardIndexS) - 1;

      String destString = scan.next();
      while (!isValidPile(destString)) {
        try {
          view.renderMessage("\nInvalid move. Try again.");
        } catch (IOException e) {
          e.printStackTrace();
        }
        destString = scan.next();
      }

      if (isStringQuitSignal(destString)) {
        try {
          view.renderMessage("\nGame quit prematurely.");
        } catch (IOException e) {
          e.printStackTrace();
        }
        return;
      }
      destination = getPileType(destString);
      destPileNumber = getPileNumber(destString) - 1;
      
      try {
        model.move(source, sourcePileNumber, cardIndex, destination, destPileNumber);
      } catch (Exception e) {
        try {
          view.renderMessage("\nInvalid move. Try again. " + e.getMessage());
        } catch (IOException ioException) {
          ioException.printStackTrace();
        }
      }
    }
    endGame(view);
  }


  // displays game and prints game over
  private void endGame(FreecellTextView view) {
    try {
      view.renderBoard();
    } catch (IOException e) {
      e.printStackTrace();
    }
    try {
      view.renderMessage("\nGame over.");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // Returns true if the card index is valid
  private boolean isValidCardIndex(String cardIndexS) {
    return cardIndexS.equalsIgnoreCase("q") || isStringAnInt(cardIndexS);
  }

  // Returns the pile number from an input string
  private int getPileNumber(String s) {
    String pile = s.substring(1);
    return Integer.valueOf(pile);
  }

  // Returns the piletype from a given input
  private PileType getPileType(String s) {
    String pileType = s.substring(0, 1);
    switch (pileType) {
      case "O":
        return PileType.OPEN;
      case "F":
        return PileType.FOUNDATION;
      case "C":
        return PileType.CASCADE;
      default:
        return null;
    }
  }

  // Checks if a pile string indicates game should be quit
  private boolean isStringQuitSignal(String sourceString) {
    return sourceString.equalsIgnoreCase("q");
  }

  // Returns true if the input pile is valid
  private boolean isValidPile(String source) {
    if (source.equals("q") || source.equals("Q")) {
      return true;
    }
    if (source.length() < 2) {
      return false;
    }
    String pile = source.substring(0, 1);
    String pileIndex = source.substring(1);

    if (!(pile.equals("C") || pile.equals("O") || pile.equals("F"))) {
      return false;
    }
    return isStringAnInt(pileIndex);
  }

  // Returns true if the string holds an integer as a string
  private boolean isStringAnInt(String s) {
    if (s == null) {
      return false;
    }
    try {
      Scanner scan = new Scanner(s);
      int num = scan.nextInt();
    }
    catch (Exception e) {
      return false;
    }
    return true;
  }
}