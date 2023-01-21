package softuni.exam.models.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "apartments")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class ImportApartmentRootDTO {
    @XmlElement(name = "apartment")
    private List<ImportApartmentDTO> importApartmentDTOS;

    public List<ImportApartmentDTO> getImportApartmentDTOS() {
        return importApartmentDTOS;
    }
}
