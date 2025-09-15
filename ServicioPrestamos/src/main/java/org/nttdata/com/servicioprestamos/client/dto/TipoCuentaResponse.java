package org.nttdata.com.servicioprestamos.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TipoCuentaResponse {
    private Long id;
    private String nombre;
}
