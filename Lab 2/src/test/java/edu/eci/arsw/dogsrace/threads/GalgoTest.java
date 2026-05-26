package edu.eci.arsw.dogsrace.threads;

import edu.eci.arsw.dogsrace.control.RaceControl;
import edu.eci.arsw.dogsrace.domain.ArrivalRegistry;
import edu.eci.arsw.dogsrace.ui.Carril;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GalgoTest {

    @Test
    void deberiaCorrerHastaElFinalYRegistrarLlegada() throws Exception {
        Carril carril = mock(Carril.class);
        ArrivalRegistry registry = mock(ArrivalRegistry.class);
        RaceControl control = mock(RaceControl.class);

        when(carril.size()).thenReturn(3);

        when(registry.registerArrival(anyString())).thenReturn(new ArrivalRegistry.ArrivalSnapshot(1, "Firulais"));

        Galgo galgo = new Galgo(carril, "Firulais", registry, control);

        galgo.start();
        galgo.join();

        verify(control, atLeastOnce()).awaitIfPaused();
        verify(carril, times(3)).setPasoOn(anyInt());
        verify(carril, times(3)).displayPasos(anyInt());
        verify(carril, times(1)).finish();
        verify(registry, times(1)).registerArrival("Firulais");
    }

    @Test
    void deberiaManejarInterrupcionSinColgarse() throws Exception {
        Carril carril = mock(Carril.class);
        ArrivalRegistry registry = mock(ArrivalRegistry.class);
        RaceControl control = mock(RaceControl.class);

        when(carril.size()).thenReturn(10);

        Galgo galgo = new Galgo(carril, "Bolt", registry, control);

        galgo.start();
        galgo.interrupt();
        galgo.join();

        verify(carril, atMost(10)).setPasoOn(anyInt());
    }

    @Test
    void deberiaManejarInterrupcionEnRun() throws Exception {
        Carril carril = mock(Carril.class);
        ArrivalRegistry registry = mock(ArrivalRegistry.class);
        RaceControl control = mock(RaceControl.class);

        when(carril.size()).thenReturn(10);
        // Ensure awaitIfPaused throws InterruptedException
        doThrow(new InterruptedException()).when(control).awaitIfPaused();

        Galgo galgo = new Galgo(carril, "InterruptedDog", registry, control);
        galgo.start();
        galgo.join(1000);

        assertFalse(galgo.isAlive(), "Thread should have finished after interruption");
    }
}
