package org.launchcode.TasteBuddiesServer.controller.Services;

import org.launchcode.TasteBuddiesServer.controller.EventCreateRequest;
import org.launchcode.TasteBuddiesServer.data.EventRepository;
import org.launchcode.TasteBuddiesServer.data.UserRepository;
import org.launchcode.TasteBuddiesServer.models.Event;
import org.launchcode.TasteBuddiesServer.models.User;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

//public class EventService {
//
//    @Autowired
//    private EventRepository eventRepository;
//
//    @Autowired
//    UserRepository userRepository;
//
//    public Event createEvent(EventCreateRequest request, String userEmail) {
//        Event event = new Event();
//        event.setLocation(request.getLocation());
//        event.setSearchRadius(request.getSearchRadius());
//
//        String entryCode = generateUniqueEntryCode();
//        event.setEntryCode(entryCode);
//
//        Optional<User> optionalUser = userRepository.findByEmail(userEmail);
//        if (optionalUser.isPresent()) {
//            User user = optionalUser.get();
//            event.getUsers().add(user);
//
//            Event savedEvent = eventRepository.save(event);
//            user.getEvents().add(savedEvent);
//            userRepository.save(user);
//
//            return savedEvent;
//        } else {
//            // handel the case when user is not found, throw exception or return default value
//        }
//
//    }
//
//    private String generateUniqueEntryCode() {
//        //Implement unique entry
//    }
//
//}
