package ru.netology.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.entities.File;
import ru.netology.repositories.FileRepository;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class StorageService {

    private static final Logger logger = LoggerFactory.getLogger(StorageService.class);

    private final FileRepository fileRepository;

    public StorageService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public List<File> getFiles(String owner, int limit) {
        logger.debug("Получение файлов для пользователя: {}, лимит: {}", owner, limit);
        List<File> files = fileRepository.findAllByOwner(owner);
        List<File> limitedFiles = files.stream()
                .limit(limit)
                .collect(java.util.stream.Collectors.toList());
        logger.debug("Найдено файлов: {}, возвращено: {}", files.size(), limitedFiles.size());
        return limitedFiles;
    }

    public void uploadFile(String owner, String filename, MultipartFile file) throws IOException {
        logger.debug("Сохранение файла: {} для пользователя: {}, размер: {} байт", 
                    filename, owner, file.getSize());
        fileRepository.save(new File(filename, file.getContentType(), file.getSize(), file.getBytes(), owner));
        logger.debug("Файл {} успешно сохранен в базе данных", filename);
    }

    public void deleteFile(String owner, String filename) {
        logger.debug("Удаление файла: {} для пользователя: {}", filename, owner);
        fileRepository.deleteByFilenameAndOwner(filename, owner);
        logger.debug("Файл {} успешно удален из базы данных", filename);
    }

    public File downloadFile(String owner, String filename) {
        logger.debug("Поиск файла: {} для пользователя: {}", filename, owner);
        File file = fileRepository.findByFilenameAndOwner(filename, owner);
        if (file != null) {
            logger.debug("Файл {} найден, размер: {} байт", filename, file.getSize());
        } else {
            logger.warn("Файл {} не найден для пользователя: {}", filename, owner);
        }
        return file;
    }

    public void renameFile(String owner, String filename, String newFilename) {
        logger.debug("Переименование файла: {} в {} для пользователя: {}", filename, newFilename, owner);
        fileRepository.renameFile(filename, newFilename, owner);
        logger.debug("Файл {} успешно переименован в {}", filename, newFilename);
    }
}