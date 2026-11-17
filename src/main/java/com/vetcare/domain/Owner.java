package com.vetcare.domain;

public class Owner {
    private Long id;
    private final String documentId;
    private final String fullName;
    private final String phone;
    private final String email;

    public Owner(Long id, String documentId, String fullName, String phone, String email) {
        this.id = id;
        this.documentId = documentId;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
    }

    public Long getId() { return id; }
    public String getDocumentId() { return documentId; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
}