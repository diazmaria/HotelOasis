package es.uca.iw.hoteloasis.domain;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;
import javax.validation.constraints.NotNull;
import javax.persistence.ManyToOne;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(finders = { "findBebidasByCategoria" })
public class Bebida {

    /**
     */
    @NotNull
    private String nombre;

    /**
     */
    @NotNull
    private double coste;

    /**
     */
    private int cantidad_minibar;

    /**
     */
    @ManyToOne
    private Categoria categoria;
}
