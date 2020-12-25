package org.challenge.fileimport;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Builder()
@Slf4j
public class DirectoryWatcherService {

  protected static final String HOME_PATH = System.getProperty("user.home");
  protected static final String IN_DIR = HOME_PATH + "/data/in/";
  protected static final String OUT_DIR = HOME_PATH + "/data/out/";

  public static final String FAIL_ON_FILE_S_PROCESSING = "Fail on file %s processing.";
  public static final String ERROR_TRYING_TO_GET_WATCH_SERVICE = "Error trying to get watch service";
  public static final String NO_WATCH_SERVICE_CREATED_VERIFY_IT = "No watch service created. Verify it.";
  public static final String LOADING_WATCH_SERVICE = "Loading watch service";
  public static final String CREATED_DIRECTORY_IN_S = "Created directory in: %s";
  public static final String CREATED_DIRECTORY_OUT_S = "Created directory out: %s";
  public static final String STARTED_TO_WATCH_IN_DIRECTORY_S = "Started to watch in directory: %s";

  private final ExecutorService executorService = Executors.newWorkStealingPool(4);
  private final Optional<WatchService> serviceOptional = loadWatcherService();

  protected Optional<WatchService> loadWatcherService() {
    try {
      log.info(LOADING_WATCH_SERVICE);
      return Optional.of(FileSystems.getDefault().newWatchService());
    } catch (IOException e) {
      log.error(ERROR_TRYING_TO_GET_WATCH_SERVICE, e);
    }

    return Optional.empty();
  }

  public void execute() throws Exception {
    createDirectories();
    Path directory = Paths.get(IN_DIR);

    if(serviceOptional.isEmpty()){
      throw new FileImportException(NO_WATCH_SERVICE_CREATED_VERIFY_IT);
    }

    directory.register(serviceOptional.get(),
        StandardWatchEventKinds.ENTRY_CREATE,
        StandardWatchEventKinds.ENTRY_MODIFY);

    log.info(String.format(STARTED_TO_WATCH_IN_DIRECTORY_S, directory.toString()));

    while (true) {
      WatchKey key = serviceOptional.get().take();

      for (WatchEvent<?> event : key.pollEvents()) {
        executorService.execute(processFile(event.context(),
            event.kind().equals(StandardWatchEventKinds.ENTRY_CREATE)));
      }
      key.reset();
    }
  }

  private <T> Runnable processFile(T context, boolean newFile) throws Exception {
    return () -> {
      File file = new File(IN_DIR + context);
      SalesDataServices salesService = SalesDataServices.builder().build();
      try {
        LineHelper.builder().outputDir(OUT_DIR)
            .worstSalesman(salesService::findWorstSalesman)
            .mostExpensiveSaleId(salesService::findMostExpensiveSaleId)
            .build()
            .readFileLines(file);
      } catch (Exception e) {
        log.error(String.format(FAIL_ON_FILE_S_PROCESSING, file.getAbsolutePath()), e);
      }
    };
  }

  private void createDirectories() throws Exception {
    Path inDirectory = Paths.get(IN_DIR);
    Path outDirectory = Paths.get(OUT_DIR);

    if (!Files.exists(inDirectory)) {
      Files.createDirectories(inDirectory);
      log.info(String.format(CREATED_DIRECTORY_IN_S, inDirectory.toString()));
    }

    if (!Files.exists(outDirectory)) {
      Files.createDirectories(outDirectory);
      log.info(String.format(CREATED_DIRECTORY_OUT_S, inDirectory.toString()));
    }
  }

}
