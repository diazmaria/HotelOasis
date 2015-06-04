package es.uca.iw.hoteloasis.web;
import es.uca.iw.hoteloasis.domain.Habitacion;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/habitacions")
@Controller
@RooWebScaffold(path = "habitacions", formBackingObject = Habitacion.class)
public class HabitacionController {
}
