
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @author Joseph Cumbo (jwc6999)
 */
public class Project {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Not enough arguments. Correct usage: Project [file,...] [word,...]");
            return;
        }
        String[] fileNames = args[0].split(",");
        String[] words = args[1].split(",");
        File[] files = new File[fileNames.length];
        for (int i = 0; i < fileNames.length; i++) {
            File file = new File(fileNames[i]);
            if (!file.exists()) {
                System.err.println("The file '" + fileNames[i] + "' does not exist.");
                return;
            }
            files[i] = file;
        }
        WordSearch wordSearch = new WordSearch(files, words);
    }

    public static class WordSearch {

        private final File[] files;
        private final String[] words;
        private final ExecutorService readPool = Executors.newCachedThreadPool();
        private final ExecutorService searchPool = Executors.newCachedThreadPool();

        /**
         * Creates a new WordSearch object.
         *
         * @param files the list of files to search from.
         * @param words the list of words to search in the files for.
         */
        public WordSearch(File[] files, String[] words) {
            this.files = Arrays.copyOf(files, files.length);
            this.words = Arrays.copyOf(words, words.length);
        }

        public void run() {
            for (int i = 0; i < files.length; i++) {
                FileReadTask readTask = new FileReadTask(files[i], words);
                readPool.submit(readTask);
            }
        }
    }

    public static class FileReadTask implements Runnable {

        /**
         * Each thread will make a defensive copy of the word list to prevent
         * threads from modifying the data.
         */
        private final File file;
        private final String[] words;

        public FileReadTask(File file, String[] words) {
            this.file = file;
            this.words = Arrays.copyOf(words, words.length);
        }

        @Override
        public void run() {
            try {
                List<String> lines = Files.readAllLines(file.toPath(), Charset.defaultCharset());
            } catch (IOException ex) {
                System.err.println("An error occured while reading the file '" + file.getName() + "'.");
            }
        }
    }

    public static class FileSearchTask implements Runnable {

        private final String fileName;
        private final String word;
        private final String lword;
        private final List<String> lines;

        public FileSearchTask(String fileName, String word, List<String> lines) {
            this.fileName = fileName;
            this.word = word;
            this.lword = word.toLowerCase();
            this.lines = lines;
        }

        @Override
        public void run() {
            for (String line : lines) {
                if (line.toLowerCase().contains(lword)) {
                    System.out.println(word + " " + fileName);
                    return;
                }
            }
        }
    }
}
