package com.web_hub.web_hub.admin.announcement;

import com.web_hub.web_hub.admin.AnnouncementRequest;
import com.web_hub.web_hub.admin.AnnouncementResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @PostMapping
    public AnnouncementResponse createAnnouncement(@RequestBody AnnouncementRequest request) {
        return announcementService.createAnnouncement(request);
    }

    @GetMapping
    public List<AnnouncementResponse> getAllAnnouncements() {
        return announcementService.getAllAnnouncements();
    }
}
