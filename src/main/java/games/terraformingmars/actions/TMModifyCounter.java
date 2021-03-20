package games.terraformingmars.actions;

import core.AbstractGameState;
import core.actions.AbstractAction;
import core.components.Counter;

import java.util.Objects;

public class TMModifyCounter extends TMAction {
    public int counterID;
    public final int change;

    public TMModifyCounter(int player, int counterID, int change, boolean free) {
        super(player, free);
        this.counterID = counterID;
        this.change = change;
    }

    @Override
    public boolean execute(AbstractGameState gs) {
        Counter c = (Counter)gs.getComponentById(counterID);
        if (change > 0 && !c.isMaximum()) {
            c.increment(change);
            return super.execute(gs);
        } else if (change < 0 && !c.isMinimum()) {
            c.increment(change);
            return super.execute(gs);
        }
        super.execute(gs);
        return false;
    }

    @Override
    public AbstractAction copy() {
        return new TMModifyCounter(player, counterID, change, free);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TMModifyCounter)) return false;
        if (!super.equals(o)) return false;
        TMModifyCounter that = (TMModifyCounter) o;
        return counterID == that.counterID &&
                change == that.change;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), counterID, change);
    }

    @Override
    public String getString(AbstractGameState gameState) {
        return "Modify counter " + gameState.getComponentById(counterID).toString() + " by " + change;
    }
}