/* (C)2020 */
package org.challenge.fileimport;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import lombok.Builder;
import org.apache.commons.io.FileUtils;
import org.challenge.fileimport.entities.SaleData;

@Builder
public class LineHelper {

  public static final String ERROR_TRYING_TO_CREATE_AND_WRITE_ON_OUTPUT_FILE_S =
      "Error trying to create and write on output file: %s";
  public static final String ERROR_TRYING_TO_READ_LINES_FROM_INPUT_FILE_S =
      "Error trying to read lines from input file: %s";
  private String outputDir;
  private Function<List<SaleData>, String> mostExpensiveSaleId;
  private Function<List<SaleData>, String> worstSalesman;

  public File readFileLines(File inputFile) {
    verifyInputFile(inputFile);

    List<String> lines = null;
    try {
      lines = FileUtils.readLines(inputFile, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new FileImportException(
          String.format(ERROR_TRYING_TO_READ_LINES_FROM_INPUT_FILE_S, inputFile.getAbsolutePath()),
          e);
    }
    OutputFileContentVO outputFileContent = loadEntities(lines);

    return writeFileLines(inputFile, outputFileContent);
  }

  protected OutputFileContentVO loadEntities(List<String> lines) {
    return new EntityHelper().createEntities(lines);
  }

  protected File writeFileLines(File inputFile, OutputFileContentVO outputFileContent) {
    verifyInputFile(inputFile);

    File outputFile = new File(outputDir + inputFile.getName());

    try {
      FileUtils.writeLines(
          outputFile,
          StandardCharsets.UTF_8.name(),
          Arrays.asList(
              Optional.ofNullable(outputFileContent)
                  .map(OutputFileContentVO::getClients)
                  .map(List::size)
                  .orElse(0),
              Optional.ofNullable(outputFileContent)
                  .map(OutputFileContentVO::getSalesmen)
                  .map(List::size)
                  .orElse(0),
              mostExpensiveSaleId.apply(
                  Optional.ofNullable(outputFileContent)
                      .map(OutputFileContentVO::getSalesdata)
                      .orElse(List.of())),
              worstSalesman.apply(
                  Optional.ofNullable(outputFileContent)
                      .map(OutputFileContentVO::getSalesdata)
                      .orElse(List.of()))));

      return outputFile;
    } catch (IOException e) {
      throw new FileImportException(
          String.format(ERROR_TRYING_TO_CREATE_AND_WRITE_ON_OUTPUT_FILE_S, outputFile), e);
    }
  }

  protected void verifyInputFile(File inputFile) {
    if (inputFile == null || !inputFile.exists()) {
      throw new FileImportException("Input file not sent !");
    }
  }
}
