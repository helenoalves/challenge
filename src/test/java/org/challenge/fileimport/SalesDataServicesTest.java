/* (C)2020 */
package org.challenge.fileimport;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.challenge.fileimport.entities.ItemData;
import org.challenge.fileimport.entities.SaleData;
import org.junit.Test;

public class SalesDataServicesTest {

  private List<SaleData> listOfSales =
      Arrays.asList(
          SaleData.builder()
              .saleId(3)
              .salesmanName("Jonny")
              .items(
                  Arrays.asList(
                      ItemData.builder().itemId(1).price(BigDecimal.TEN).quantity(1).build(),
                      ItemData.builder().itemId(2).price(BigDecimal.TEN).quantity(1).build()))
              .build(),
          SaleData.builder()
              .saleId(2)
              .salesmanName("Bino")
              .items(
                  Arrays.asList(
                      ItemData.builder()
                          .itemId(1)
                          .price(BigDecimal.valueOf(50.0))
                          .quantity(1)
                          .build()))
              .build());

  @Test
  public void shouldReturnBinoMostExpensive() {
    String mostExpensive = SalesDataServices.builder().build().findMostExpensiveSaleId(listOfSales);

    Assert.assertEquals("2", mostExpensive);
  }

  @Test
  public void shouldReturnJonnyWorst() {
    String worstSales = SalesDataServices.builder().build().findWorstSalesman(listOfSales);

    Assert.assertEquals("Jonny", worstSales);
  }
}
