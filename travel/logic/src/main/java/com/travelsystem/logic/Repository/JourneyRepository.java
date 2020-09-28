package com.travelsystem.logic.Repository;

import com.travelsystem.model.dao.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface JourneyRepository extends JpaRepository<Journey,Long> {

    List<Journey> findAllByPeopleContaining(User user);
    List<Journey> findAllByIsActiveIsTrueAndPeopleContaining(User user);
    List<Journey> findAllByIsActiveIsFalseAndPeopleContaining(User user);
    List<Journey> findAllByOrganizator(User user);
    List<Journey> findAllByOrganizatorAndIsActiveIsTrue(User user);
    @Query("FROM Journey where start_date like %:keyword% and active like true")
    List<Journey>findByStart_dateAndActiveTrue(@Param("keyword") String keyword);

    @Query("FROM Journey  where is_active = TRUE and title like %:title%  or is_active = TRUE and start_position.name like %:start% and title like %:title%  or is_active = TRUE  and  start_date <= :date and title like %:title% or is_active = TRUE and peopleNumber <= :number and title like %:title% "  )
    List<Journey>findByAllParametersStrictCase(@Param("title") String title,@Param("start") String start, @Param("date") Date date, @Param("number") Integer integer);

    @Query("FROM Journey  where  title like %:title%  or start_position.name like %:start% or start_date <= :date or  peopleNumber <= :number " )
    List<Journey>findAllByAllParametersStrictCase(@Param("title") String title,@Param("start") String start, @Param("date") Date date, @Param("number") Integer integer);
    @Query("FROM Journey where is_active = TRUE and is_byInvitation= FALSE and title like  %:title% and start_position.coordinates like %:start% " )
    List<Journey>findByAllParametersOpenStrictCase(@Param("title") String title,@Param("start") String start);
    List<Journey> findAllByOrganizatorAndSuggestedPeopleIsNotNull(User user);
    List<Journey> findAllByByInvitationIsTrueAndSuggestedPeopleContaining(User user);
    List<Journey> findAllByOrganizatorAndRequestedInvitationIsNotNull(User user);
    List<Journey> findAllByByInvitationIsTrueAndRequestedInvitationContaining(User user);
    Journey findJourneyById(Long id);
    List<Journey> findByRouteContaining(Destination destination);
//List<Journey> findByStart_position(Destination destination);
    /*Search functions */

//    List<Journey> findAllByStart_dateOrderByCreate_dateDesc(Date start);

    @Query("FROM Journey where start_position.name like %:keyword% ")
   List<Journey> findAllByStart_position(@Param("keyword") String destination);
    List<Journey> findAllByCategoriesIn(List<Category>categories);
    List<Journey> findAllByByInvitationIsFalse();
    List<Journey> findAllByTitle(String title);
    List<Journey> findDistinctByTagsIn(List<Tag> tags);
//    List<Journey> findDistinctByTagsInAndPeopleInAndIsActiveTrueOrderByCreate_dateDesc(List<Tag> tags,List<User> user);
//    List<Journey> findAllByCategoriesInAndPeopleContainingAndIsActiveFalseOrderByCreate_dateDesc(List<Category>categories,User user);

    List<Journey> findAllByByInvitationIsFalseAndIsActiveTrue();
//    List<Journey> findAllByStart_position(Destination destination);
//List<Journey> findAllByTitleOrPeopleNumberOrstart_dateIgnoreCaseOrderByCreate_dateAsc(String title,int peopleNumber,Date start);
// for admin stuff
    List <Journey> findAll();




}
