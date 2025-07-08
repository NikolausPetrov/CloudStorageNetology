package ru.netology.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.dto.FileResponse;
import ru.netology.entities.File;
import ru.netology.security.JwtTokenUtils;
import ru.netology.services.StorageService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
public class StorageController {

    private static final Logger logger = LoggerFactory.getLogger(StorageController.class);

    private final StorageService storageService;
    private final JwtTokenUtils jwtTokenUtils;

    public StorageController(StorageService storageService, JwtTokenUtils jwtTokenUtils) {
        this.storageService = storageService;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    @GetMapping("/list")
    public ResponseEntity<List<FileResponse>> getAllFiles(@RequestHeader("auth-token") String authToken,
                                                          @RequestParam("limit") int limit) {
        String owner = jwtTokenUtils.getUsernameFromToken(authToken);
        logger.info("Получение списка файлов для пользователя: {}, лимит: {}", owner, limit);
        
        List<File> files = storageService.getFiles(owner, limit);
        List<FileResponse> fileResponses = files.stream()
                .map(file -> new FileResponse(file.getFilename(), file.getSize()))
                .collect(Collectors.toList());
        
        logger.info("Найдено файлов: {}", fileResponses.size());
        return ResponseEntity.ok(fileResponses);
    }

    @PostMapping("/file")
    public ResponseEntity<Void> uploadFile(@RequestHeader("auth-token") String authToken,
                                           @RequestParam("filename") String filename,
                                           @RequestParam("file") MultipartFile file) throws IOException {
        String owner = jwtTokenUtils.getUsernameFromToken(authToken);
        logger.info("Загрузка файла: {} для пользователя: {}, размер: {} байт", 
                   filename, owner, file.getSize());
        
        storageService.uploadFile(owner, filename, file);
        logger.info("Файл {} успешно загружен", filename);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/file")
    public ResponseEntity<Void> renameFile(@RequestHeader("auth-token") String authToken,
                                           @RequestParam("filename") String filename,
                                           @RequestBody Map<String, String> fileNameRequest) {
        String owner = jwtTokenUtils.getUsernameFromToken(authToken);
        String newName = fileNameRequest.get("filename");
        logger.info("Переименование файла: {} в {} для пользователя: {}", filename, newName, owner);
        
        storageService.renameFile(owner, filename, newName);
        logger.info("Файл {} успешно переименован в {}", filename, newName);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/file")
    public ResponseEntity<Void> deleteFile(@RequestHeader("auth-token") String authToken,
                                           @RequestParam("filename") String filename) {
        String owner = jwtTokenUtils.getUsernameFromToken(authToken);
        logger.info("Удаление файла: {} для пользователя: {}", filename, owner);
        
        storageService.deleteFile(owner, filename);
        logger.info("Файл {} успешно удален", filename);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/file")
    public ResponseEntity<byte[]> downloadFile(@RequestHeader("auth-token") String authToken,
                                               @RequestParam("filename") String filename) {
        String owner = jwtTokenUtils.getUsernameFromToken(authToken);
        logger.info("Скачивание файла: {} для пользователя: {}", filename, owner);
        
        File file = storageService.downloadFile(owner, filename);
        logger.info("Файл {} успешно найден, размер: {} байт", filename, file.getSize());
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file.getContent());
    }
}