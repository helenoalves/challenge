/* (C)2020 */
package org.challenge.fileimport;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.challenge.fileimport.entities.Client;
import org.challenge.fileimport.entities.ItemData;
import org.challenge.fileimport.entities.LineItem;
import org.challenge.fileimport.entities.SaleData;
import org.challenge.fileimport.entities.Salesman;

public final class EntityHelper {

  public static final String SEPARATOR = "ç";
  public static final String EXCEPTION_NO_STRATEGY_FOUND_FOR_LINE_S =
      "No strategy found for line: %s";

  @Getter private Map<String, Function<String, LineItem>> lineStrategies;

  public EntityHelper() {
    lineStrategies =
        Map.of(
            Salesman.LINE_ID,
            this::createSalesman,
            Client.LINE_ID,
            this::createClient,
            SaleData.LINE_ID,
            this::createSaleData);
  }

  public OutputFileContentVO createEntities(List<String> fileLines) {
    List<String> fileLinesNotNull = Optional.ofNullable(fileLines).orElse(Arrays.asList());
    List<LineItem> entities = new ArrayList<>();

    for (String line : fileLinesNotNull) {
      String[] result = StringUtils.split(line, SEPARATOR);
      Function<String, LineItem> selectedStrategy =
          lineStrategies.getOrDefault(result[0], this::createDefault);
      entities.add(selectedStrategy.apply(line));
    }

    return OutputFileContentVO.builder()
        .clients(
            entities.stream()
                .filter(Client.class::isInstance)
                .map(Client.class::cast)
                .collect(Collectors.toList()))
        .salesmen(
            entities.stream()
                .filter(Salesman.class::isInstance)
                .map(Salesman.class::cast)
                .collect(Collectors.toList()))
        .salesdata(
            entities.stream()
                .filter(SaleData.class::isInstance)
                .map(SaleData.class::cast)
                .collect(Collectors.toList()))
        .build();
  }

  protected LineItem createDefault(String line) {
    throw new FileImportException(String.format(EXCEPTION_NO_STRATEGY_FOUND_FOR_LINE_S, line));
  }

  protected Salesman createSalesman(String line) {
    String[] entity = line.split(SEPARATOR);

    verifyEntity(entity, "Salesman line", 4);

    return Salesman.builder()
        .cpf(entity[1])
        .name(entity[2])
        .salary(new BigDecimal(entity[3]))
        .build();
  }

  protected Client createClient(String line) {
    String lineNotNull = Optional.ofNullable(line).orElse(StringUtils.EMPTY);
    String[] entity = lineNotNull.split(SEPARATOR);

    verifyEntity(entity, "Client line", 4);
    return Client.builder().cnpj(entity[1]).name(entity[2]).businessArea(entity[3]).build();
  }

  protected SaleData createSaleData(String line) {
    String lineNotNull = Optional.ofNullable(line).orElse(StringUtils.EMPTY);
    String[] entity = lineNotNull.split(SEPARATOR);

    verifyEntity(entity, "SalesData line", 4);
    String itemsData = StringUtils.substringBetween(entity[2], "[", "]");

    return SaleData.builder()
        .saleId(Integer.valueOf(entity[1]))
        .items(createItems(itemsData))
        .salesmanName(entity[3])
        .build();
  }

  private List<ItemData> createItems(String itemsData) {
    String itemDataNotNull = Optional.ofNullable(itemsData).orElse(StringUtils.EMPTY);
    List<ItemData> items = new ArrayList<>();

    for (String itemDataComma : itemDataNotNull.split(",")) {
      String[] entity = itemDataComma.split("-");

      verifyEntity(entity, "SalesData line on item", 3);

      items.add(
          ItemData.builder()
              .itemId(Integer.valueOf(entity[0]))
              .quantity(Integer.valueOf(entity[1]))
              .price(new BigDecimal(entity[2]))
              .build());
    }

    return items;
  }

  protected void verifyEntity(String[] entity, String entityType, int partsSize) {
    if (entity.length != partsSize) {
      throw new EntityException(
          String.format("%s parts without the indicated %d parts", entityType, partsSize));
    }
  }
}
