package cs3500.freecell.view;

import cs3500.freecell.model.FreecellModel;
import java.io.IOException;

/**
 * Represents information from a freecell game through text, including displaying the
 * game status and messages to the user.
 */

public class FreecellTextView implements FreecellView {

  private final FreecellModel<?> model;
  // INVARIANT: model is non null
  private final Appendable ap;

  /**
   * Represents a view of a freecell game through text.
   *
   * @param model represents the model that the game is run through
   * @param ap represents an appendable object that relays messages to the user
   * @param <K> represents the object that is used to play the freecell game
   */

  public <K> FreecellTextView(FreecellModel<K> model, Appendable ap) {
    if (model == null) {
      throw new IllegalArgumentException("Model cannot be null");
    }
    this.model = model;
    this.ap = ap;
  }

  /**
   * Represents a model that a freecell game is run through with an appendable set as null.
   *
   * @param model represents the model that the game is run through
   */
  public FreecellTextView(FreecellModel<?> model) {
    if (model == null) {
      throw new IllegalArgumentException("Model cannot be null");
    }

    this.model = model;
    this.ap = null;
  }

  
  @Override
  public String toString() {
    if (this.model.getNumCascadePiles() == -1) {
      return "";
    }
    String gameDisplay = "";
    for (int i = 0; i < 4; ++i) {
      if (i != 0) {
        gameDisplay += "\n";
      }
      gameDisplay += "F" + (i + 1) + ":";
      if (model.getNumCardsInFoundationPile(i) != 0) {
        gameDisplay += " ";
      }
      for (int k = 0; k < model.getNumCardsInFoundationPile(i); ++k) {
        gameDisplay += model.getFoundationCardAt(i, k).toString();
        if (k < model.getNumCardsInCascadePile(i) - 1) {
          gameDisplay += ", ";
        }
      }
    }
    gameDisplay += "\n";
    for (int i = 0; i < model.getNumOpenPiles(); ++i) {
      if (i != 0) {
        gameDisplay += "\n";
      }
      gameDisplay += "O" + (i + 1) + ":";
      if (model.getNumCardsInOpenPile(i) != 0) {
        gameDisplay += " ";
      }
      for (int k = 0; k < model.getNumCardsInOpenPile(i); ++k) {
        gameDisplay += model.getOpenCardAt(i).toString();
        if (k < model.getNumCardsInOpenPile(i) - 1) {
          gameDisplay += ", ";
        }
      }
    }
    gameDisplay += "\n";
    for (int i = 0; i < model.getNumCascadePiles(); ++i) {
      if (i != 0) {
        gameDisplay += "\n";
      }
      gameDisplay += "C" + (i + 1) + ":";
      if (model.getNumCardsInCascadePile(i) != 0) {
        gameDisplay += " ";
      }
      for (int k = 0; k < model.getNumCardsInCascadePile(i); ++k) {
        gameDisplay += model.getCascadeCardAt(i, k).toString();
        if (k < model.getNumCardsInCascadePile(i) - 1) {
          gameDisplay += ", ";
        }
      }
    }
    return gameDisplay;
  }

  @Override
  public void renderBoard() throws IOException {
    try {
      ap.append(this.toString());
      ap.append("\n");
    } catch (IOException e) {
      throw new IOException("Illegal input received");
    }
  }

  @Override
  public void renderMessage(String message) throws IOException {
    try {
      this.ap.append(message);
    } catch (IOException e) {
      throw new IOException("Couldn't write to appendable");
    }
  }
}

