
/**
 * @author Joseph Cumbo (mooman219)
 */
public abstract class InboundMessage {

    public abstract void apply(ConnectFour game);

    public static class Number extends InboundMessage {

        private final int id;

        public Number(int id) {
            this.id = id;
        }

        @Override
        public void apply(ConnectFour game) {
            game.setId(id);
        }

        @Override
        public String toString() {
            return "Number{" + "id=" + id + '}';
        }
    }

    public static class Name extends InboundMessage {

        private final int id;
        private final String opponentName;

        public Name(int id, String opponentName) {
            this.id = id;
            this.opponentName = opponentName;
        }

        @Override
        public void apply(ConnectFour game) {
            if (game.getId() != id) {
                game.setOpponentid(id);
                game.setOpponentname(opponentName);
            }
        }

        @Override
        public String toString() {
            return "Name{" + "id=" + id + ", opponentName=" + opponentName + '}';
        }
    }

    public static class Turn extends InboundMessage {

        private final int id;

        public Turn(int id) {
            this.id = id;
        }

        @Override
        public void apply(ConnectFour game) {
            game.setTurn(id);
        }

        @Override
        public String toString() {
            return "Turn{" + "id=" + id + '}';
        }
    }

    public static class Add extends InboundMessage {

        private final int id;
        private final int r;
        private final int c;

        public Add(int id, int r, int c) {
            this.id = id;
            this.r = r;
            this.c = c;
        }

        @Override
        public void apply(ConnectFour game) {
            game.addMove(id, r, c);
        }

        @Override
        public String toString() {
            return "Add{" + "id=" + id + ", r=" + r + ", c=" + c + '}';
        }
    }

    public static class Clear extends InboundMessage {

        @Override
        public void apply(ConnectFour game) {
            game.clearBoard();
        }

        @Override
        public String toString() {
            return "Clear{" + '}';
        }
    }
}
