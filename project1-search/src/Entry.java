
/**
 * @author Joseph Cumbo (jwc6999) <br>
 * Base tuple to hold text and file names.
 */
public final class Entry {

    public final String text;
    public final String file;

    /**
     * Creates a new Entry.
     *
     * @param text the line of text.
     * @param file the file name associated to the line of text.
     */
    public Entry(String text, String file) {
        this.text = text;
        this.file = file;
    }

}
