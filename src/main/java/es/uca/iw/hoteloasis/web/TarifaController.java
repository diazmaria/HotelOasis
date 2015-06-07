package es.uca.iw.hoteloasis.web;
import es.uca.iw.hoteloasis.domain.Tarifa;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/tarifas")
@Controller
@RooWebScaffold(path = "tarifas", formBackingObject = Tarifa.class)
public class TarifaController {
}
