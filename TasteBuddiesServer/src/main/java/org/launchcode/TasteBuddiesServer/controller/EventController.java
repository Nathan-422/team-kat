package org.launchcode.TasteBuddiesServer.controller;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.launchcode.TasteBuddiesServer.data.EventRepository;
import org.launchcode.TasteBuddiesServer.data.RestaurantRepository;
import org.launchcode.TasteBuddiesServer.data.UserRepository;
import org.launchcode.TasteBuddiesServer.models.Event;
import org.launchcode.TasteBuddiesServer.models.Restaurant;
import org.launchcode.TasteBuddiesServer.models.dto.EventDTO;
import org.launchcode.TasteBuddiesServer.models.geocode.TranscriptGC;
import org.launchcode.TasteBuddiesServer.models.nearbySearch.TranscriptNB;
import org.launchcode.TasteBuddiesServer.models.place.TranscriptPlace;
import org.launchcode.TasteBuddiesServer.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/event")
@CrossOrigin(origins = "http://localhost:4200",
        allowCredentials = "true")
public class EventController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventService eventService;

    @Value("${apiKey}")
    private String APIKey;

    @PostMapping("")
    public ResponseEntity<?> getEventFromId(@RequestBody int eventId) {

        System.out.println(eventId);

        if (eventId <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Optional<Event> possibleEvent = eventRepository.findById(eventId);

        if (possibleEvent.isEmpty()) {
            return ResponseEntity.status(204).body(null);
        }

        return ResponseEntity.status(200).body(possibleEvent.get());
    }

    @GetMapping("all")
    public ResponseEntity<?> getAllEvents(HttpServletRequest request) {

        List<Event> possibleEvents = (List<Event>) eventRepository.findAll();

        if (possibleEvents.size() == 0) {
            return ResponseEntity.status(204).body(null);
        }

        return ResponseEntity.status(200).body(eventRepository.findAll());
    }

    @PostMapping("create")
    public ResponseEntity<?> collectRestaurantData(@RequestBody EventDTO eventDTO)
            throws URISyntaxException, IOException, InterruptedException {
        TranscriptGC transcriptGC;
        TranscriptNB transcriptNB;
        TranscriptPlace transcriptPlace;
        Event newEvent = new Event(eventDTO.getLocation(), eventDTO.getSearchRadius(), eventService.generateUniqueEntryCode());

        String URLGC = "https://maps.googleapis.com/maps/api/geocode/json?address=";
        String URLNB = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?type=restaurant&location=";
        String URLPlace = "https://maps.googleapis.com/maps/api/place/details/json?place_id=";
        Gson gson = new Gson();
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(new URI(URLGC+eventDTO.getLocation()+"&key="+APIKey))
                .build();
        HttpResponse<String> getResponse = httpClient
                .send(getRequest, HttpResponse.BodyHandlers.ofString());

        transcriptGC = gson.fromJson(getResponse.body(), TranscriptGC.class);

        String lat = transcriptGC.getResults().get(0).getGeometry().getLocation().getLat();
        String lng = transcriptGC.getResults().get(0).getGeometry().getLocation().getLng();

        HttpRequest getRequestNB = HttpRequest.newBuilder()
                .uri(new URI(URLNB+lat+"%2C"+lng+"&radius="+eventDTO.getSearchRadius()+"&key="+APIKey))
                .build();
        HttpResponse<String> getResponseNB = httpClient.send(getRequestNB, HttpResponse.BodyHandlers.ofString());
        transcriptNB = gson.fromJson(getResponseNB.body(), TranscriptNB.class);

        List<String> place_ids = new ArrayList<>();

        for(int i=0; i < transcriptNB.getResults().size(); i++){
            String place_id = transcriptNB.getResults().get(i).getPlace_id();
            place_ids.add(place_id);
        }

        List<Restaurant> restaurants = new ArrayList<>();

        for(int place_num = 0; place_num < place_ids.size(); place_num++){
            HttpRequest getRequestPlace = HttpRequest.newBuilder()
                    .uri(new URI(URLPlace+place_ids.get(place_num)+"&key="+APIKey))
                    .build();
            HttpResponse<String> getResponsePlace = httpClient.send(getRequestPlace, HttpResponse.BodyHandlers.ofString());
            transcriptPlace = gson.fromJson(getResponsePlace.body(), TranscriptPlace.class);

            String id = place_ids.get(place_num);
            String name = transcriptPlace.getResult().getName();
            String address = transcriptPlace.getResult().getFormatted_address();

            if(!restaurantRepository.existsById(id)) {
                restaurantRepository.save(new Restaurant(id, name, address));
            }
            Optional<Restaurant> result = restaurantRepository.findById(id);
            restaurants.add(result.get());
        }

        newEvent.setAvailableRestaurants(restaurants);
        eventRepository.save(newEvent);

        return ResponseEntity.status(200).build();
    }
}