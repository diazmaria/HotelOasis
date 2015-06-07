package es.uca.iw.hoteloasis.domain;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import javax.persistence.ManyToOne;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class Hotel {

    /**
     */
    @NotNull
    @Size(max = 30)
    private String nombre;

    /**
     */
    @NotNull
    @Size(min = 4, max = 30)
    private String provincia;

    /**
     */
    @NotNull
    @Size(min = 4, max = 30)
    private String poblacion;

    /**
     */
    @NotNull
    @Size(min = 6, max = 30)
    private String direccion;

    /**
     */
    @NotNull
    @Size(min = 9, max = 20)
    @Pattern(regexp = "[0-9]+")
    private String telefono;

    /**
     */
    @NotNull
    private int estrellas;

    /**
     */
    @NotNull
    private double precio_hab_simple;

    /**
     */
    @NotNull
    private double precio_hab_doble;

    /**
     */
    @NotNull
    private double precio_cama_sup;

    /**
     */
    @NotNull
    private int dias_maximos;

    /**
     */
    @NotNull
    private int dias_antelacion;

    /**
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "hotel")
    private Set<Habitacion> habitaciones = new HashSet<Habitacion>();

    /**
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "hotel")
    private Set<Reserva> reservas = new HashSet<Reserva>();

    /**
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "hotel")
    private Set<Categoria> categorias = new HashSet<Categoria>();

    /**
     */
    @ManyToOne
    private Tarifa tarifa;
}
