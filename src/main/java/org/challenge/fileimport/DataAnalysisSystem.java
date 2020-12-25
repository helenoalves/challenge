package org.challenge.fileimport;

public class DataAnalysisSystem {

    public static void main(String[] args) throws Exception {
        DirectoryWatcherService.builder().build().execute();
    }

}
