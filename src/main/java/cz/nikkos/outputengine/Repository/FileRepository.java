package cz.nikkos.outputengine.Repository;


import cz.nikkos.outputengine.Model.TemplateFile;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FileRepository extends JpaRepository<TemplateFile, Long> {
    TemplateFile findByName(String name);
}
