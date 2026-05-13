package com.viteprotocolo.atendimento.entity.cadSus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CadSusRequest {
    private String type_consult;
    private String value;

}
