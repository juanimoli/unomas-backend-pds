package com.unomas;

import com.unomas.model.TipoDeporte;
import com.unomas.model.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test básico para verificar el contexto de Spring Boot
 */
@SpringBootTest
class UnoMasApplicationTests {

    @Test
    void contextLoads() {
        // Verificar que el contexto de Spring Boot se carga correctamente
        assertTrue(true);
    }
    
    @Test
    void testUsuarioCreation() {
        Usuario usuario = Usuario.builder()
                .nombreUsuario("testUser")
                .email("test@example.com")
                .contrasena("password")
                .nivelJuego(Usuario.NivelJuego.INTERMEDIO)
                .build();
        
        assertEquals("testUser", usuario.getNombreUsuario());
        assertEquals(Usuario.NivelJuego.INTERMEDIO, usuario.getNivelJuego());
    }
    
    @Test
    void testTipoDeporteValues() {
        assertEquals(11, TipoDeporte.FUTBOL.getJugadoresDefault());
        assertEquals(5, TipoDeporte.FUTBOL_5.getJugadoresDefault());
        assertEquals(5, TipoDeporte.BASQUET.getJugadoresDefault());
    }
}
