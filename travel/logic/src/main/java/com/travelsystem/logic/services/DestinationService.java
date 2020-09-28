package com.travelsystem.logic.services;

import com.travelsystem.model.dao.Destination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface DestinationService {
    List<Destination> destinationListUsers();

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    List<Destination> destinationListAdmin();

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    void destinationDelete(Long id);

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    void destinationAdd(Destination destination);

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    void destinationEdit(Destination destination);

    @PreAuthorize("hasAnyAuthority('ADMIN,USER')")
    Destination destinationbyId(Long id);

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    void saveChanges(Destination destination);

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    void saveImage(Destination destination);

    Destination findByCoordinates(String coordinates);


    @PreAuthorize("hasAnyAuthority('ADMIN')")
    boolean NotexistsInDB(String destination);

    @PreAuthorize("hasAnyAuthority('ADMIN,USER')")
    byte[] getDestinationImageById(Long id);

    @PreAuthorize("hasAnyAuthority('ADMIN,USER')")
    List<String>search(String keyword);

    List<Destination> newLocations();
    @PreAuthorize("hasAnyAuthority('ADMIN,USER')")
    List<Destination> findAllByCountryOrNameOrCoordinates(String parameter);
    @PreAuthorize("hasAnyAuthority('ADMIN,USER')")
    List<Destination> findAllByCountryOrNameOrCoordinatesUserFalse(String parameter);
    Page<Destination> findPaginated(Pageable pageable);
    Page<Destination> findPaginatedList(Pageable pageable, List<Destination>all);
}
