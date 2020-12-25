package org.challenge.fileimport.entities;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleData implements LineItem {

    public static final String LINE_ID = "003";
    private Integer saleId;
    private String salesmanName;
    private List<ItemData> items;

}
