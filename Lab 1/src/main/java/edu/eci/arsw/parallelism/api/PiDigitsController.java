
package edu.eci.arsw.parallelism.api;

import edu.eci.arsw.parallelism.core.PiDigitsService;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/pi")
@Validated
@Tag(name = "Pi", description = "Operaciones para obtener dígitos de Pi en hexadecimal")
/**
 * REST controller that exposes endpoints to obtain hexadecimal digits of Pi.
 */
public class PiDigitsController {

    private final PiDigitsService service;

    public PiDigitsController(PiDigitsService service) {
        this.service = service;
    }

    /**
     * REST endpoint that returns hexadecimal digits of Pi.
     *
     * @param start zero-based position after the radix point (must be >= 0)
     * @param count number of hex digits to return (must be >= 0)
     * @param threads number of threads to use for calculation (must be >= 1)
     * @param strategy calculation strategy to use (e.g., "sequential" or "parallel")
     * @return a {@link PiResponse} containing the request parameters and the
     * computed digits as an uppercase hexadecimal string
     */
    @Operation(summary = "Obtener dígitos de Pi", description = "Devuelve una cadena con dígitos hexadecimales de Pi a partir de la posición solicitada")
    @GetMapping("/digits")
    public PiResponse digits(
            @RequestParam @Min(0) @Parameter(description = "Posición inicial (0-based) después del punto", example = "0", required = true) int start,
            @RequestParam @Min(0) @Parameter(description = "Cantidad de dígitos hexadecimales a retornar", example = "5", required = true) int count,
            @RequestParam(required = false, defaultValue = "1") @Min(1) @Parameter(description = "Número de hilos a usar (dependiente de la estrategia)", example = "1", required = false) int threads,
            @RequestParam(required = false, defaultValue = "sequential") @Parameter(description = "Estrategia de cálculo: 'sequential' o 'thread-join'", example = "sequential", required = false) String strategy
    ) {
        String digits = service.calculate(start, count, threads, strategy);
        return new PiResponse(start, count, digits);
    }
}
