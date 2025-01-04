package grupo7.services;

import grupo7.models.Promoter;

import java.util.List;

public class PromoterApiResponse {
    private String status;
    private List<Promoter> data;

    // Getters y Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Promoter> getData() {
        return data;
    }

    public void setData(List<Promoter> data) {
        this.data = data;
    }
}
