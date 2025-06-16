package ru.netology.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.dto.FileResponse;
import ru.netology.entities.File;
import ru.netology.repositories.FileRepository;
import ru.netology.security.JwtTokenUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StorageServiceTest {

    @InjectMocks
    private StorageService storageService;

    @Mock
    private FileRepository fileRepository;

    @Mock
    private JwtTokenUtils jwtTokenUtils;

    @Captor
    private ArgumentCaptor<File> fileCaptor;

    private final String TOKEN = "Bearer token123";
    private final String OWNER = "user";
    private final String FILENAME = "test.txt";

    @Test
    void getFilesReturnsList() {
        File file = new File(FILENAME, "text/plain", 100L, new byte[0], OWNER);
        when(jwtTokenUtils.getUsernameFromToken("token123")).thenReturn(OWNER);
        when(fileRepository.findAllByOwner(OWNER)).thenReturn(Optional.of(List.of(file)));

        List<FileResponse> files = storageService.getFiles(TOKEN, 1);

        assertEquals(1, files.size());
        assertEquals(FILENAME, files.get(0).getFilename());
    }

    @Test
    void getFilesThrowsIfNoFiles() {
        when(jwtTokenUtils.getUsernameFromToken("token123")).thenReturn(OWNER);
        when(fileRepository.findAllByOwner(OWNER)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> storageService.getFiles(TOKEN, 1));
    }

    @Test
    void uploadFileSavesFile() throws IOException {
        MultipartFile multipartFile = new MockMultipartFile(FILENAME, "text/plain", null, "test content".getBytes());
        when(jwtTokenUtils.getUsernameFromToken("token123")).thenReturn(OWNER);

        storageService.uploadFile(TOKEN, FILENAME, multipartFile);

        verify(fileRepository).save(fileCaptor.capture());
        File saved = fileCaptor.getValue();
        assertEquals(FILENAME, saved.getFilename());
        assertEquals(OWNER, saved.getOwner());
        assertEquals(multipartFile.getSize(), saved.getSize());
        assertArrayEquals(multipartFile.getBytes(), saved.getContent());
    }

    @Test
    void deleteFileRemovesCorrectly() {
        when(jwtTokenUtils.getUsernameFromToken("token123")).thenReturn(OWNER);

        storageService.deleteFile(TOKEN, FILENAME);

        verify(fileRepository).removeByFilenameAndOwner(FILENAME, OWNER);
    }

    @Test
    void downloadFileReturnsCorrectly() {
        File file = new File(FILENAME, "text/plain", 100L, new byte[0], OWNER);
        when(jwtTokenUtils.getUsernameFromToken("token123")).thenReturn(OWNER);
        when(fileRepository.findByFilenameAndOwner(FILENAME, OWNER)).thenReturn(file);

        File result = storageService.downloadFile(TOKEN, FILENAME);

        assertEquals(file, result);
    }

    @Test
    void renameFileCallsRepository() {
        when(jwtTokenUtils.getUsernameFromToken("token123")).thenReturn(OWNER);

        storageService.renameFile(TOKEN, FILENAME, "new.txt");

        verify(fileRepository).renameFile(FILENAME, "new.txt", OWNER);
    }
}