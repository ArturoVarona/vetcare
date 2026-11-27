package com.vetcare.repository;

import com.vetcare.domain.Owner;
import java.util.List;
import java.util.Optional;

public interface OwnerRepository {
    Owner save(Owner owner);
    Optional<Owner> findById(long id);
    Optional<Owner> findByDocumentId(String documentId);
    List<Owner> findAll();
    void update(Owner owner);
    void deleteById(long id);
}