package com.unomas.observer;

import com.unomas.model.Partido;
import com.unomas.model.Usuario;
import com.unomas.strategy.notificacion.IStrategiaNotificacion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listener concreto para eventos de Partido.
 * Utiliza una estrategia de notificación (Strategy pattern) para enviar notificaciones.
 * 
 * Patrones: Observer + Strategy
 * Rol Observer: Concrete Observer/Listener
 * Rol Strategy: Context que delega a IStrategiaNotificacion
 */
public class PartidoListener implements IListener {
    
    private static final Logger logger = LoggerFactory.getLogger(PartidoListener.class);
    
    private final Usuario usuario;
    private final IStrategiaNotificacion estrategiaNotificacion;
    
    /**
     * Constructor del listener.
     * @param usuario el usuario que será notificado
     * @param estrategiaNotificacion la estrategia a usar para enviar notificaciones
     */
    public PartidoListener(Usuario usuario, IStrategiaNotificacion estrategiaNotificacion) {
        this.usuario = usuario;
        this.estrategiaNotificacion = estrategiaNotificacion;
    }
    
    @Override
    public void notificar(Object observable) {
        if (observable instanceof Partido partido) {
            logger.info("PartidoListener: Notificando a usuario {} sobre cambios en partido {}", 
                       usuario.getNombreUsuario(), partido.getId());
            
            // Delegar a la estrategia de notificación
            estrategiaNotificacion.enviarNotificacion(usuario, partido);
        } else {
            logger.warn("PartidoListener recibió un observable que no es un Partido: {}", 
                       observable.getClass().getName());
        }
    }
    
    public Usuario getUsuario() {
        return usuario;
    }
    
    public IStrategiaNotificacion getEstrategiaNotificacion() {
        return estrategiaNotificacion;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        PartidoListener that = (PartidoListener) o;
        
        // Dos listeners son iguales si notifican al mismo usuario con la misma estrategia
        return usuario.getId().equals(that.usuario.getId()) &&
               estrategiaNotificacion.getClass().equals(that.estrategiaNotificacion.getClass());
    }
    
    @Override
    public int hashCode() {
        int result = usuario.getId().hashCode();
        result = 31 * result + estrategiaNotificacion.getClass().hashCode();
        return result;
    }
}
