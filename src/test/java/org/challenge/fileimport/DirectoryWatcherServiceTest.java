/* (C)2020 */
package org.challenge.fileimport;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class DirectoryWatcherServiceTest {

  @Test
  public void shouldCreateWatchService() {
    Optional<WatchService> serviceOptional = DirectoryWatcherService.loadWatcherService();

    Assert.assertFalse(serviceOptional.isEmpty());
  }

  @Test
  public void shouldExecuteDirectoryService() throws InterruptedException {
    ExecutorService executorMock = Mockito.mock(ExecutorService.class);
    WatchService watchServiceMock = Mockito.mock(WatchService.class);
    WatchKey watchKeyMock = Mockito.mock(WatchKey.class);
    Optional<WatchService> serviceOptionalMock = Optional.of(watchServiceMock);

    Mockito.when(watchServiceMock.take()).thenReturn(watchKeyMock);
    Mockito.when(watchKeyMock.pollEvents())
        .thenReturn(Arrays.asList(Mockito.mock(WatchEvent.class)));

    DirectoryWatcherService dirService =
        Mockito.spy(DirectoryWatcherService.startWatchService(executorMock, serviceOptionalMock));
    Mockito.doNothing().when(dirService).registerWatcher(any());

    ExecutorService service = Executors.newSingleThreadExecutor();
    service.execute(
        () -> {
          try {
            dirService.execute();
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        });

    while (service.awaitTermination(1, TimeUnit.SECONDS)) {
      System.out.println("Waiting");
    }

    Mockito.verify(dirService, atLeastOnce()).registerWatcher(any());
    Mockito.verify(dirService, atLeastOnce()).verifyFileAndProcess(any());
  }

  @Test
  public void shouldProcessFileOnDirectoryService() throws InterruptedException {
    ExecutorService executorMock = Mockito.mock(ExecutorService.class);
    WatchService watchServiceMock = Mockito.mock(WatchService.class);
    Optional<WatchService> serviceOptionalMock = Optional.of(watchServiceMock);

    DirectoryWatcherService dirService =
        Mockito.spy(DirectoryWatcherService.startWatchService(executorMock, serviceOptionalMock));
    Mockito.doNothing().when(dirService).processFile(any(), any());

    dirService.verifyFileAndProcess("anyfile.txt").run();

    Mockito.verify(dirService, times(1)).processFile(any(), any());
    Assert.assertTrue(dirService.isRunning());
  }

  @Test
  public void shouldStopExecuteDirectoryService() throws InterruptedException {
    ExecutorService executorMock = Mockito.mock(ExecutorService.class);
    WatchService watchServiceMock = Mockito.mock(WatchService.class);
    Optional<WatchService> serviceOptionalMock = Optional.of(watchServiceMock);

    DirectoryWatcherService dirService =
        Mockito.spy(DirectoryWatcherService.startWatchService(executorMock, serviceOptionalMock));
    Mockito.doNothing().when(dirService).processFile(any(), any());

    dirService.verifyFileAndProcess(DirectoryWatcherService.STOP_RUNNING_FILE).run();

    Mockito.verify(dirService, never()).processFile(any(), any());
    Assert.assertFalse(dirService.isRunning());
  }
}
