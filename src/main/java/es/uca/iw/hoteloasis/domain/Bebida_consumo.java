package es.uca.iw.hoteloasis.domain;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(finders = {"findBebida_consumoesByBebida"})
public class Bebida_consumo {

    /**
     */
    @NotNull
    private int cantidad;

    /**
     */
    @ManyToOne
    @NotNull
    private Bebida bebida;
}
