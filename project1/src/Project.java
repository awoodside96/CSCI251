
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
        WordSearch search = new WordSearch(words);
        for (int i = 0; i < fileNames.length; i++) {
            File file = new File(fileNames[i]);
            if (!file.exists()) {
                System.out.println("The file '" + fileNames[i] + "' does not exist, skipping.");
                continue;
            }
            search.addFile(file);
        }
        search.catchup();
    }

    public static final class Line {

        public final String line;
        public final String fileName;

        public Line(String line, String fileName) {
            this.line = line;
            this.fileName = fileName;
        }
    }

    public static final class WordSearch {

        private final LinkedBlockingQueue<File> fileList = new LinkedBlockingQueue<File>();
        private final LinkedBlockingQueue<Line> lineList = new LinkedBlockingQueue<Line>();
        private final ExecutorService readPool;
        private final ExecutorService searchPool;

        /**
         * Creates a new WordSearch object.
         *
         * @param words the words to search for.
         */
        public WordSearch(String[] words) {
            this.readPool = Executors.newWorkStealingPool();
            this.searchPool = Executors.newWorkStealingPool();
            for (int i = 0; i < words.length; i++) {
                this.searchPool.submit(new SearchTask(words, lineList));
                this.readPool.submit(new ReadTask(fileList, lineList));
            }
        }

        public final void addFile(File file) {
            fileList.offer(file);
        }

        public final void catchup() throws InterruptedException {
            int iterations = 0;
            while (fileList.size() + lineList.size() > 0) {
                Thread.sleep(10);
                iterations++;
            }
            System.out.println(iterations);
//                System.out.println("Triggered " + fileList.size() + lineList.size());
        }
    }

    public static final class ReadTask implements Runnable {

        private final LinkedBlockingQueue<File> fileList;
        private final LinkedBlockingQueue<Line> lineList;

        public ReadTask(LinkedBlockingQueue<File> fileList, LinkedBlockingQueue<Line> lineList) {
            this.fileList = fileList;
            this.lineList = lineList;
        }

        @Override
        public final void run() {
            try {
                while (true) {
                    File file = fileList.take();
                    try {
                        BufferedReader reader = new BufferedReader(new FileReader(file));
                        String fileName = file.getName();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            lineList.offer(new Line(line.toLowerCase(), fileName));
                        }
                    } catch (FileNotFoundException ex) {
                        System.out.println("Unable to find file '" + file.getName() + "'.");
                    } catch (IOException ex) {
                        System.out.println("Problem while reading file file '" + file.getName() + "'.");
                    }
                }
            } catch (InterruptedException ex) {
                System.out.println("Interrupted file reader");
            }
        }
    }

    public static final class SearchTask implements Runnable {

        private final String[] words;
        private final LinkedBlockingQueue<Line> lineList;

        public SearchTask(String[] words, LinkedBlockingQueue<Line> lineList) {
            this.words = words;
            this.lineList = lineList;
        }

        @Override
        public final void run() {
            try {
                while (true) {
                    Line line = lineList.take();
                    for (int i = 0; i < words.length; i++) {
                        if (line.line.contains(words[i])) {
                            System.out.println(words[i] + " " + line.fileName);
                            continue;
                        }
                    }
                }
            } catch (InterruptedException ex) {
                System.out.println("Interrupted word searcher");
            }
        }
    }
}
