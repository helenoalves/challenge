/* (C)2020 */
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
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DirectoryWatcherService {

  public static final String ERROR_FAIL_ON_FILE_S_PROCESSING = "Fail on file %s processing.";
  public static final String ERROR_TRYING_TO_GET_WATCH_SERVICE =
      "Error trying to get watch service";
  public static final String ERROR_NO_WATCH_SERVICE_CREATED_VERIFY_IT =
      "No watch service created. Verify it.";
  public static final String INFO_LOADING_WATCH_SERVICE = "Loading watch service";
  public static final String INFO_CREATED_DIRECTORY_IN_S = "Created directory in: %s";
  public static final String INFO_CREATED_DIRECTORY_OUT_S = "Created directory out: %s";
  public static final String INFO_STARTED_TO_WATCH_IN_DIRECTORY_S =
      "Started to watch in directory: %s";
  public static final String ERROR_ON_CREATING_IN_DIRECTORY_S =
      "Error on creating in directory: %s";
  public static final String ERROR_ON_CREATING_OUT_DIRECTORY_S =
      "Error on creating out directory: %s";
  public static final String ERROR_TRYING_TO_REGISTER_WATCHER_ON_DIRECTORY_S =
      "Error trying to register watcher on directory: %s";
  public static final String ERROR_TRYING_TO_TAKE_EVENTS_ON_WATCHED_DIRECTORY_S =
      "Error trying to take events on watched directory: %s";
  public static final String INFO_PROCESSING_LINES_OF_FILE_S = "Processing lines of file %s";
  public static final String INFO_TOOK_AND_POOL_D_EVENTS = "Took and pool %d event(s)";
  protected static final String HOME_PATH = System.getProperty("user.home");
  protected static final String IN_DIR = HOME_PATH + "/data/in/";
  protected static final String OUT_DIR = HOME_PATH + "/data/out/";
  protected static final String STOP_RUNNING_FILE = "stop.run";
  public static final String INFO_STOP_FILE_FOUND_S_STOPPING_APPLICATION =
      "Stop file found %s. Stopping application";

  private final ExecutorService executorService;
  private final Optional<WatchService> serviceOptional;
  private Boolean running = true;

  private DirectoryWatcherService(
      ExecutorService executorService, Optional<WatchService> serviceOptional) {
    this.executorService = executorService;
    this.serviceOptional = serviceOptional;
  }

  public static DirectoryWatcherService startWatchService() {
    return startWatchService(Executors.newWorkStealingPool(4), loadWatcherService());
  }

  public static DirectoryWatcherService startWatchService(
      ExecutorService executorService, Optional<WatchService> serviceOptional) {
    return new DirectoryWatcherService(executorService, serviceOptional);
  }

  protected static Optional<WatchService> loadWatcherService() {
    try {
      log.info(INFO_LOADING_WATCH_SERVICE);
      return Optional.of(FileSystems.getDefault().newWatchService());
    } catch (IOException e) {
      log.error(ERROR_TRYING_TO_GET_WATCH_SERVICE, e);
    }

    return Optional.empty();
  }

  public void execute() {
    createDirectories();
    Path directory = Paths.get(IN_DIR);

    if (serviceOptional.isEmpty()) {
      throw new FileImportException(ERROR_NO_WATCH_SERVICE_CREATED_VERIFY_IT);
    }

    registerWatcher(directory);

    log.info(String.format(INFO_STARTED_TO_WATCH_IN_DIRECTORY_S, directory.toString()));

    while (isRunning()) {
      WatchKey key = null;
      try {
        key = serviceOptional.get().take();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new FileImportException(
            String.format(ERROR_TRYING_TO_TAKE_EVENTS_ON_WATCHED_DIRECTORY_S, directory.toString()),
            e);
      }
      List<WatchEvent<?>> eventsPolled = key.pollEvents();

      log.info(String.format(INFO_TOOK_AND_POOL_D_EVENTS, eventsPolled.size()));
      for (WatchEvent<?> event : eventsPolled) {
        executorService.execute(verifyFileAndProcess(event.context()));
      }
      key.reset();
    }
  }

  public boolean isRunning() {
    return running;
  }

  protected void registerWatcher(Path directory) {
    try {
      directory.register(
          serviceOptional.get(),
          StandardWatchEventKinds.ENTRY_CREATE,
          StandardWatchEventKinds.ENTRY_MODIFY);
    } catch (IOException e) {
      throw new FileImportException(
          String.format(ERROR_TRYING_TO_REGISTER_WATCHER_ON_DIRECTORY_S, directory.toString()), e);
    }
  }

  protected <T> Runnable verifyFileAndProcess(T context) {
    return () -> {
      File file = new File(IN_DIR + context);
      if (STOP_RUNNING_FILE.equals(context)) {
        log.info(
            String.format(INFO_STOP_FILE_FOUND_S_STOPPING_APPLICATION, file.getAbsolutePath()));
        running = false;
        file.deleteOnExit();
      } else {
        SalesDataServices salesService = SalesDataServices.builder().build();
        try {
          log.info(String.format(INFO_PROCESSING_LINES_OF_FILE_S, file.getAbsolutePath()));
          processFile(file, salesService);
        } catch (Exception e) {
          log.error(String.format(ERROR_FAIL_ON_FILE_S_PROCESSING, file.getAbsolutePath()), e);
        }
      }
    };
  }

  protected void processFile(File file, SalesDataServices salesService) {
    LineHelper.builder()
        .outputDir(OUT_DIR)
        .worstSalesman(salesService::findWorstSalesman)
        .mostExpensiveSaleId(salesService::findMostExpensiveSaleId)
        .build()
        .readFileLines(file);
  }

  private void createDirectories() {
    Path inDirectory = Paths.get(IN_DIR);
    Path outDirectory = Paths.get(OUT_DIR);

    if (!Files.exists(inDirectory)) {
      try {
        Files.createDirectories(inDirectory);
      } catch (IOException e) {
        throw new FileImportException(
            String.format(ERROR_ON_CREATING_IN_DIRECTORY_S, inDirectory.toString()), e);
      }
      log.info(String.format(INFO_CREATED_DIRECTORY_IN_S, inDirectory.toString()));
    }

    if (!Files.exists(outDirectory)) {
      try {
        Files.createDirectories(outDirectory);
      } catch (IOException e) {
        throw new FileImportException(
            String.format(ERROR_ON_CREATING_OUT_DIRECTORY_S, inDirectory.toString()), e);
      }
      log.info(String.format(INFO_CREATED_DIRECTORY_OUT_S, inDirectory.toString()));
    }
  }
}
