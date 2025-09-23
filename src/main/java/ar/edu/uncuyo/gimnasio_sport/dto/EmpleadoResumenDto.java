package ar.edu.uncuyo.gimnasio_sport.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoResumenDto {
    private Long id;
    private String nombre;
    private String apellido;
    private String correoElectronico;
    private String telefono;
    private String tipoEmpleado;   // Enum convertido a String
    private String sucursalNombre; // Para mostrar nombre de sucursal en la tabla
}

