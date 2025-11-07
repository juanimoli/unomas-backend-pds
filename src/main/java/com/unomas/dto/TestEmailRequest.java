package com.unomas.dto;

public class TestEmailRequest {
    private String email;
    private String asunto;
    private String mensaje;

    public TestEmailRequest() {
    }

    public TestEmailRequest(String email, String asunto, String mensaje) {
        this.email = email;
        this.asunto = asunto;
        this.mensaje = mensaje;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
