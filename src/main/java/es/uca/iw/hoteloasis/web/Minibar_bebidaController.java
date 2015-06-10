package es.uca.iw.hoteloasis.web;
import es.uca.iw.hoteloasis.domain.Minibar_bebida;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/minibar_bebidas")
@Controller
@RooWebScaffold(path = "minibar_bebidas", formBackingObject = Minibar_bebida.class)
public class Minibar_bebidaController {
}
