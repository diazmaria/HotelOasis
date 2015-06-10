package es.uca.iw.hoteloasis.domain;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

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
    @NotNull
    @ManyToOne
    private Minibar minibar;
}
