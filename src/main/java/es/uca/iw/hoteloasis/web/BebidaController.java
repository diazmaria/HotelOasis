package es.uca.iw.hoteloasis.web;
import es.uca.iw.hoteloasis.domain.Bebida;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/bebidas")
@Controller
@RooWebScaffold(path = "bebidas", formBackingObject = Bebida.class)
public class BebidaController {
}
