package org.challenge.fileimport.entities;

import java.math.BigDecimal;
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
public class Salesman implements LineItem {

    public static final String LINE_ID = "001";

    private String cpf;
    private String name;
    private BigDecimal salary;

}
