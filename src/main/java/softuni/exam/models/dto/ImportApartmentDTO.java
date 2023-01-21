package softuni.exam.models.dto;

import softuni.exam.models.entity.ApartmentType;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(value = XmlAccessType.FIELD)
public class ImportApartmentDTO {
    @XmlElement
    @NotNull
    private String apartmentType;

    @XmlElement
    @NotNull
    @Min(40)
    private Double area;

    @XmlElement
    @NotNull
    private String town;

    public ImportApartmentDTO() {}

    public String getApartmentType() {
        return apartmentType;
    }

    public Double getArea() {
        return area;
    }

    public String getTown() {
        return town;
    }
}
