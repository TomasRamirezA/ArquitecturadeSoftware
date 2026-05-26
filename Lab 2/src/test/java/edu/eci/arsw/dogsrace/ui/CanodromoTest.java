package edu.eci.arsw.dogsrace.ui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CanodromoTest {

    @Test
    void deberiaCrearCanodromoConNumeroDeCarrilesCorrecto() {
        Canodromo canodromo = new Canodromo(3, 10);

        assertEquals(3, canodromo.getNumCarriles());
    }

    @Test
    void deberiaRetornarUnCarrilValido() {
        Canodromo canodromo = new Canodromo(2, 5);

        Carril carril0 = canodromo.getCarril(0);
        Carril carril1 = canodromo.getCarril(1);

        assertNotNull(carril0);
        assertNotNull(carril1);
        assertNotSame(carril0, carril1);
    }

    @Test
    void restartNoDebeLanzarExcepcion() {
        Canodromo canodromo = new Canodromo(2, 5);

        assertDoesNotThrow(() -> canodromo.restart());
    }

    @Test
    void winnerDialogNoDebeLanzarExcepcion() {
        Canodromo canodromo = new Canodromo(1, 5);

        assertDoesNotThrow(() -> canodromo.winnerDialog("Firulais", 3));
    }
}
