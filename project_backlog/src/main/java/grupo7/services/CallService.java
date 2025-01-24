package grupo7.services;

import grupo7.models.Calls;
import grupo7.repositories.CallRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CallService {
    
    @Autowired
    private CallRepository callRepository;
    public CallService(CallRepository callsRepository) {
        this.callRepository = callsRepository;
    }
    // Obtener todas las convocatorias
    public List<Calls> getAllCalls() {
        return callRepository.findAll();
    }


    public List<Calls> findAll() {
        return callRepository.findAllWithProjects();
    }

    // Obtener una convocatoria por ID
    public Optional<Calls> getCallById(Long id) {
        return callRepository.findById(id);
    }

    // Guardar o actualizar una convocatoria
    public Calls saveCall(Calls call) {
        return callRepository.save(call);
    }

    // Eliminar una convocatoria por ID
    public void deleteCall(Long id) {
        callRepository.deleteById(id);
    }
}
