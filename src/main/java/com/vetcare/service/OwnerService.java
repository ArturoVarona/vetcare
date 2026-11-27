package com.vetcare.service;

import com.vetcare.domain.Owner;
import com.vetcare.exception.BusinessRule;
import com.vetcare.exception.EntityNotFound;
import com.vetcare.repository.OwnerRepository;
import com.vetcare.repository.PetRepository;
import java.util.List;

public class OwnerService {
    private final OwnerRepository ownerRepository;
    private final PetRepository petRepository;

    public OwnerService(OwnerRepository ownerRepository, PetRepository petRepository) {
        this.ownerRepository = ownerRepository;
        this.petRepository = petRepository;
    }

    public Owner register(String documentId, String fullName, String phone, String email) {
        validate(documentId, fullName, phone);
        if (ownerRepository.findByDocumentId(documentId.trim()).isPresent()) {
            throw new BusinessRule("Ya existe un dueño con el documento " + documentId);
        }
        return ownerRepository.save(new Owner(documentId.trim(), fullName.trim(),
                phone.trim(), blankToNull(email)));
    }

    public Owner findById(long id) {
        return ownerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("No existe el dueño con id " + id));
    }

    public List<Owner> findAll() {
        return ownerRepository.findAll();
    }

    public void update(long id, String documentId, String fullName, String phone, String email) {
        findById(id);
        validate(documentId, fullName, phone);
        ownerRepository.findByDocumentId(documentId.trim()).ifPresent(other -> {
            if (!other.getId().equals(id)) {
                throw new BusinessRule("Ya existe otro dueño con el documento " + documentId);
            }
        });
        ownerRepository.update(new Owner(id, documentId.trim(), fullName.trim(),
                phone.trim(), blankToNull(email)));
    }

    public void delete(long id) {
        findById(id);
        if (!petRepository.findByOwnerId(id).isEmpty()) {
            throw new BusinessRule("No se puede eliminar: el dueño tiene mascotas registradas");
        }
        ownerRepository.deleteById(id);
    }

    private void validate(String documentId, String fullName, String phone) {
        if (documentId == null || documentId.isBlank()) throw new BusinessRule("El documento es obligatorio");
        if (fullName == null || fullName.isBlank()) throw new BusinessRule("El nombre es obligatorio");
        if (phone == null || phone.isBlank()) throw new BusinessRule("El teléfono es obligatorio");
    }

    private String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}