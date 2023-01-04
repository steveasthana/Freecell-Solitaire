package cs3500.freecell.model;

import cs3500.freecell.model.hw02.SimpleFreecellModel;
import cs3500.freecell.model.hw04.MultiMoveModel;

/**
 * Creates new models for freecell games.
 */

public class FreecellModelCreator {

  /**
   * Represents the different types of models for a freecell game.
   */
  public enum GameType {
    SINGLEMOVE,
    MULTIMOVE
  }


  /**
   * Creates a new freecell model depending on the GameType provided.
   *
   * @param type represents the provided GameType that will be the type of the freecell model
   *             created
   * @return a new freecell model of the type provided
   * @throws IllegalArgumentException if the given GameType is null
   */
  public static FreecellModel create(GameType type) throws IllegalArgumentException {
    if (type == null) {
      throw new IllegalArgumentException("Game type cannot be null");
    } else {
      switch (type) {
        case SINGLEMOVE:
          return new SimpleFreecellModel();
        case MULTIMOVE:
          return new MultiMoveModel();
        default:
          return null;
      }
    }
  }
}