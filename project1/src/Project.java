
import java.io.File;
import java.util.Arrays;
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

    }

    public static class WordSearch {

        private final File[] files;
        private final String[] words;
        private final ExecutorService readPool = Executors.newCachedThreadPool();
        private final ExecutorService searchPool = Executors.newCachedThreadPool();

        /**
         * Creates a new WordSearch object. A defensive copy is made for the
         * input parameters.
         *
         * @param files the list of files to search from.
         * @param words the list of words to search in the files for.
         */
        public WordSearch(File[] files, String[] words) {
            this.files = Arrays.copyOf(files, files.length);
            this.words = Arrays.copyOf(words, words.length);
        }

        public void run() {

        }
    }

}
