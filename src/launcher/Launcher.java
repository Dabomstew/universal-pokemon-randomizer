package launcher;

import java.io.File;
import java.io.IOException;

public class Launcher {
    public static void main(String[] args) {
        try {
            ProcessBuilder pb = new ProcessBuilder("java", "-Xmx4096M", "-jar", "rando.jar");
            pb.redirectErrorStream(true);
            File log = new File("launcher-log.txt");
            pb.redirectOutput(ProcessBuilder.Redirect.to(log));
            Process p = pb.start();
            p.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
