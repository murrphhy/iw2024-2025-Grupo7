package grupo7.services;

import grupo7.models.Support;
import grupo7.models.keys.SupportId;
import grupo7.repositories.SupportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SupportService {

    @Autowired
    private SupportRepository supportRepository;

    // Leer todos los Support
    public List<Support> getAllSupports() {
        return supportRepository.findAll();
    }

    // Leer un Support por ID compuesto
    public Optional<Support> getSupportById(SupportId supportId) {
        return supportRepository.findById(supportId);
    }

    // Crear o actualizar un Support
    public Support saveSupport(Support support) {
        return supportRepository.save(support);
    }

    // Eliminar un Support por ID combinado
    public void deleteSupport(SupportId supportId) {
        if (supportRepository.existsById(supportId)) {
            supportRepository.deleteById(supportId);
        } else {
            throw new IllegalArgumentException("El Support con el ID especificado no existe.");
        }
    }
}

