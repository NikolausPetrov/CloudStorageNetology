package ru.netology.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "files", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"filename", "owner"})
})
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String filename;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private Long size;

    @Lob
    @Column(nullable = false)
    private byte[] content;

    @Column(nullable = false)
    private String owner;

    public File(String filename, String type, Long size, byte[] content, String owner) {
        this.filename = filename;
        this.type = type;
        this.size = size;
        this.content = content;
        this.owner = owner;
    }
}