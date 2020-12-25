package org.challenge.fileimport;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.WatchService;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Assert;
import org.junit.Test;

public class DirectoryWatcherServiceTest {

  @Test
  public void shouldCreateWatchService() {
    DirectoryWatcherService directoryService = DirectoryWatcherService.builder().build();
    Optional<WatchService> serviceOptional = directoryService.loadWatcherService();

    Assert.assertFalse(serviceOptional.isEmpty());
  }

  @Test
  public void shouldExecuteDirectoryService() throws IOException, InterruptedException {
    DirectoryWatcherService dirService = DirectoryWatcherService.builder().build();

    ExecutorService service = Executors.newSingleThreadExecutor();
    service.execute(() -> {
      try {
        dirService.execute();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    });

    createTestFileOnDirIn();
  }

  private void createTestFileOnDirIn() throws IOException {
    List<String> lines = Arrays.asList("001ç1234567891234çPedroç50000",
        "001ç3245678865434çPauloç40000.99",
        "002ç2345675434544345çJose da SilvaçRural",
        "002ç2345675433444345çEduardo PereiraçRural",
        "003ç10ç[1-10-100,2-30-2.50,3-40-3.10]çPedro");

    Files.write(Paths
            .get(DirectoryWatcherService.IN_DIR, "testfile.txt"),
        lines,
        StandardCharsets.ISO_8859_1);
  }
}