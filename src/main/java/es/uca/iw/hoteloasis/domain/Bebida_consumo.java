package es.uca.iw.hoteloasis.domain;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(finders = { "findBebida_consumoesByBebida", "findBebida_consumoesByBebidaAndEstancia" })
public class Bebida_consumo {

    /**
     */
    @NotNull
    private int cantidad_consumida;

    /**
     */
    @ManyToOne
    @NotNull
    private Bebida bebida;

    public Bebida_consumo() {
    }

    public Bebida_consumo(Bebida bebida, int cantidad_consumida, Estancia estancia) {
        this.bebida = bebida;
        this.cantidad_consumida = cantidad_consumida;
        this.estancia = estancia;
    }

    /**
     */
    @ManyToOne
    private Estancia estancia;
}
