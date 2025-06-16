package ru.netology.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.dto.FileResponse;
import ru.netology.entities.File;
import ru.netology.services.StorageService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
public class StorageController {

    private final StorageService storageService;

    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/list")
    public ResponseEntity<List<FileResponse>> getAllFiles(@RequestHeader("auth-token") String authToken,
                                                          @RequestParam("limit") int limit) {
        return ResponseEntity.ok(storageService.getFiles(authToken, limit));
    }

    @PostMapping("/file")
    public ResponseEntity<Void> uploadFile(@RequestHeader("auth-token") String authToken,
                                           @RequestParam("filename") String filename,
                                           @RequestParam("file") MultipartFile file) throws IOException {
        storageService.uploadFile(authToken, filename, file);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/file")
    public ResponseEntity<Void> renameFile(@RequestHeader("auth-token") String authToken,
                                           @RequestParam("filename") String filename,
                                           @RequestBody Map<String, String> fileNameRequest) {
        String newName = fileNameRequest.get("filename");
        storageService.renameFile(authToken, filename, newName);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/file")
    public ResponseEntity<Void> deleteFile(@RequestHeader("auth-token") String authToken,
                                           @RequestParam("filename") String filename) {
        storageService.deleteFile(authToken, filename);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/file")
    public ResponseEntity<byte[]> downloadFile(@RequestHeader("auth-token") String authToken,
                                               @RequestParam("filename") String filename) {
        File file = storageService.downloadFile(authToken, filename);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file.getContent());
    }
}