package es.uca.iw.hoteloasis.web;
import es.uca.iw.hoteloasis.domain.Llamada;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/llamadas")
@Controller
@RooWebScaffold(path = "llamadas", formBackingObject = Llamada.class)
public class LlamadaController {
}
