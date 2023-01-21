package softuni.exam.models.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(value = XmlAccessType.FIELD)
public class ImportOfferDTO {
    @XmlElement
    @Positive
    @NotNull
    private Double price;

    @XmlElement
    @NotNull
    private ImportAgentNameDto agent;

    @XmlElement
    @NotNull
    private ImportApartmentIdDto apartment;

    @XmlElement
    @NotNull
    private String publishedOn;

    public ImportOfferDTO() {}

    public Double getPrice() {
        return price;
    }

    public ImportAgentNameDto getAgent() {
        return agent;
    }

    public ImportApartmentIdDto getApartment() {
        return apartment;
    }

    public String getPublishedOn() {
        return publishedOn;
    }
}
