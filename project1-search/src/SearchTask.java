
import java.util.HashSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Pattern;


/**
 * @author Joseph Cumbo (jwc6999) <br>
 * A runnable that monitors a queue for incoming data and checks if a word
 * appears in it.
 */
public final class SearchTask implements Runnable {

    private final String word;
    private final LinkedBlockingQueue<Entry> entryList;
    private final HashSet<String> matchedFiles = new HashSet<String>();
    private final Pattern matchingPattern;

    /**
     * Create a new SearchTask.
     *
     * @param word the word to search for.
     * @param lineList the queue to monitor for incoming data.
     */
    public SearchTask(String word, LinkedBlockingQueue<Entry> lineList) {
        this.word = word;
        this.entryList = lineList;
        this.matchingPattern = Pattern.compile("\\b(" + word + ")\\b");
    }

    @Override
    public final void run() {
        try {
            while (!Thread.interrupted()) {
                Entry entry = entryList.take();
                if (!matchedFiles.contains(entry.file) && matchingPattern.matcher(entry.text).find()) {
                    matchedFiles.add(entry.file);
                    System.out.println(word + " " + entry.file);
                }
            }
        } catch (InterruptedException ex) {
            // Quietly close
        }
    }

}
