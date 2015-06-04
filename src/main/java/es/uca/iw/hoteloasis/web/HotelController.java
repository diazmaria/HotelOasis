package es.uca.iw.hoteloasis.web;
import es.uca.iw.hoteloasis.domain.Hotel;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/hotels")
@Controller
@RooWebScaffold(path = "hotels", formBackingObject = Hotel.class)
public class HotelController {
}
