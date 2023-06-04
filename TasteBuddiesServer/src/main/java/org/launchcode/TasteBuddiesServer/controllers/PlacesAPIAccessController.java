package org.launchcode.TasteBuddiesServer.controllers;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.launchcode.TasteBuddiesServer.services.PlaceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.nio.file.Files;

@RestController
@RequestMapping("/api/places")
@CrossOrigin(
        origins = "http://localhost:4200",
        allowCredentials = "true")
public class PlacesAPIAccessController {
    private final PlaceService placeService;

    @Value("${apiKey}")
    private String APIKey;

    public PlacesAPIAccessController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping("restaurant")
    public ResponseEntity<?> getPlaceFromPlacesAPI(
            @RequestParam String placeID
    ) {
        try {
            return ResponseEntity
                    .status(200)
                    .body(placeService.getRestaurantFromPlaceID(placeID));
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity
                    .status(400)
                    .build();
        }
    }

    /**
     *
     * @param photo_reference - Places API photo reference
     * @param maxwidth int - optional, but at least one of the dimension parameters are required
     * @param maxheight - int - optional, but at least one of the dimension parameters are required
     * @return
     *
     * NOT YET WORKING. I'm still trying to get this image passed properly. Right now this will handle pngs, sort of.
     */
    @GetMapping(
            value = "image",
            produces = MediaType.IMAGE_PNG_VALUE
    )
    public ResponseEntity<?> getImageFromPlacesAPI(
            @RequestParam String photo_reference,
            @RequestParam(required = false) Integer maxwidth,
            @RequestParam(required = false) Integer maxheight
    ) {
        if (maxheight == null && maxwidth == null) {
            return ResponseEntity.status(400).build();
        }

        if (maxwidth == null) {
            maxwidth = 0;
        }

        if (maxheight == null) {
            maxheight = 0;
        }

        try {
            ByteArrayResource resource = new ByteArrayResource(placeService.getImageFromPhotoReference(photo_reference, maxheight, maxwidth));
            return ResponseEntity.status(200).body(resource);
        } catch (Exception e) {
            System.out.println(e);
        }

        return ResponseEntity.status(500).build();

    }


}
