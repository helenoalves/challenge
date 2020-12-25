package org.challenge.fileimport.utils.vo;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.challenge.fileimport.entities.Client;
import org.challenge.fileimport.entities.SaleData;
import org.challenge.fileimport.entities.Salesman;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutputFileContentVO {

    private List<Client> clients;
    private List<Salesman> salesmen;
    private List<SaleData> salesdata;

}
