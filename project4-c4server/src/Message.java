
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


/**
 * @author Joseph Cumbo (mooman219)
 */
public interface Message {
    
    /**
     * The stream will be flushed after send is called.
     *
     * @param out the stream to write to.
     * @throws IOException
     */
    public void send(DataOutputStream out) throws IOException;

    public static interface ToClient extends Message {

        public void apply(ConnectFour model);

        public static class Number implements ToClient {

            public static final byte MESSAGE_ID = 0;
            public final int id;

            public Number(int id) {
                this.id = id;
            }

            public Number(DataInputStream in) throws IOException {
                id = in.readInt();
            }

            @Override
            public void send(DataOutputStream out) throws IOException {
                out.writeByte(MESSAGE_ID);
                out.writeInt(id);
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

        public static class Name implements ToClient {

            public static final byte MESSAGE_ID = 1;
            public final int id;
            public final String opponentName;

            public Name(int id, String opponentName) {
                this.id = id;
                this.opponentName = opponentName;
            }

            public Name(DataInputStream in) throws IOException {
                id = in.readInt();
                opponentName = in.readUTF();
            }

            @Override
            public void send(DataOutputStream out) throws IOException {
                out.writeByte(MESSAGE_ID);
                out.writeInt(id);
                out.writeUTF(opponentName);
            }

            @Override
            public void apply(ConnectFour game) {
                game.setName(id, opponentName);
            }

            @Override
            public String toString() {
                return "Name{" + "id=" + id + ", opponentName=" + opponentName + '}';
            }
        }

        public static class Turn implements ToClient {

            public static final byte MESSAGE_ID = 2;
            public final int id;

            public Turn(int id) {
                this.id = id;
            }

            public Turn(DataInputStream in) throws IOException {
                id = in.readInt();
            }

            @Override
            public void send(DataOutputStream out) throws IOException {
                out.writeByte(MESSAGE_ID);
                out.writeInt(id);
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

        public static class Add implements ToClient {

            public static final byte MESSAGE_ID = 3;
            public final int id;
            public final int row;
            public final int col;

            public Add(int id, int r, int c) {
                this.id = id;
                this.row = r;
                this.col = c;
            }

            public Add(DataInputStream in) throws IOException {
                id = in.readInt();
                row = in.readInt();
                col = in.readInt();
            }

            @Override
            public void send(DataOutputStream out) throws IOException {
                out.writeByte(MESSAGE_ID);
                out.writeInt(id);
                out.writeInt(row);
                out.writeInt(col);
            }

            @Override
            public void apply(ConnectFour game) {
                game.addMove(id, row, col);
            }

            @Override
            public String toString() {
                return "Add{" + "id=" + id + ", r=" + row + ", c=" + col + '}';
            }
        }

        public static class Clear implements ToClient {

            public static final byte MESSAGE_ID = 4;

            @Override
            public void apply(ConnectFour game) {
                game.clearBoard();
            }

            @Override
            public String toString() {
                return "Clear{" + '}';
            }

            @Override
            public void send(DataOutputStream out) throws IOException {
                out.writeByte(MESSAGE_ID);
            }
        }
    }

    public static interface ToServer extends Message {

        public void apply(int id, ConnectFourServer.Session model);

        public static class Join implements ToServer {

            public static final byte MESSAGE_ID = 5;
            public final String name;

            public Join(String name) {
                this.name = name;
            }

            public Join(DataInputStream in) throws IOException {
                name = in.readUTF();
            }

            @Override
            public void send(DataOutputStream out) throws IOException {
                out.writeByte(MESSAGE_ID);
                out.writeUTF(name);
            }

            @Override
            public void apply(int id, ConnectFourServer.Session server) {
                server.setName(id, name);
            }

            @Override
            public String toString() {
                return "Join{" + "name=" + name + '}';
            }
        }

        public static class Add implements ToServer {

            public static final byte MESSAGE_ID = 6;
            public final int id;
            public final int col;

            public Add(int id, int c) {
                this.id = id;
                this.col = c;
            }

            public Add(DataInputStream in) throws IOException {
                id = in.readInt();
                col = in.readInt();
            }

            @Override
            public void send(DataOutputStream out) throws IOException {
                out.writeByte(MESSAGE_ID);
                out.writeInt(id);
                out.writeInt(col);
            }

            @Override
            public void apply(int id, ConnectFourServer.Session server) {
                server.makeMove(id, col);
            }

            @Override
            public String toString() {
                return "Add{" + "id=" + id + ", c=" + col + '}';
            }
        }

        public static class Clear implements ToServer {

            public static final byte MESSAGE_ID = 7;

            @Override
            public void send(DataOutputStream out) throws IOException {
                out.writeByte(MESSAGE_ID);
            }

            @Override
            public void apply(int id, ConnectFourServer.Session server) {
                server.clearBoard();
            }

            @Override
            public String toString() {
                return "Clear{" + '}';
            }
        }
    }
}
