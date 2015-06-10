package es.uca.iw.hoteloasis.domain;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

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
    @ManyToOne
    private Tarifa tarifa; 
    
    /**
     */

}
