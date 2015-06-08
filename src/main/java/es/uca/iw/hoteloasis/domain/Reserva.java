package es.uca.iw.hoteloasis.domain;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(finders = { "findHabitacionesDisponibles" })
public class Reserva {

    /**
     */
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date fecha_reserva;

    /**
     */
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date fecha_entrada;

    /**
     */
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date fecha_salida;

    /**
     */
    @NotNull
    @Enumerated
    private Habitacion_tipo tipo;

    /**
     */
    private Boolean cama_supletoria;

    /**
     */
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date fecha_cancelacion;

    /**
     */
    @NotNull
    @ManyToOne
    private Hotel hotel;

    /**
     */
    @NotNull
    @ManyToOne
    private Categoria categoria;

    /**
     */
    private double coste_reserva;
    
    /**
     */
    @ManyToOne
    private Usuario usuario;
    
    
    
    //CONSTRUCTORES
    public Reserva() {
    }
    
    public Reserva(Date fecha_reserva, Date fecha_entrada, Date fecha_salida, Boolean cama_supletoria, Hotel hotel, Categoria categoria, Habitacion_tipo tipo, Usuario usuario, double coste_reserva) {
        this.fecha_reserva = fecha_reserva;
        this.fecha_entrada = fecha_entrada;
        this.fecha_salida = fecha_salida;
        this.cama_supletoria = cama_supletoria;
        this.hotel = hotel;
        this.categoria = categoria;
        this.tipo = tipo;
        this.usuario = usuario;
        this.coste_reserva = coste_reserva;
    }
    
    
    // BUSCADOR PERSONLIZADO DE HABITACIONES DISPONIBLES
    public static long findHabitacionesDisponibles(Hotel hotel, Categoria categoria, Habitacion_tipo tipo, Date fecha_entrada, Date fecha_salida) {
        EntityManager em = Habitacion.entityManager();
        //Contar las habitaciones totales que tiene el hotel de dicha categoria y tipo
        String query = "SELECT COUNT(*) " + "FROM Habitacion " + "WHERE hotel = :hotel AND categoria = :categoria AND tipo = :tipo";
        TypedQuery<Long> tQuery = em.createQuery(query, Long.class);
        tQuery.setParameter("hotel", hotel);
        tQuery.setParameter("categoria", categoria);
        tQuery.setParameter("tipo", tipo);
        long habitacionesTotales = tQuery.getSingleResult();
        
        //Contar las habitaciones que tiene el hotel reservadas de dicha categoria y tipo para esas fechas
        query = "SELECT COUNT(*) " + "FROM Reserva " + "WHERE hotel = :hotel AND categoria = :categoria AND tipo = :tipo AND fecha_cancelacion IS NULL " + "AND ((fecha_entrada BETWEEN :fecha_entrada AND :fecha_salida) " + "OR (fecha_salida BETWEEN :fecha_entrada AND :fecha_salida) " + "OR (:fecha_entrada BETWEEN fecha_entrada AND fecha_salida) " + "OR (:fecha_salida BETWEEN fecha_entrada AND fecha_salida))";
        tQuery = em.createQuery(query, Long.class);
        tQuery.setParameter("hotel", hotel);
        tQuery.setParameter("fecha_entrada", fecha_entrada);
        tQuery.setParameter("fecha_salida", fecha_salida);
        tQuery.setParameter("categoria", categoria);
        tQuery.setParameter("tipo", tipo);
        long habitacionesReservadas = tQuery.getSingleResult();
        long habitacionesDisponibles = (habitacionesTotales - habitacionesReservadas);
        return habitacionesDisponibles;
    }
}
