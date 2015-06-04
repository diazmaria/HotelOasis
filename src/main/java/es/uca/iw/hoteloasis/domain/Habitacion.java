package es.uca.iw.hoteloasis.domain;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;
import javax.validation.constraints.NotNull;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class Habitacion {

    /**
     */
    @NotNull
    private int numero;

    /**
     */
    @NotNull
    @Enumerated
    private Habitacion_tipo tipo;

    /**
     */
    @Enumerated
    private Habitacion_estado estado;

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
}
