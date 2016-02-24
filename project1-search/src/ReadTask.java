
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * @author Joseph Cumbo (jwc6999) <br>
 * A runnable that reads files and pushes the contents into a queue to be
 * processed.
 */
public final class ReadTask implements Runnable {

    private final File file;
    private final ConcurrentLinkedQueue<LinkedBlockingQueue<Entry>> entryLists;

    /**
     * Creates a new ReadTask.
     *
     * @param file the file to read from.
     * @param entryLists the list of queue's to send the parsed data into.
     */
    public ReadTask(File file, ConcurrentLinkedQueue<LinkedBlockingQueue<Entry>> entryLists) {
        this.file = file;
        this.entryLists = entryLists;
    }

    @Override
    public final void run() {
        try (final BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            /**
             * Scan through the file line by line.
             */
            while ((line = reader.readLine()) != null) {
                /**
                 * Send the line to each searcher to be compared.
                 */
                Entry entry = new Entry(line.toLowerCase(), file.getName());
                for (LinkedBlockingQueue<Entry> entryList : entryLists) {
                    entryList.offer(entry);
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Unable to find file '" + file.getName() + "', skipping.");
        } catch (IOException ex) {
            System.out.println("Problem while reading file '" + file.getName() + "', skipping.");
        }
    }

}
