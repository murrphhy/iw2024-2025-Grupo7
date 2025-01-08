package grupo7.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PromoterApiResponse {
    private List<PromoterData> data;

    public List<PromoterData> getData() {
        return data;
    }

    public void setData(List<PromoterData> data) {
        this.data = data;
    }

    public static class PromoterData {
        @JsonIgnore
        private Long id;

        private String cargo;
        private String nombre;

        // Getters y Setters

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getCargo() {
            return cargo;
        }

        public void setCargo(String cargo) {
            this.cargo = cargo;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }
    }
}
