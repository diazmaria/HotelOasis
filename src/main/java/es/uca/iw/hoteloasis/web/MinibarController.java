package es.uca.iw.hoteloasis.web;
import es.uca.iw.hoteloasis.domain.Minibar;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/minibars")
@Controller
@RooWebScaffold(path = "minibars", formBackingObject = Minibar.class)
public class MinibarController {
}
