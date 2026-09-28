package ec.edu.ucacue.odontocare.clinicalrecord.presentation;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ec.edu.ucacue.odontocare.clinicalrecord.domain.Icd10Code;
import ec.edu.ucacue.odontocare.clinicalrecord.infrastructure.Icd10CodeRepository;

@RestController
@RequestMapping("/api/v1/icd10")
public class Icd10CodeController {

    private final Icd10CodeRepository repository;

    public Icd10CodeController(Icd10CodeRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Icd10Code> search(@RequestParam(defaultValue = "") String query) {
        if (query.isBlank()) {
            return repository.findAll(PageRequest.of(0, 50)).getContent();
        }
        return repository.search(query, PageRequest.of(0, 50));
    }
}
