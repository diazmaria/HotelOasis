package es.uca.iw.hoteloasis.domain;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;
import javax.validation.constraints.NotNull;
import javax.persistence.ManyToOne;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(finders = { "findTarifasByHotel" })
public class Tarifa {

    /**
     */
    @NotNull
    private double llamada_nacional;

    /**
     */
    @NotNull
    private double llamada_internacional;

    /**
     */
    @NotNull
    private double internet;

    /**
     */
    @NotNull
    private double cancel_mas_cinco_dias;

    /**
     */
    @NotNull
    private double cancel_dos_cinco_dias;

    /**
     */
    @NotNull
    private double cancel_uno_dos_dias;

    /**
     */
    @NotNull
    private double cancel_mismo_dia;

    /**
     */
    @ManyToOne
    private Hotel hotel;
}
