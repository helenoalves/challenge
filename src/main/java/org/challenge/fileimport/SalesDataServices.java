package org.challenge.fileimport;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import lombok.Builder;
import org.apache.commons.collections4.CollectionUtils;
import org.challenge.fileimport.entities.SaleData;

@Builder
public class SalesDataServices {

  public String findMostExpensiveSaleId(List<SaleData> salesData) {
    if (CollectionUtils.isEmpty(salesData)) {
      return null;
    }

    salesData.sort(Comparator.comparing(sale -> sale.getItems().stream()
        .map(itemData -> (itemData.getPrice().multiply(new BigDecimal(itemData.getQuantity()))))
        .reduce(BigDecimal.ZERO, BigDecimal::add)));
    Collections.reverse(salesData);

    return salesData.stream().findFirst().get().getSaleId().toString();
  }

  public String findWorstSalesman(List<SaleData> salesData) {
    if (CollectionUtils.isEmpty(salesData)) {
      return null;
    }

    return salesData.stream()
        .sorted(Comparator.comparing(sale -> sale.getItems().stream()
            .map(itemData -> (itemData.getPrice().multiply(new BigDecimal(itemData.getQuantity()))))
            .reduce(BigDecimal.ZERO, BigDecimal::add)))
        .findFirst()
        .get()
        .getSalesmanName();
  }

}
