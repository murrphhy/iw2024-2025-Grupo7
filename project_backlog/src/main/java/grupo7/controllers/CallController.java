package grupo7.controllers;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import grupo7.models.Calls;
import grupo7.services.CallService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@AnonymousAllowed
@RestController
@RequestMapping("/api/calls")
public class CallController {

    @Autowired
    private CallService callService;

    // Crear una nueva convocatoria
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Calls createCall(@RequestBody Calls call) {
        return callService.saveCall(call);
    }

    // Leer todas las convocatorias
    @GetMapping
    public List<Calls> getAllCalls() {
        System.out.println("Listando convocatorias a través del endpoint");
        return callService.getAllCalls();
    }

    // Leer una convocatoria por ID
    @GetMapping("/read/{id}")
    public ResponseEntity<Calls> getCallById(@PathVariable Long id) {
        Optional<Calls> call = callService.getCallById(id);
        return call.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Actualizar una convocatoria por ID
    @PutMapping("/update/{id}")
    public ResponseEntity<Calls> updateCall(@PathVariable Long id, @RequestBody Calls callDetails) {
        Optional<Calls> callOptional = callService.getCallById(id);

        if (callOptional.isPresent()) {
            Calls call = callOptional.get();
            call.setName(callDetails.getName());
            call.setTotalBudget(callDetails.getTotalBudget());
            call.setState(callDetails.getState());
            return ResponseEntity.ok(callService.saveCall(call));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar una convocatoria por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCall(@PathVariable Long id) {
        if (callService.getCallById(id).isPresent()) {
            callService.deleteCall(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
