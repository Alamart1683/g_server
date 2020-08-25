package g_server.g_server.application.controller;

import g_server.g_server.application.entity.system_data.Cathedras;
import g_server.g_server.application.service.system_data.CathedrasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
public class CathedrasController {
    @Autowired
    private CathedrasService cathedrasService;

    @GetMapping("/cathedras/all")
    public List<Cathedras> getAll() {
        return cathedrasService.findAll();
    }

    @GetMapping("/cathedras/{id}")
    public Optional<Cathedras> findById(@PathVariable int id) {
        return cathedrasService.findByID(id);
    }

    @PostMapping("/cathedras/save/")
    public void save (@RequestParam String cathedra_name) {
        cathedrasService.save(new Cathedras(cathedra_name));
    }

    @PutMapping("/cathedras/update/")
    public void update (
            @RequestParam int id,
            @RequestParam String cathedra_name
    ) {
        Cathedras cathedras = cathedrasService.findByID(id).get();
        cathedras.setCathedraName(cathedra_name);
        cathedrasService.save(cathedras);
    }

    @DeleteMapping("/cathedras/delete/{id}")
    public void delete(@PathVariable int id) {
        cathedrasService.delete(id);
    }
}
