package com.unomas.controller;

import com.unomas.adapter.EmailServiceAdapter;
import com.unomas.dto.TestEmailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class NotificacionTestController {

    @Autowired
    private EmailServiceAdapter emailService;

    @PostMapping("/email")
    public ResponseEntity<String> enviarEmailPrueba(@RequestBody TestEmailRequest request) {
        try {
            emailService.enviarNotificacion(
                request.getEmail(),
                request.getAsunto(),
                request.getMensaje()
            );
            return ResponseEntity.ok("Email enviado exitosamente a: " + request.getEmail());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error al enviar email: " + e.getMessage());
        }
    }

    @GetMapping("/email/disponible")
    public ResponseEntity<Boolean> verificarDisponibilidad() {
        boolean disponible = emailService.isDisponible();
        return ResponseEntity.ok(disponible);
    }
}
