
import edu.rit.pj2.Loop;
import edu.rit.pj2.Task;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Fermat - Calculates witnesses to a compound number.
 *
 * @author Joseph Cumbo (jwc6999)
 */
public class Fermat extends Task {

    public void main(String[] args) {
        if (args.length != 1) {
            System.err.println("usage: pj2 Fermat <p>: provided an incorrect number of arguments");
        } else {
            try {
                int p = Integer.parseInt(args[0]);
                if (p < 3) {
                    System.err.println("usage: pj2 Fermat <p>: p must be larger then 3");
                } else {
                    FermatRunner runner = new FermatRunner(p);
                    parallelFor(2, p - 1).exec(runner);
                    System.out.println(runner.count.get());
                }
            } catch (NumberFormatException nfe) {
                System.err.println("usage: pj2 Fermat <p>: p must be an integer");
            }
        }
    }

    public static final class FermatRunner extends Loop {

        public final AtomicInteger count = new AtomicInteger(0);
        public final BigInteger p;

        public FermatRunner(int p) {
            this.p = BigInteger.valueOf(p);
        }

        @Override
        public void run(int i) throws Exception {
            BigInteger a = BigInteger.valueOf(i);
            if (a.modPow(p, p).compareTo(a) != 0) {
                count.incrementAndGet();
            }
        }
    }
}
