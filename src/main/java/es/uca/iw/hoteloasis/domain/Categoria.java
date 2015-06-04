package es.uca.iw.hoteloasis.domain;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;
import javax.validation.constraints.NotNull;
import javax.persistence.ManyToOne;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.OneToMany;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class Categoria {

    /**
     */
    @NotNull
    private String nombre;

    /**
     */
    private String descripcion;

    /**
     */
    @NotNull
    private double precio_categoria;

    /**
     */
    @NotNull
    @ManyToOne
    private Hotel hotel;

    /**
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "categoria")
    private Set<Habitacion> habitaciones = new HashSet<Habitacion>();
}
