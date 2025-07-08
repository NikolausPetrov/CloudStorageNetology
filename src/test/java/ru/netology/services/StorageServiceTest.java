package ru.netology.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.entities.File;
import ru.netology.repositories.FileRepository;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StorageServiceTest {

    private StorageService storageService;
    @Mock
    private FileRepository fileRepository;
    @Captor
    private ArgumentCaptor<File> fileCaptor;

    private final String OWNER = "user";
    private final String FILENAME = "test.txt";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        storageService = new StorageService(fileRepository);
    }

    @Test
    void getFilesReturnsList() {
        File file = new File(FILENAME, "text/plain", 100L, new byte[0], OWNER);
        when(fileRepository.findAllByOwner(OWNER)).thenReturn(Arrays.asList(file));

        List<File> files = storageService.getFiles(OWNER, 1);

        assertEquals(1, files.size());
        assertEquals(FILENAME, files.get(0).getFilename());
    }

    @Test
    void uploadFileSavesFile() throws IOException {
        MultipartFile multipartFile = new MockMultipartFile(FILENAME, "text/plain", null, "test content".getBytes());

        storageService.uploadFile(OWNER, FILENAME, multipartFile);

        verify(fileRepository).save(fileCaptor.capture());
        File saved = fileCaptor.getValue();
        assertEquals(FILENAME, saved.getFilename());
        assertEquals(OWNER, saved.getOwner());
        assertEquals(multipartFile.getSize(), saved.getSize());
        assertArrayEquals(multipartFile.getBytes(), saved.getContent());
    }

    @Test
    void deleteFileRemovesCorrectly() {
        storageService.deleteFile(OWNER, FILENAME);
        verify(fileRepository).deleteByFilenameAndOwner(FILENAME, OWNER);
    }

    @Test
    void downloadFileReturnsCorrectly() {
        File file = new File(FILENAME, "text/plain", 100L, new byte[0], OWNER);
        when(fileRepository.findByFilenameAndOwner(FILENAME, OWNER)).thenReturn(file);

        File result = storageService.downloadFile(OWNER, FILENAME);

        assertEquals(file, result);
    }

    @Test
    void renameFileCallsRepository() {
        storageService.renameFile(OWNER, FILENAME, "new.txt");
        verify(fileRepository).renameFile(FILENAME, "new.txt", OWNER);
    }
}