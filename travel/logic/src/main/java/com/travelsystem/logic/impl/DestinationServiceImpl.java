package com.travelsystem.logic.impl;

import com.travelsystem.logic.Repository.DestinationRepository;
import com.travelsystem.logic.Repository.JourneyRepository;
import com.travelsystem.logic.services.DestinationService;
import com.travelsystem.model.dao.Destination;
import com.travelsystem.model.dao.Journey;
import lombok.Data;
import lombok.extern.apachecommons.CommonsLog;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.travelsystem.logic.CONSTANTS.HEIGHT;
import static com.travelsystem.logic.CONSTANTS.WIDTH;

@Data
@CommonsLog
@Service
@Transactional
public class DestinationServiceImpl implements DestinationService {

    final
    DestinationRepository destinationRepository;
    final JourneyRepository journeyRepository;

    @Autowired
    public DestinationServiceImpl(DestinationRepository destinationRepository, JourneyRepository journeyRepository) {
        this.destinationRepository = destinationRepository;
        this.journeyRepository = journeyRepository;
    }


    public List<Destination> destinationListUsers(){
        return destinationRepository.findAllByCreatedByUserIsFalse();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public List<Destination> destinationListAdmin(){
        return destinationRepository.findAll();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public void destinationDelete(Long id){
        Destination empty = new Destination();
        Destination destination = destinationRepository.findDestinationById(id);
        List<Journey> journeys = journeyRepository.findByRouteContaining(destination);
        for (Journey journey : journeys){
            if(journey.getStart_position() == destination){
                journey.setStart_position(empty);
                journeyRepository.save(journey);

            }
            journey.getRoute().remove(destination);
            journeyRepository.save(journey);
        }

        List <Journey> journeyList = journeyRepository.findAllByStart_position(destination.getCoordinates());

        journeyList.stream().forEach(x ->{x.setStart_position(empty);journeyRepository.save(x);});

         destinationRepository.deleteById(id);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public void destinationAdd(Destination destination){
        destination.setCreatedByUser(false);
        destination.setCreated(new Date());
        destinationRepository.save(destination);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public void destinationEdit(Destination destination){
        destination.setCreated(new Date());
        destinationRepository.save(destination);
    }

    @Override
    public Destination destinationbyId(Long id) {
        return destinationRepository.findDestinationById(id);
    }

    @Override
    public void saveChanges(Destination destination) {
        Destination pic = destinationRepository.findDestinationById(destination.getId());
        destination.setCreated(new Date());
        destination.setThumbnail(pic.getThumbnail());
        destinationRepository.save(destination);
    }

    public void saveImage(Destination destination) {
        destination.setCreated(new Date());
        Destination updatedDestination = destinationRepository.findDestinationById(destination.getId());
        resizeImage(destination);
        updatedDestination.setThumbnail(destination.getThumbnail());
        destinationRepository.save(updatedDestination);
    }

    @Override
    public Destination findByCoordinates(String coordinates) {
        return destinationRepository.findByCoordinates(coordinates);
    }

    @Override
    public boolean NotexistsInDB(String destination) {
        return destinationRepository.findByName(destination) == null;
    }


    private void resizeImage(Destination destination) {
        try (InputStream in = new ByteArrayInputStream(destination.getThumbnail())) {
            BufferedImage img = ImageIO.read(in);
            BufferedImage thumbnailImage = Scalr.resize(img, Scalr.Mode.FIT_EXACT, WIDTH, HEIGHT);

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                //Need to choose formats by itself
                ImageIO.write(thumbnailImage, "png", baos);
                baos.flush();
                destination.setThumbnail(baos.toByteArray());
            }
        } catch (IOException e) {
            log.error("From bytes to bufferImage in  thumbnail()  -> ");
        }
    }
    @PreAuthorize("hasAnyAuthority('ADMIN,USER')")
    public byte[] getDestinationImageById(Long id) {

        Destination ob = destinationRepository.findDestinationById(id);
       if(ob.getThumbnail()!=null){
           return ob.getThumbnail();
       }else{
           return null;
       }
    }

    @Override
    public List<String> search(String keyword) {
        return destinationRepository.search(keyword);
    }

    @Override
    public List<Destination> newLocations() {
        List<Destination> destinations = destinationRepository.findAllByCreatedByUserIsFalseOrderByCreatedDesc();
        List <Destination> show = new ArrayList<>();
        if (destinations.size()>3){
            for( int i =0; i<3; i++)
                show.add(destinations.get(i));
        return show;
        }else {
            return destinationRepository.findAllByCreatedByUserIsFalseOrderByCreatedDesc();
        }
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<Destination> findAllByCountryOrNameOrCoordinates(String parameter) {
        return destinationRepository.findByCountryOrNameOrCoordinatesIgnoreCase(parameter,parameter,parameter);
    }
    @Override
    public List<Destination> findAllByCountryOrNameOrCoordinatesUserFalse(String parameter) {
        return destinationRepository.findAllByNameOrAndCountryOrCoordinatesAndCreatedByUserFalseIgnoreCase(parameter,parameter,parameter);
    }



    public Page<Destination> findPaginated(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Destination> list;
        List<Destination> all = destinationRepository.findAllByCreatedByUserIsFalse(); //only created by admin

        if (all.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, all.size());
            list = all.subList(startItem, toIndex);
        }

        Page<Destination> destinationsPage
                = new PageImpl<Destination>(list, PageRequest.of(currentPage, pageSize), all.size());

        return destinationsPage;
    }

    public Page<Destination> findPaginatedList(Pageable pageable, List<Destination>all) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Destination> list;

        if (all.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, all.size());
            list = all.subList(startItem, toIndex);
        }

        Page<Destination> destinationsPage
                = new PageImpl<Destination>(list, PageRequest.of(currentPage, pageSize), all.size());

        return destinationsPage;
    }


}
