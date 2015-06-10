package es.uca.iw.hoteloasis.web;
import es.uca.iw.hoteloasis.domain.Bebida_consumo;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/bebida_consumoes")
@Controller
@RooWebScaffold(path = "bebida_consumoes", formBackingObject = Bebida_consumo.class)
public class Bebida_consumoController {
}
