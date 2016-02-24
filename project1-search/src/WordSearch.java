
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Pattern;


/**
 * @author Joseph Cumbo (jwc6999)
 */
public final class WordSearch {

    public static final Pattern INVALID_INPUT = Pattern.compile("[^A-Za-z,]");

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        /**
         * Check if there are enough arguments.
         */
        if (args.length < 2) {
            System.err.println("Error: Not enough arguments. Correct usage: Project [file,...] [word,...]");
            return;
        }
        /**
         * Check if the word list contains invalid characters.
         */
        if (INVALID_INPUT.matcher(args[1]).find()) {
            System.err.println("Error: Invalid input for word list. Only letters A-Z are allowed.");
        }

        String[] fileNames = args[0].split(",");
        String[] words = args[1].toLowerCase().split(",");
        WordSearch search = new WordSearch();

        /**
         * Add words to the searcher.
         */
        for (int i = 0; i < words.length; i++) {
            search.addWord(words[i]);
        }

        /**
         * Add files to the searcher.
         */
        for (int i = 0; i < fileNames.length; i++) {
            File file = new File(fileNames[i]);
            /**
             * Fail if the file doesn't exist.
             */
            if (!file.exists()) {
                System.err.println("Error: Unable to find file '" + file.getName() + "'.");
                return;
            }
            search.addFile(file);
        }
        search.shutdown();
    }

    private final ConcurrentLinkedQueue<LinkedBlockingQueue<Entry>> entryLists;
    private final ArrayList<Thread> searchers;
    private final ArrayList<Thread> readers;
    private final ThreadGroup searcherGroup;
    private final ThreadGroup readerGroup;

    public WordSearch() {
        this.entryLists = new ConcurrentLinkedQueue<LinkedBlockingQueue<Entry>>();
        this.searchers = new ArrayList<Thread>();
        this.readers = new ArrayList<Thread>();
        this.searcherGroup = new ThreadGroup("Searchers");
        this.readerGroup = new ThreadGroup("Readers");
    }

    public final void addWord(String word) {
        LinkedBlockingQueue queue = new LinkedBlockingQueue<>();
        Thread thread = new Thread(searcherGroup, new SearchTask(word, queue), "Searcher for '" + word + "'");
        entryLists.add(queue);
        searchers.add(thread);
        thread.start();
    }

    public final void addFile(File file) {
        Thread thread = new Thread(readerGroup, new ReadTask(file, entryLists), "Reader for '" + file.getName() + "'");
        readers.add(thread);
        thread.start();
    }

    public final void shutdown() throws InterruptedException {
        for (Thread reader : readers) {
            reader.join();
        }
        for (LinkedBlockingQueue<Entry> entryList : entryLists) {
            while(!entryList.isEmpty()) {
                // Do nothing.
            }
        }
        searcherGroup.interrupt();
    }
}
