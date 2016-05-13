
import java.io.PrintWriter;

/**
 * @author Joseph Cumbo (mooman219)
 */
public abstract class OutboundMessage {

    public abstract void send(PrintWriter out);

    public static class Join extends OutboundMessage {

        private final String name;

        public Join(String name) {
            this.name = name;
        }

        @Override
        public void send(PrintWriter out) {
            out.println("join " + name);
        }

        @Override
        public String toString() {
            return "Join{" + "name=" + name + '}';
        }
    }

    public static class Add extends OutboundMessage {

        private final int id;
        private final int c;

        public Add(int id, int c) {
            this.id = id;
            this.c = c;
        }

        @Override
        public void send(PrintWriter out) {
            out.println("add " + id + " " + c);
        }

        @Override
        public String toString() {
            return "Add{" + "id=" + id + ", c=" + c + '}';
        }
    }

    public static class Clear extends OutboundMessage {

        @Override
        public void send(PrintWriter out) {
            out.println("clear");
        }

        @Override
        public String toString() {
            return "Clear{" + '}';
        }
    }
}
