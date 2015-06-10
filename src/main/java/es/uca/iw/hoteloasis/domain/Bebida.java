package es.uca.iw.hoteloasis.domain;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;
import javax.validation.constraints.NotNull;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class Bebida {

    /**
     */
    @NotNull
    private String nombre;

    /**
     */
    @NotNull
    private double coste;
}
