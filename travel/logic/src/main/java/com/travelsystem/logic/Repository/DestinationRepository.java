package com.travelsystem.logic.Repository;

import com.travelsystem.model.dao.Category;
import com.travelsystem.model.dao.Destination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sun.security.krb5.internal.crypto.Des;

import java.util.List;
import java.util.Optional;

@Repository
public interface DestinationRepository extends JpaRepository<Destination,Long> {
    Destination findByCoordinates(String coordinates);
    Optional<Destination> findDestinationByCoordinates(String coordinates);
    List<Destination>findAllByCreatedByUserIsFalse();
    Destination findDestinationById(Long id);

    List<Destination> findAllByCreatedByUserIsFalseAndCoordinates(String address);
//    List<Destination>findAllByCreatedByUserIsFalseAndCategoriesContaining(List<String>categories);
    Destination findByName(String name);
    List<Destination>findAllByCreatedByUserIsFalseOrderByCreatedDesc();


List<Destination> findByCountryOrNameOrCoordinatesIgnoreCase(String country, String name, String coordinates);
    @Query("SELECT name FROM Destination where name like %:keyword% AND createdByUser = false")
    List<String> search(@Param("keyword") String keyword);

    @Query("FROM Destination where UPPER(name) like UPPER('%' ||:keyword||'%') AND createdByUser = false")
    List<Destination> searchLocations(@Param("keyword") String keyword);

    List<Destination> findByCountry(String country);
    List<Destination> findAllByNameOrAndCountryOrCoordinatesIgnoreCase(String name,String country,String coordinates);
    List<Destination> findAllByNameOrAndCountryOrCoordinatesAndCreatedByUserFalseIgnoreCase(String name,String country,String coordinates);

    List<Destination> findAllByCategoriesIn(List<Category>categories);
}
