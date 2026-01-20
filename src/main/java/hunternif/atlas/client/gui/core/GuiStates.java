package hunternif.atlas.client.gui.core;

public class GuiStates {
    private volatile IState currentState;

    public IState current() {
        return this.currentState;
    }

    public boolean is(IState state) {
        return this.currentState == state;
    }

    public void switchTo(IState state) {
        if (this.currentState != null) {
            this.currentState.onExitState();
        }

        this.currentState = state;
        if (state != null) {
            state.onEnterState();
        }

    }

    public static class SimpleState implements IState {
        public void onEnterState() {
        }

        public void onExitState() {
        }
    }

    public interface IState {
        void onEnterState();

        void onExitState();
    }
}
