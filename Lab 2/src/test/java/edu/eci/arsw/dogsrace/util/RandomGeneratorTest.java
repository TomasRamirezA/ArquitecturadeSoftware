package edu.eci.arsw.dogsrace.util;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

class RandomGeneratorTest {

    @Test
    void nextInt_shouldReturnRandomValue() {
        int bound = 10;
        int value = RandomGenerator.nextInt(bound);
        assertTrue(value >= 0 && value < bound, "Random value should be within bounds");
    }

    @Test
    void testPrivateConstructor() throws Exception {
        Constructor<RandomGenerator> constructor = RandomGenerator.class.getDeclaredConstructor();
        assertTrue(java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()), "Constructor should be private");
        constructor.setAccessible(true);
        assertNotNull(constructor.newInstance(), "Should be able to instantiate via reflection (for coverage)");
    }
}
