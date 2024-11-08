import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

  public static void main(String[] args) throws IOException {

    List<String> images = List.of(
      "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/" +
        "pokemon/other/dream-world/1.svg",
      "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/" +
        "pokemon/other/dream-world/2.svg",
      "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/" +
        "pokemon/other/dream-world/3.svg"
    );

    Path dir = Path.of("download");
//    Files.deleteIfExists(dir);
//    Files.createDirectory(dir);

    ExecutorService executorService = Executors.newCachedThreadPool();

    for (int i = 0; i < images.size(); i++) {

      final int I = i;
      executorService.submit(() -> downloadFile(images.get(I), dir));
    }

    executorService.shutdown();
    try {
      executorService.awaitTermination(5, TimeUnit.MINUTES);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private static void downloadFile(String fileURL, Path dir) {
    try {
      URL url = new URI(fileURL).toURL();

      try (InputStream in = url.openStream()) {
        Path fileName = Path.of(url.getPath()).getFileName();
        Path targetPath = dir.resolve(fileName);
        Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Downloading File: " + targetPath);
      }
    } catch (URISyntaxException | IOException e) {
      throw new RuntimeException(e);
    }
  }
}
