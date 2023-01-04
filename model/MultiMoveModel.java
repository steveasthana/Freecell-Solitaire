package cs3500.freecell.model.hw04;

import cs3500.freecell.model.PileType;
import cs3500.freecell.model.hw02.Card;
import cs3500.freecell.model.hw02.SimpleFreecellModel;
import java.util.List;

/**
 * A freecell operation extending a simplefreecellmodel that allows the moving of multiple
 * cards at a time.
 */

public class MultiMoveModel extends SimpleFreecellModel {

  // moves a list of cards to the given destination
  private void moveCards(List<Card> tar, PileType dest, int destPN) {
    if (dest == PileType.FOUNDATION) {
      foundation.get(destPN).addAll(tar);
    } else if (dest == PileType.OPEN) {
      open.get(destPN).addAll(tar);
    } else if (dest == PileType.CASCADE) {
      cascade.get(destPN).addAll(tar);
    } else {
      throw new IllegalArgumentException("Invalid Move");
    }
  }
  
  // returns the list of cards being moved, or throws an exception if the source is invalid
  private List<Card> getListWanted(PileType p, int pileNumber, int cardIndex) {
    int sSize = getSize(p, pileNumber);
    if (p == PileType.OPEN && cardIndex == sSize - 1) {
      return this.open.get(pileNumber).subList(cardIndex, sSize);
    } else if (p == PileType.FOUNDATION && cardIndex == sSize - 1) {
      return this.foundation.get(pileNumber).subList(cardIndex, sSize);
    } else if (p == PileType.CASCADE && validCards(pileNumber, cardIndex)) {
      return this.cascade.get(pileNumber).subList(cardIndex, sSize);
    } else {
      throw new IllegalArgumentException("Invalid source");
    }

  }
  
  // returns the size of the given pile
  private int getSize(PileType s, int pile) {
    if (s == PileType.FOUNDATION && pile >= 0 && pile < foundation.size()) {
      return this.foundation.get(pile).size();
    } else if (s == PileType.OPEN && pile >= 0 && pile < open.size()) {
      return this.open.get(pile).size();
    } else if (s == PileType.CASCADE && pile >= 0 && pile < cascade.size()) {
      return this.cascade.get(pile).size();
    } else {
      throw new IllegalArgumentException("Invalid Source");
    }

  }

  // returns true if cards are valid to be moved from the given cascade pile
  private boolean validCards(int pileNumber, int cardIndex) {
    int cascadeSize = cascade.get(pileNumber).size();
    boolean valid = true;
    try {
      List<Card> cardList = cascade.get(pileNumber).subList(cardIndex, cascadeSize);
      for (int i = 0; i < cardList.size() - 1; i++) {
        valid = valid && !(cardList.get(i + 1).sameColor(cardList.get(i)));
      }
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid source");
    }
    return valid;
  }
  
  // checks if the pile is valid to move cards to
  private Boolean validDest(List<Card> cardList, PileType dest, int destPN) {
    Card first = cardList.get(0);
    if (dest == PileType.FOUNDATION) {
      int foundationSize = foundation.get(destPN).size();
      if (foundationSize == 0) {
        return cardList.size() == 1 && first.getValue() == 1;
      } else {
        return cardList.size() == 1
            && first.getSuit() == foundation.get(destPN).get(foundationSize - 1).getSuit()
            && first.getValue() - 1 == foundation.get(destPN).get(foundationSize - 1).getValue();
      }
    } else if (dest == PileType.OPEN) {
      return cardList.size() == 1 && open.get(destPN).size() == 0;
    } else if (dest == PileType.CASCADE) {
      int cascadeSize = cascade.get(destPN).size();
      if (cascadeSize == 0) {
        return true;
      } else {
        return !(first.sameColor(cascade.get(destPN).get(cascadeSize - 1)));
      }
    } else {
      throw new IllegalArgumentException("Invalid destination");
    }
  }
  
  @Override
  public void move(PileType sourcePile, int pileNumber, int cardIndex, PileType
      destination, int destPileNumber) {
    int emptyCas = 0;
    int emptyOpen = 0;
    List<Card> cardList = this.getListWanted(sourcePile, pileNumber, cardIndex);
    for (int i = 0; i < cascade.size(); i++) {
      if (cascade.get(i).size() == 0) {
        emptyCas = emptyCas + 1;
      }
    }
    for (int i = 0; i < open.size(); i++) {
      if (open.get(i).size() == 0) {
        emptyOpen = emptyOpen + 1;
      }
    }

    boolean validEmptyPile = cardList.size() <= ((emptyOpen + 1) * Math.pow(2, emptyCas));
    if (sourcePile == PileType.FOUNDATION && validEmptyPile
        && validDest(cardList, destination, destPileNumber)) {
      this.moveCards(cardList, destination, destPileNumber);
      foundation.get(pileNumber).remove(cardIndex);
    } else if (sourcePile == PileType.OPEN && validEmptyPile
        && validDest(cardList, destination, destPileNumber)) {
      this.moveCards(cardList, destination, destPileNumber);
      open.get(pileNumber).remove(cardIndex);
    } else if (sourcePile == PileType.CASCADE && validEmptyPile && validCards(pileNumber, cardIndex)
        && cardList.size() > 0 && validDest(cardList, destination, destPileNumber)) {
      this.moveCards(cardList, destination, destPileNumber);
      cascade.get(pileNumber).removeAll(cardList);
    } else {
      throw new IllegalArgumentException("Invalid move");
    }
  }

}