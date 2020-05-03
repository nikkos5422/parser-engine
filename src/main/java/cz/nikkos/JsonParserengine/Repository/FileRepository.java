package cz.nikkos.JsonParserengine.Repository;


import cz.nikkos.JsonParserengine.Model.TemplateFile;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FileRepository extends JpaRepository<TemplateFile, Long> {
    TemplateFile findByName(String name);
}
