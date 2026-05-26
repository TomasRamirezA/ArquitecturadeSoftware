package edu.eci.arsw.dogsrace.ui;

import org.junit.jupiter.api.Test;

import javax.swing.JButton;
import java.awt.Color;

import static org.junit.jupiter.api.Assertions.*;

class CarrilTest {

    @Test
    void deberiaCrearCarrilConNumeroCorrectoDePasos() {
        Carril carril = new Carril(5, "A");

        assertEquals(5, carril.size());
    }

    @Test
    void deberiaRetornarPasoYLlegadaNoNulos() {
        Carril carril = new Carril(3, "B");

        JButton paso = carril.getPaso(0);
        JButton llegada = carril.getLlegada();

        assertNotNull(paso);
        assertNotNull(llegada);
    }

    @Test
    void setPasoOnDebeMarcarTexto() {
        Carril carril = new Carril(3, "C");

        carril.setPasoOn(1);

        assertEquals("o", carril.getPaso(1).getText());
    }

    @Test
    void setPasoOffDebeLimpiarTexto() {
        Carril carril = new Carril(3, "D");

        carril.setPasoOn(1);
        carril.setPasoOff(1);

        assertEquals("", carril.getPaso(1).getText());
    }

    @Test
    void finishDebeCambiarTextoDeLlegada() {
        Carril carril = new Carril(3, "E");

        carril.finish();

        assertEquals("!", carril.getLlegada().getText());
    }

    @Test
    void displayPasosDebeMostrarNumeroEnLlegada() {
        Carril carril = new Carril(3, "F");

        carril.displayPasos(2);

        assertEquals("2", carril.getLlegada().getText());
    }

    @Test
    void reStartDebeReiniciarTextoYColor() {
        Carril carril = new Carril(3, "G");

        carril.setPasoOn(0);
        carril.finish();
        carril.reStart();

        for (int i = 0; i < carril.size(); i++) {
            assertEquals(Color.LIGHT_GRAY, carril.getPaso(i).getBackground());
        }

        assertEquals("G", carril.getLlegada().getText());
        assertEquals(Color.GREEN, carril.getLlegada().getBackground());
    }

    @Test
    void getNameDebeRetornarNombreDelCarril() {
        Carril carril = new Carril(2, "Carril1");

        assertEquals("Carril1", carril.getName());
    }
}
