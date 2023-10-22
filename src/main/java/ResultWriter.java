import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ResultWriter {
    private static final String RESULT_FILE_PATH = "results.txt";

    public synchronized void writeDomain(String domainName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(RESULT_FILE_PATH, true))) {
            writer.println(domainName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized String getResultFilePath() {
        return RESULT_FILE_PATH;
    }

    public synchronized void clearResults() {
        try (PrintWriter writer = new PrintWriter(RESULT_FILE_PATH)) {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}