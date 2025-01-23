package grupo7.services;

import grupo7.models.AppUser;
import grupo7.models.Project;
import grupo7.models.Role;
import grupo7.repositories.ProjectRepository;
import grupo7.repositories.UserRepository;
import grupo7.repositories.TechnicianProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Service for managing projects.
 * Provides functionality for saving, retrieving, and deleting projects,
 * as well as handling email notifications based on project state transitions.
 */
@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    /**
     * Retrieves all projects stored in the database.
     *
     * @return a list of all projects.
     */
    private TechnicianProjectRepository technicianProjectRepository;

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    /**
     * Retrieves a project by its ID.
     *
     * @param projectId the ID of the project to retrieve.
     * @return an Optional containing the project if it exists, or empty if it does not.
     */
    public Optional<Project> getProjectById(Long projectId) {
        return projectRepository.findById(projectId);
    }

    /**
     * Retrieves a project by its title.
     *
     * @param title the title of the project to retrieve.
     * @return an Optional containing the project if it exists, or empty if it does not.
     */
    public Optional<Project> getProjectByTitle(String title) {
        return projectRepository.findByTitle(title);
    }

    public boolean determineIfAccepted(Project project) {
        return "aceptado".equalsIgnoreCase(project.getState());
    }

    /**
     * Saves a project to the database.
     * If the project's state changes, it notifies relevant users via email.
     *
     * @param project the project to save.
     * @return the saved project.
     */
    public Project saveProject(Project project) {
        AtomicReference<String> oldStateRef = new AtomicReference<>(null);
        if (project.getId() != null) {
            projectRepository.findById(project.getId())
                    .ifPresent(existing -> oldStateRef.set(existing.getState()));
        }
        String oldState = oldStateRef.get();
    
        boolean accepted = determineIfAccepted(project);
    
        String newState = getNextState(oldState, accepted);
        project.setState(newState);
    
        Project savedProject = projectRepository.save(project);
    
        if (newState != null && !newState.equals(oldState)) {
            handleStateTransition(oldState, newState, savedProject);
        }
    
        return savedProject;
    }
    

    /**
     * Handles project state transitions and sends email notifications as appropriate.
     *
     * @param oldState the previous state of the project.
     * @param newState the new state of the project.
     * @param project  the project whose state has changed.
     */
    private void handleStateTransition(String oldState, String newState, Project project) {
        // Get relevant users (CIO and Technical)
        AppUser cioUser = userRepository.findByRole(Role.CIO)
                .orElseThrow(() -> new RuntimeException("No user with role ADMINISTRATOR found"));

        AppUser applicant = project.getApplicantId();
        Optional<AppUser> promoter = userRepository.findByUsername(project.getPromoterId());

        switch (newState) {
            case "esperando aval":


                if (promoter.isPresent() && !"esperando aval".equals(oldState)) {
                    AppUser promoterUser = promoter.get();
                    String subject = "Nuevo proyecto para avalar";
                    String body = String.format(
                            "Hola %s,\n\n" +
                                    "El usuario %s ha presentado un proyecto titulado '%s'.\n" +
                                    "El usuario ha requerido tu aval para el proyecto.\n\n" +
                                    "Saludos,\nTu aplicación",
                            promoterUser.getUsername(),
                            (applicant != null ? applicant.getUsername() : "solicitante"),
                            project.getTitle());
                    emailService.sendEmail(promoterUser.getEmail(), subject, body);
                } else if (promoter.isEmpty()) {
                    System.err.println("No se encontró el promotor");
                }
                break;

            case "presentado":
                // Notify CIO if the state changes to "presentado"
                if (!"presentado".equals(oldState)) {
                    String subject = "Nuevo proyecto para evaluar";
                    String body = String.format(
                            "Hola %s,\n\n" +
                                    "El usuario %s ha presentado un proyecto titulado '%s'.\n" +
                                    "Por favor, revísalo y procede con su evaluación.\n\n" +
                                    "Saludos,\nTu aplicación",
                            cioUser.getUsername(),
                            (applicant != null ? applicant.getUsername() : "desconocido"),
                            project.getTitle());
                    emailService.sendEmail(cioUser.getEmail(), subject, body);

                    if (applicant != null && applicant.getEmail() != null) {
                        String subjectUser = "Estado de tu proyecto: PRESENTADO";
                        String bodyUser = String.format(
                                "Hola %s,\n\n" +
                                        "Tu proyecto '%s' ha sido avalado por '%s'. Tu proceso pasa a ser evaluado por el CIO.\n\n" +
                                        "Saludos,\nTu aplicación",
                                applicant.getUsername(),
                                project.getTitle(),
                                promoter.isPresent() ? promoter.get().getUsername() : "tu promotor seleccionado");
                        emailService.sendEmail(applicant.getEmail(), subjectUser, bodyUser);
                    }
                }
                break;

            case "alineado":
                AppUser techUser = userRepository.findByRole(Role.TECHNICAL)
                        .orElseThrow(() -> new RuntimeException("No user with role TECHNICAL found"));
                // Notify the technical user and applicant if the state changes to "alineado"
                if (!"alineado".equals(oldState)) {
                    String subjectTech = "Proyecto pendiente de evaluación técnica";
                    String bodyTech = String.format(
                            "Hola %s,\n\n" +
                                    "Se ha alineado un proyecto titulado '%s'.\n" +
                                    "Por favor, revísalo para completar su evaluación técnica.\n\n" +
                                    "Saludos,\nTu aplicación",
                            techUser.getUsername(),
                            project.getTitle());
                    emailService.sendEmail(techUser.getEmail(), subjectTech, bodyTech);

                    if (applicant != null && applicant.getEmail() != null) {
                        String subjectUser = "Estado de tu proyecto: ALINEADO";
                        String bodyUser = String.format(
                                "Hola %s,\n\n" +
                                        "Tu proyecto '%s' ha sido revisado por el CIO y ahora se encuentra en fase de evaluación técnica.\n\n" +
                                        "Saludos,\nTu aplicación",
                                applicant.getUsername(),
                                project.getTitle());
                        emailService.sendEmail(applicant.getEmail(), subjectUser, bodyUser);
                    }
                }
                break;

            case "evaluado":
                // Notify the CIO and applicant if the state changes to "evaluado"
                if (!"evaluado".equals(oldState)) {
                    String subjectCio = "Proyecto en fase de decisión";
                    String bodyCio = String.format(
                            "Hola %s,\n\n" +
                                    "El proyecto titulado '%s' ya ha sido evaluado por el técnico.\n" +
                                    "Por favor, decide si se acepta o se rechaza.\n\n" +
                                    "Saludos,\nTu aplicación",
                            cioUser.getUsername(),
                            project.getTitle());
                    emailService.sendEmail(cioUser.getEmail(), subjectCio, bodyCio);

                    if (applicant != null && applicant.getEmail() != null) {
                        String subjectUser = "Estado de tu proyecto: EVALUADO (fase final)";
                        String bodyUser = String.format(
                                "Hola %s,\n\n" +
                                        "Tu proyecto '%s' ha sido evaluado por el técnico y se encuentra en la fase final.\n" +
                                        "En breve, el CIO decidirá si se acepta o se rechaza.\n\n" +
                                        "Saludos,\nTu aplicación",
                                applicant.getUsername(),
                                project.getTitle());
                        emailService.sendEmail(applicant.getEmail(), subjectUser, bodyUser);
                    }
                }
                break;

            case "aceptado":
                // Notify the applicant if the state changes to "aceptado"
                if (!"aceptado".equals(oldState)) {
                    if (applicant != null && applicant.getEmail() != null) {
                        String subject = "Tu proyecto ha sido aceptado";
                        String body = String.format(
                                "Hola %s,\n\n" +
                                        "¡Enhorabuena! Tu proyecto '%s' ha sido aceptado por el CIO.\n\n" +
                                        "Saludos,\nTu aplicación",
                                applicant.getUsername(),
                                project.getTitle());
                        emailService.sendEmail(applicant.getEmail(), subject, body);
                    }
                }
                break;

            case "rechazado":
                // Notify the applicant if the state changes to "rechazado"
                if (!"rechazado".equals(oldState)) {
                    if (applicant != null && applicant.getEmail() != null) {
                        String subject = "Tu proyecto ha sido rechazado";
                        String body = String.format(
                                "Hola %s,\n\n" +
                                        "Lamentablemente, tu proyecto '%s' ha sido rechazado por el CIO.\n\n" +
                                        "Saludos,\nTu aplicación",
                                applicant.getUsername(),
                                project.getTitle());
                        emailService.sendEmail(applicant.getEmail(), subject, body);
                    }
                }
                break;

            default:
                break;
        }
    }

    /**
     * Determines the next state based on the old state,
     * in a simple linear flow:
     * null/"" -> "presentado" -> "alineado" -> "evaluado"
     *
     * "aceptado" or "rechazado" is not handled here;
     * those are manual decisions by the CIO.
     *
     * @param oldState the previous state (may be null or empty if new)
     * @return the new state to assign
     */
    public String getNextState(String oldState, boolean accepted) {
        if (oldState == null || oldState.isEmpty()) {
            return "esperando aval";  // El primer estado es "esperando aval"
        }
        switch (oldState) {
            case "esperando aval":
                return "presentado";
            case "presentado":
                return "alineado";  // De "presentado" a "alineado"
            case "alineado":
                return "evaluado";  // De "alineado" a "evaluado"
            case "evaluado":
                // Aquí es donde decides si el proyecto es "aceptado" o "no aceptado"
                // El estado cambiaría en función de la acción tomada
                // Si el proyecto es rechazado, cambiar a "No Aceptado"
                return accepted ? "aceptado" : "no aceptado";  // Este estado no se modifica por sí solo, se actualiza con una acción de aceptación o rechazo
            case "aceptado":
                return oldState;  // Si ya está "aceptado", no cambia
            case "no aceptado":
                return oldState;  // Si ya está "no aceptado", no cambia
            default:
                return oldState;  // Otros estados no cambiantes
        }
    }
    
    

    /**
     * Deletes a project by its ID.
     *
     * @param projectId the ID of the project to delete.
     */
    public void deleteProject(Long projectId) {
        projectRepository.deleteById(projectId);
    }

    /**
     * Retrieves projects associated with a specific user ID.
     *
     * @param userId the ID of the user.
     * @return a list of projects associated with the user.
     */
    public List<Project> getProjectsByUserId(Long userId) {
        return projectRepository.findAll().stream()
                .filter(project -> project.getApplicantId() != null && project.getApplicantId().getId().equals(userId))
                .toList();
    }


}
