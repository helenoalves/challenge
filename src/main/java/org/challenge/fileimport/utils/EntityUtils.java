package org.challenge.fileimport.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.challenge.fileimport.FileImportException;
import org.challenge.fileimport.entities.Client;
import org.challenge.fileimport.entities.ItemData;
import org.challenge.fileimport.entities.LineItem;
import org.challenge.fileimport.entities.SaleData;
import org.challenge.fileimport.entities.Salesman;
import org.challenge.fileimport.utils.vo.OutputFileContentVO;

public final class EntityUtils {

  public static final String SEPARATOR = "ç";

  @Getter
  private Map<String, Function<String, LineItem>> lineStrategies;

  private EntityUtils() {
    lineStrategies = Map
        .of(Salesman.LINE_ID, this::createSalesman, Client.LINE_ID, this::createClient,
            SaleData.LINE_ID, this::createSaleData);
  }

  public static EntityUtils getInstance() {
    return EntityUtilsHolder.INSTANCE;
  }

  public OutputFileContentVO createEntities(List<String> fileLines) {
    List<LineItem> entities = new ArrayList<>();

    for (String line : fileLines) {
      String[] result = StringUtils.split(line, SEPARATOR);
      Function<String, LineItem> selectedStrategy = lineStrategies
          .getOrDefault(result[0], this::createDefault);
      entities.add(selectedStrategy.apply(line));
    }

    return OutputFileContentVO.builder()
        .clients(entities.stream().filter(Client.class::isInstance).map(Client.class::cast)
            .collect(Collectors.toList()))
        .salesmen(entities.stream().filter(Salesman.class::isInstance).map(Salesman.class::cast)
            .collect(Collectors.toList()))
        .salesdata(entities.stream().filter(SaleData.class::isInstance).map(SaleData.class::cast)
            .collect(Collectors.toList()))
        .build();
  }

  protected LineItem createDefault(String line) {
    throw new FileImportException(String.format("No strategy found for line: %s", line));
  }

  protected Salesman createSalesman(String line) {
    String[] entity = line.split(SEPARATOR);

    return Salesman.builder()
        .cpf(entity[1])
        .name(entity[2])
        .salary(new BigDecimal(entity[3])).build();
  }

  protected Client createClient(String line) {
    String[] entity = line.split(SEPARATOR);

    return Client.builder()
        .cnpj(entity[1])
        .name(entity[2])
        .businessArea(entity[3]).build();
  }

  protected SaleData createSaleData(String line) {
    String[] entity = line.split(SEPARATOR);
    String itemsData = StringUtils.substringBetween(entity[2], "[", "]");

    return SaleData.builder()
        .saleId(Integer.valueOf(entity[1]))
        .items(createItems(itemsData))
        .salesmanName(entity[3]).build();
  }

  private List<ItemData> createItems(String itemsData) {
    List<ItemData> items = new ArrayList<>();

    for (String itemData : itemsData.split(",")) {
      String[] entity = itemData.split("-");

      items.add(ItemData.builder()
          .itemId(Integer.valueOf(entity[0]))
          .quantity(Integer.valueOf(entity[1]))
          .price(new BigDecimal(entity[2])).build());
    }

    return items;
  }

  private static final class EntityUtilsHolder {

    static final EntityUtils INSTANCE = new EntityUtils();
  }

}
