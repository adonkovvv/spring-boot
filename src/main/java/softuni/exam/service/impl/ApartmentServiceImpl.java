package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportApartmentDTO;
import softuni.exam.models.dto.ImportApartmentRootDTO;
import softuni.exam.models.entity.Apartment;
import softuni.exam.models.entity.ApartmentType;
import softuni.exam.models.entity.Town;
import softuni.exam.repository.ApartmentRepository;
import softuni.exam.repository.TownRepository;
import softuni.exam.service.ApartmentService;

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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ApartmentServiceImpl implements ApartmentService {
    private final ApartmentRepository apartmentRepository;
    private final TownRepository townRepository;
    private final Unmarshaller unmarshaller;
    private final Validator validator;
    private final ModelMapper mapper;

    @Autowired
    public ApartmentServiceImpl(ApartmentRepository apartmentRepository, TownRepository townRepository) throws JAXBException {
        this.apartmentRepository = apartmentRepository;
        this.townRepository = townRepository;
        JAXBContext context = JAXBContext.newInstance(ImportApartmentRootDTO.class);
        this.unmarshaller = context.createUnmarshaller();
        this.validator = Validation
                .buildDefaultValidatorFactory()
                .getValidator();
        this.mapper = new ModelMapper();
    }

    @Override
    public boolean areImported() {
        return this.apartmentRepository.count() > 0;
    }

    @Override
    public String readApartmentsFromFile() throws IOException {
        return String.join("\n", Files.readAllLines(Path.of("src/main/resources/files/xml/apartments.xml")));
    }

    @Override
    public String importApartments() throws IOException, JAXBException {
        ImportApartmentRootDTO apartmentRootDTOS = (ImportApartmentRootDTO) this.unmarshaller
                .unmarshal(new FileReader(Path.of("src/main/resources/files/xml/apartments.xml").toString()));

        return apartmentRootDTOS
                .getImportApartmentDTOS()
                .stream()
                .map(this::importApartment)
                .collect(Collectors.joining("\n"));
    }

    private String importApartment(ImportApartmentDTO dto) {
        Set<ConstraintViolation<ImportApartmentDTO>> validate =
                this.validator.validate(dto);

        if (validate.isEmpty()) {
            Optional<Town> byTownName = this.townRepository.findByTownName(dto.getTown());

            Optional<Apartment> byTownAndArea = this.apartmentRepository.findByTownAndArea(byTownName.get(), dto.getArea());

            if(byTownAndArea.isPresent()) {
                return "Invalid apartment";
            }

            Apartment apartment = this.mapper.map(dto, Apartment.class);

            if (dto.getApartmentType().equals("two_rooms")) {
                apartment.setApartmentType(ApartmentType.twoRooms);
            } else if (dto.getApartmentType().equals("three_rooms")) {
                apartment.setApartmentType(ApartmentType.threeRooms);
            } else {
                apartment.setApartmentType(ApartmentType.fourRooms);
            }

            apartment.setTown(byTownName.get());

            this.apartmentRepository.save(apartment);

            return "Successfully imported apartment " + apartment.getApartmentType().getLabel() + " - " + apartment.getArea();
        }
        else {
            return "Invalid apartment";
        }
    }
}
