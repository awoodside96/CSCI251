
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * @author Joseph Cumbo (jwc6999)
 */
public class Project {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        if (args.length < 2) {
            System.out.println("Not enough arguments. Correct usage: Project [file,...] [word,...]");
            return;
        }
        String[] fileNames = args[0].split(",");
        String[] words = args[1].toLowerCase().split(",");
        WordSearch search = new WordSearch();
        /**
         * Add searchable words to the searcher.
         */
        for (int i = 0; i < words.length; i++) {
            search.addWord(words[i]);
        }
        /**
         * Add valid files to the searcher.
         */
        for (int i = 0; i < fileNames.length; i++) {
            search.addFile(new File(fileNames[i]));
        }
        search.catchup();
    }

    public static final class Entry {

        public final String text;
        public final String file;

        public Entry(String text, String file) {
            this.text = text;
            this.file = file;
        }
    }

    public static final class WordSearch {

        private final LinkedBlockingQueue<Entry> entryList = new LinkedBlockingQueue<Entry>();
        private final ThreadGroup readers = new ThreadGroup("Readers");
        private final ThreadGroup searchers = new ThreadGroup("Searchers");

        public final void addFile(File file) {
            new Thread(readers, new ReadTask(file, entryList), "Reader for '" + file.getName() + "'").start();
        }

        public final void addWord(String word) {
            new Thread(searchers, new SearchTask(word, entryList), "Searcher for '" + word + "'").start();
        }

        public final void catchup() throws InterruptedException {
            while (readers.activeCount() > 0 || !entryList.isEmpty()) {
            }
            searchers.interrupt();
        }
    }

    public static final class ReadTask implements Runnable {

        private final File file;
        private final LinkedBlockingQueue<Entry> lineList;

        public ReadTask(File file, LinkedBlockingQueue<Entry> lineList) {
            this.file = file;
            this.lineList = lineList;
        }

        @Override
        public final void run() {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lineList.offer(new Entry(line.toLowerCase(), file.getName()));
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
