package com.mini_crm.main.dto.customer;

public class CustomerDTO {
    private String name;
    private String phone;
    private String email;
    private String company;
    private String notes;
    private Long saleId;

    public CustomerDTO() {
    }

    public CustomerDTO(String name, String phone, String email, String company, String notes, Long saleId) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.company = company;
        this.notes = notes;
        this.saleId = saleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Long getSaleId() {
        return saleId;
    }

    public void setSaleId(Long saleId) {
        this.saleId = saleId;
    }

    @Override
    public String toString() {
        return "CustomerDTO{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", company='" + company + '\'' +
                ", notes='" + notes + '\'' +
                ", saleId=" + saleId +
                '}';
    }
}
