package es.uca.iw.hoteloasis.web;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.format.annotation.DateTimeFormat;
import es.uca.iw.hoteloasis.domain.Reserva;
import javax.persistence.ManyToOne;
import es.uca.iw.hoteloasis.domain.Habitacion;
import es.uca.iw.hoteloasis.domain.Usuario;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class Estancia {

    /**
     */
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date fecha_check_in;

    /**
     */
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date fecha_check_out;

    /**
     */
    @ManyToOne
    private Reserva reserva;

    /**
     */
    @ManyToOne
    private Habitacion habitacion;

    /**
     */
    @ManyToOne
    private Usuario usuario;
}
