package cz.nikkos.JsonParserengine.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;

@Entity
@Table(name = "temp_file")
@NoArgsConstructor
public class TemplateFile implements Serializable {

    private long id;
    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Basic
    @Column(name = "file_bytes")
    private byte[] fileBytes;


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public TemplateFile(String name, String description, byte[] file) {
        this.name = name;
        this.description = description;
        this.fileBytes = file;
    }

    @Override
    public String toString() {
        int fileSize = fileBytes == null ? 0 : fileBytes.length;
        return "TemplateFile{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", fileBytes=" + fileSize +
                '}';
    }

    @JsonIgnore
    public InputStream returnFileAsStream() {
        // convert byte [] to input stream
        return new ByteArrayInputStream(fileBytes);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFileBytes(byte[] fileBytes) {
        this.fileBytes = fileBytes;
    }

    @JsonIgnore
    public byte[] getFileBytes() {
        return fileBytes;
    }
}

