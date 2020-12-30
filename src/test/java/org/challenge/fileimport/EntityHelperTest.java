/* (C)2020 */
package org.challenge.fileimport;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.challenge.fileimport.entities.Client;
import org.challenge.fileimport.entities.SaleData;
import org.challenge.fileimport.entities.Salesman;
import org.junit.Assert;
import org.junit.Test;

public class EntityHelperTest {

  @Test
  public void shouldCreateLines() {
    String oneLine = "003ç10ç[1-10-100,2-30-2.50,3-40-3.10]çPedro";
    String[] result = StringUtils.split(oneLine, EntityHelper.SEPARATOR);

    Assert.assertEquals(4, result.length);
  }

  @Test
  public void shouldCreateEntities() {
    List<String> lines =
        Arrays.asList(
            "001ç1234567891234çPedroç50000",
            "001ç3245678865434çPauloç40000.99",
            "002ç2345675434544345çJose da SilvaçRural",
            "002ç2345675433444345çEduardo PereiraçRural",
            "003ç10ç[1-10-100,2-30-2.50,3-40-3.10]çPedro");

    OutputFileContentVO outputFileContent = new EntityHelper().createEntities(lines);
    Assert.assertNotNull(outputFileContent);
    Assert.assertEquals(2, outputFileContent.getClients().size());
    Assert.assertEquals(2, outputFileContent.getSalesmen().size());
    Assert.assertEquals(1, outputFileContent.getSalesdata().size());

    Assert.assertEquals(
        Integer.valueOf(10), outputFileContent.getSalesdata().stream().findAny().get().getSaleId());
    Assert.assertEquals(
        Integer.valueOf(10), outputFileContent.getSalesdata().stream().findAny().get().getSaleId());
    Assert.assertEquals(
        3, outputFileContent.getSalesdata().stream().findAny().get().getItems().size());
  }

  @Test
  public void shouldCreateSalesman() {
    String salesmanLine = "001ç1234567891234çPedroç50000";
    Salesman salesmanCreated = new EntityHelper().createSalesman(salesmanLine);

    Assert.assertEquals("1234567891234", salesmanCreated.getCpf());
    Assert.assertEquals("Pedro", salesmanCreated.getName());
  }

  @Test
  public void shouldCreateClient() {
    String clientLine = "002ç2345675434544345çJose da SilvaçRural";
    Client clientCreated = new EntityHelper().createClient(clientLine);

    Assert.assertEquals("2345675434544345", clientCreated.getCnpj());
    Assert.assertEquals("Rural", clientCreated.getBusinessArea());
  }

  @Test
  public void shouldCreateSaleData() {
    String saleDataLine = "003ç10ç[1-10-100,2-30-2.50,3-40-3.10]çPedro";
    SaleData saleDataCreated = new EntityHelper().createSaleData(saleDataLine);

    Assert.assertEquals("Pedro", saleDataCreated.getSalesmanName());
    Assert.assertEquals(
        BigDecimal.valueOf(2.50).setScale(2), saleDataCreated.getItems().get(1).getPrice());
  }
}
