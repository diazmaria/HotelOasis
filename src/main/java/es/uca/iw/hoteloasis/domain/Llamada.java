package es.uca.iw.hoteloasis.domain;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;
import javax.persistence.ManyToOne;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(finders = { "findLlamadasByEstancia" })
public class Llamada {

    /**
     */
    private double minutos_nacionales;

    /**
     */
    private double minutos_internacionales;

    /**
     */
    @ManyToOne
    private Estancia estancia;

    /**
     */
    private double minutos_internet;
}
