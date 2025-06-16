package ru.netology.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.netology.entities.File;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    List<File> findAllByOwner(String owner);

    File findByFilenameAndOwner(String filename, String owner);

    void deleteByFilenameAndOwner(String filename, String owner);

    @Modifying
    @Transactional
    @Query("update File f set f.filename = :newName where f.filename = :filename and f.owner = :owner")
    void renameFile(@Param("filename") String filename,
                    @Param("newName") String newFilename,
                    @Param("owner") String owner);
}