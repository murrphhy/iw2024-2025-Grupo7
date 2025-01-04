package grupo7.models;

/**
 * Represents a promoter in the system.
 * A promoter has an ID, name, and position ("cargo").
 */
public class Promoter {

    /**
     * Unique identifier for the promoter. Typically corresponds to an external system ID.
     */
    private String id;

    /**
     * Name of the promoter.
     */
    private String nombre;

    /**
     * Position or role of the promoter.
     */
    private String cargo;

    /**
     * Default constructor for creating an empty Promoter instance.
     */
    public Promoter() {}

    /**
     * Constructs a Promoter with the specified details.
     *
     * @param id     the unique identifier for the promoter
     * @param nombre the name of the promoter
     * @param cargo  the position or role of the promoter
     */
    public Promoter(String id, String nombre, String cargo) {
        this.id = id;
        this.nombre = nombre;
        this.cargo = cargo;
    }

    /**
     * Gets the unique identifier of the promoter.
     *
     * @return the promoter's ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the promoter.
     *
     * @param id the new ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the name of the promoter.
     *
     * @return the promoter's name
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Sets the name of the promoter.
     *
     * @param nombre the new name
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Gets the position or role of the promoter.
     *
     * @return the promoter's position
     */
    public String getCargo() {
        return cargo;
    }

    /**
     * Sets the position or role of the promoter.
     *
     * @param cargo the new position
     */
    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    /**
     * Provides a string representation of the promoter.
     * By default, returns the promoter's name.
     *
     * @return the name of the promoter
     */
    @Override
    public String toString() {
        return nombre;
    }

    /**
     * Generates a display-friendly string combining the promoter's name and position.
     *
     * @return a string in the format "{nombre} - {cargo}"
     */
    public String getDisplayName() {
        return nombre + " - " + cargo;
    }
}
