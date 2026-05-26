
package edu.eci.arsw.parallelism.api;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response payload returned by the Pi digits endpoints.
 *
 * @param start the requested start index (zero-based)
 * @param count the requested number of digits
 * @param digits the computed hexadecimal digits (uppercase)
 */
@Schema(description = "Respuesta con los parámetros de la petición y los dígitos hexadecimales calculados")
public record PiResponse(
	@Schema(description = "Posición inicial solicitada (0-based)", example = "0") int start,
	@Schema(description = "Número de dígitos solicitados", example = "5") int count,
	@Schema(description = "Dígitos de Pi en hexadecimal (mayúsculas)", example = "243F6") String digits) {
}
