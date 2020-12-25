package org.challenge.fileimport;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import lombok.Builder;
import org.apache.commons.io.FileUtils;
import org.challenge.fileimport.entities.SaleData;
import org.challenge.fileimport.utils.EntityUtils;
import org.challenge.fileimport.utils.vo.OutputFileContentVO;

@Builder
public class LineHelper {

  private String outputDir;
  private Function<List<SaleData>, String> mostExpensiveSaleId;
  private Function<List<SaleData>, String> worstSalesman;

  public void readFileLines(File file) throws Exception {
    List<String> lines = FileUtils.readLines(file, StandardCharsets.UTF_8);
    OutputFileContentVO outputFileContent = EntityUtils.getInstance().createEntities(lines);

    writeFileLines(file, outputFileContent);
  }

  private void writeFileLines(File file, OutputFileContentVO outputFileContent) throws Exception {
    File outputFile = new File(outputDir + file.getName());

    FileUtils.writeLines(outputFile,
        StandardCharsets.UTF_8.name(),
        Arrays.asList(outputFileContent.getClients(),
            outputFileContent.getSalesmen(),
            mostExpensiveSaleId.apply(outputFileContent.getSalesdata()),
            worstSalesman.apply(outputFileContent.getSalesdata()),
            false));
  }

}
