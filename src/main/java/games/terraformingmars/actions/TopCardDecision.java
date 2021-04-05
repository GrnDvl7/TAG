package games.terraformingmars.actions;

import core.AbstractGameState;
import core.actions.AbstractAction;
import core.interfaces.IExtendedSequence;
import games.terraformingmars.TMGameParameters;
import games.terraformingmars.TMGameState;
import games.terraformingmars.components.TMCard;

import java.util.ArrayList;
import java.util.List;

public class TopCardDecision extends TMAction implements IExtendedSequence {
    int stage;
    int nCardsKept;

    final int nCardsLook;
    final int nCardsKeep;
    final boolean buy;

    public TopCardDecision(int nCardsLook, int nCardsKeep, boolean buy) {
        super(-1, true);
        this.nCardsKeep = nCardsKeep;
        this.nCardsLook = nCardsLook;
        this.buy = buy;
    }

    @Override
    public boolean _execute(TMGameState gameState) {
        for (int i = 0; i < nCardsLook; i++) {
            gameState.getPlayerCardChoice()[player].add(gameState.getProjectCards().pick(0));
        }
        stage = 0;
        nCardsKept = 0;
        gameState.setActionInProgress(this);
        return true;
    }

    @Override
    public List<AbstractAction> _computeAvailableActions(AbstractGameState state) {
        // for first card in the card choice, buy it or discard it
        List<AbstractAction> actions = new ArrayList<>();
        TMGameState gs = (TMGameState) state;
        int cardId = gs.getPlayerCardChoice()[player].get(0).getComponentID();
        if (nCardsLook == 1 || nCardsKept <= nCardsKeep) {
            int cost = 0;
            if (buy) cost = ((TMGameParameters)gs.getGameParameters()).getProjectPurchaseCost();
            actions.add(new BuyCard(player, cardId, cost));
        }
        if (nCardsLook == 1 || nCardsLook - stage > nCardsKeep - nCardsKept) {
            actions.add(new DiscardCard(player, cardId));
        }
        return actions;
    }

    @Override
    public int getCurrentPlayer(AbstractGameState state) {
        return player;
    }

    @Override
    public void registerActionTaken(AbstractGameState state, AbstractAction action) {
        stage++;
        if (action instanceof BuyCard) nCardsKept++;

        if (nCardsKept == nCardsKeep && stage != nCardsLook) {
            TMGameState gs = (TMGameState) state;
            // Discard the rest
            for (TMCard card: gs.getPlayerCardChoice()[player].getComponents()) {
                new DiscardCard(player, card.getComponentID()).execute(gs);
            }
        }
    }

    @Override
    public boolean executionComplete(AbstractGameState state) {
        return nCardsKept == nCardsKeep || stage == nCardsLook;
    }

    @Override
    public TopCardDecision copy() {
        return this;
    }
}