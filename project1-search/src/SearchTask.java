
import java.util.HashSet;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * @author Joseph Cumbo (jwc6999)
 */
public final class SearchTask implements Runnable {

    private final String word;
    private final LinkedBlockingQueue<Entry> entryList;
    private final HashSet<String> matchedFiles = new HashSet<String>();

    /**
     * Create a new SearchTask.
     *
     * @param word the word to search for.
     * @param lineList the queue to monitor for incoming data.
     */
    public SearchTask(String word, LinkedBlockingQueue<Entry> lineList) {
        this.word = word;
        this.entryList = lineList;
    }

    @Override
    public final void run() {
        try {
            while (!Thread.interrupted()) {
                Entry line = entryList.take();
                if (!matchedFiles.contains(line.file) && line.text.contains(word)) {
                    matchedFiles.add(line.file);
                    System.out.println(word + " " + line.file);
                }
            }
        } catch (InterruptedException ex) {
            // Quietly close
        }
    }

}
