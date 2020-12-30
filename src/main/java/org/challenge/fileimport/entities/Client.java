/* (C)2020 */
package org.challenge.fileimport.entities;

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
public class Client implements LineItem {

  public static final String LINE_ID = "002";
  private String cnpj;
  private String name;
  private String businessArea;
}
