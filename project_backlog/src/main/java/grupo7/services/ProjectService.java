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

        String newState = getNextState(oldState);
        project.setState(newState);

        // Save the project
        Project savedProject = projectRepository.save(project);

        // If there's a real state change, handle transitions (send emails, etc.)
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

        // Handle logic for each state transition
        switch (newState) {
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
    private String getNextState(String oldState) {
        if (oldState == null || oldState.isEmpty()) {
            return "presentado";
        }
        switch (oldState) {
            case "presentado":
                return "alineado";
            case "alineado":
                return "evaluado";
            default:
                // If it's already "evaluado" (or any other state like "aceptado"/"rechazado"),
                // we leave it as is.
                return oldState;
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


}
