
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
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
        WordSearch search = new WordSearch(words);

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
        search.catchup();
    }

    private final LinkedBlockingQueue<Entry>[] entryLists;
    private final ArrayList<Thread> searchers = new ArrayList<Thread>();
    private final ArrayList<Thread> readers = new ArrayList<Thread>();
    private final ThreadGroup searcherGroup = new ThreadGroup("Searchers");
    private final ThreadGroup readerGroup = new ThreadGroup("Readers");

    public WordSearch(String[] words) {
        /**
         * Create a queue for each searcher thread.
         */
        this.entryLists = new LinkedBlockingQueue[words.length];
        for (int i = 0; i < words.length; i++) {
            entryLists[i] = new LinkedBlockingQueue<>();
            Thread thread = new Thread(searcherGroup, new SearchTask(words[i], entryLists[i]), "Searcher for '" + words[i] + "'");
            searchers.add(thread);
        }
        /**
         * Start the searcher threads.
         */
        for (Thread thread : searchers) {
            thread.start();
        }
    }

    public final void addFile(File file) {
        Thread thread = new Thread(readerGroup, new ReadTask(file, entryLists), "Reader for '" + file.getName() + "'");
        readers.add(thread);
        thread.start();
    }

    public final void catchup() throws InterruptedException {
        while (readerGroup.activeCount() > 0 || !entryList.isEmpty()) {
        }
        searcherGroup.interrupt();
    }

    public static final class Entry {

        public final String text;
        public final String file;

        public Entry(String text, String file) {
            this.text = text;
            this.file = file;
        }
    }

    public static final class ReadTask implements Runnable {

        private final File file;
        private final LinkedBlockingQueue<Entry>[] entryLists;

        public ReadTask(File file, LinkedBlockingQueue<Entry>[] entryLists) {
            this.file = file;
            this.entryLists = entryLists;
        }

        @Override
        public final void run() {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                /**
                 * Scan through the file line by line.
                 */
                while ((line = reader.readLine()) != null) {
                    /**
                     * Send the line to each searcher to be compared.
                     */
                    for (int i = 0; i < entryLists.length; i++) {
                        entryLists[i].offer(new Entry(line.toLowerCase(), file.getName()));
                    }
                }
            } catch (FileNotFoundException ex) {
                System.out.println("Unable to find file '" + file.getName() + "', skipping.");
            } catch (IOException ex) {
                System.out.println("Problem while reading file '" + file.getName() + "', skipping.");
            }
        }
    }

    public static final class SearchTask implements Runnable {

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
}
