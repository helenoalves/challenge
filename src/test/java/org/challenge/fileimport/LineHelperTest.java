/* (C)2020 */
package org.challenge.fileimport;

import static org.mockito.ArgumentMatchers.any;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.challenge.fileimport.entities.Client;
import org.challenge.fileimport.entities.ItemData;
import org.challenge.fileimport.entities.SaleData;
import org.challenge.fileimport.entities.Salesman;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class LineHelperTest {

  @Test
  public void shouldReadTheThreeLinesResourceFile() {
    File anyResourceFile =
        Paths.get("src", "test", "resources", "not.exists.line.type.txt").toFile();

    LineHelper lineHelper = Mockito.spy(LineHelper.builder().build());
    ArgumentCaptor<List<String>> linesCaptor = ArgumentCaptor.forClass(List.class);
    Mockito.doReturn(anyResourceFile).when(lineHelper).writeFileLines(any(), any());
    Mockito.doReturn(new OutputFileContentVO())
        .when(lineHelper)
        .loadEntities(linesCaptor.capture());

    File outFile = lineHelper.readFileLines(anyResourceFile);
    Assert.assertNotNull(outFile);
    Assert.assertEquals(3, linesCaptor.getValue().size());
  }

  @Test
  public void shouldWriteOnFileBasedOnVO() throws IOException {
    File anyResourceFile =
        Paths.get("src", "test", "resources", "not.exists.line.type.txt").toFile();

    LineHelper lineHelper =
        Mockito.spy(
            LineHelper.builder()
                .outputDir(Paths.get("src", "test", "resources").toFile().getAbsolutePath())
                .mostExpensiveSaleId(allSalesData -> Integer.toString(allSalesData.size()))
                .worstSalesman(allSalesData -> allSalesData.toString())
                .build());

    OutputFileContentVO outVO =
        OutputFileContentVO.builder()
            .clients(
                Arrays.asList(
                    new Client("111111111", "Zé", "Selva de Pedra"),
                    new Client("222222222", "Bino", "Sem Area")))
            .salesmen(
                Arrays.asList(
                    new Salesman("3333333333", "Josué da Venda", BigDecimal.valueOf(10.54)),
                    new Salesman("44444444444", "Sergio Malandro", BigDecimal.valueOf(234.32)),
                    new Salesman("55555555555", "Gustavo Pinheiro", BigDecimal.valueOf(54342.12))))
            .salesdata(
                Arrays.asList(
                    new SaleData(
                        171,
                        "Sheila Moreira",
                        Arrays.asList(new ItemData(1, 2, BigDecimal.valueOf(1.99))))))
            .build();

    File outFile = lineHelper.writeFileLines(anyResourceFile, outVO);
    Assert.assertNotNull(outFile);

    List<String> outLines = Files.readAllLines(outFile.toPath());

    Assert.assertEquals(4, outLines.size());
    Assert.assertEquals("2", outLines.get(0));
    Assert.assertEquals("3", outLines.get(1));
    Assert.assertEquals("1", outLines.get(2));
    Assert.assertEquals(
        "[SaleData(saleId=171, salesmanName=Sheila Moreira, items=[ItemData(itemId=1, quantity=2, price=1.99)])]",
        outLines.get(3));

    outFile.deleteOnExit();
  }
}
