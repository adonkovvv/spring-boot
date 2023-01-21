package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportOfferDTO;
import softuni.exam.models.dto.ImportOfferRootDTO;
import softuni.exam.models.entity.Agent;
import softuni.exam.models.entity.Apartment;
import softuni.exam.models.entity.ApartmentType;
import softuni.exam.models.entity.Offer;
import softuni.exam.repository.AgentRepository;
import softuni.exam.repository.ApartmentRepository;
import softuni.exam.repository.OfferRepository;
import softuni.exam.service.OfferService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OfferServiceImpl implements OfferService {
    private final OfferRepository offerRepository;
    private final AgentRepository agentRepository;
    private final ApartmentRepository apartmentRepository;
    private final Unmarshaller unmarshaller;
    private final Validator validator;
    private final ModelMapper mapper;

    @Autowired
    public OfferServiceImpl(OfferRepository offerRepository, AgentRepository agentRepository, ApartmentRepository apartmentRepository) throws JAXBException {
        this.offerRepository = offerRepository;
        this.agentRepository = agentRepository;
        this.apartmentRepository = apartmentRepository;
        JAXBContext context = JAXBContext.newInstance(ImportOfferRootDTO.class);
        this.unmarshaller = context.createUnmarshaller();
        this.validator = Validation
                .buildDefaultValidatorFactory()
                .getValidator();
        this.mapper = new ModelMapper();
        this.mapper.addConverter(c -> LocalDate.parse(c.getSource(), DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                String.class, LocalDate.class);
    }

    @Override
    public boolean areImported() {
        return this.offerRepository.count() > 0;
    }

    @Override
    public String readOffersFileContent() throws IOException {
        return String.join("\n", Files.readAllLines(Path.of("src/main/resources/files/xml/offers.xml")));
    }

    @Override
    public String importOffers() throws IOException, JAXBException {
        ImportOfferRootDTO offerRootDTOS = (ImportOfferRootDTO) this.unmarshaller
                .unmarshal(new FileReader(Path.of("src/main/resources/files/xml/offers.xml").toString()));

        return offerRootDTOS
                .getImportOfferDTOS()
                .stream()
                .map(this::importOffer)
                .collect(Collectors.joining("\n"));
    }

    private String importOffer(ImportOfferDTO dto) {
        Set<ConstraintViolation<ImportOfferDTO>> validate =
                this.validator.validate(dto);

        if (validate.isEmpty()) {
            Optional<Agent> byFirstName = this.agentRepository.findByFirstName(dto.getAgent().getName());

            if (byFirstName.isPresent()) {
                Offer offer = this.mapper.map(dto, Offer.class);

                Optional<Apartment> byId = this.apartmentRepository.findById(dto.getApartment().getId());

                offer.setApartment(byId.get());
                offer.setAgent(byFirstName.get());

                this.offerRepository.save(offer);

                return String.format("Successfully imported offer %.2f", offer.getPrice());
            }
            else {
                return "Invalid offer";
            }
        }
        else {
            return "Invalid offer";
        }
    }

    @Override
    public String exportOffers() {
        List<Offer> threeRoomsApartments = this.offerRepository
                .findAllByApartmentApartmentTypeOrderByApartmentAreaDescPriceAsc(ApartmentType.threeRooms);

        List<String> result = threeRoomsApartments.stream()
                .map(a -> a.toString())
                .collect(Collectors.toList());

        return String.join("\n", result);
    }
}
