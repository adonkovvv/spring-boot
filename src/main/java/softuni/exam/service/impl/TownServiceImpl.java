package softuni.exam.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportTownDTO;
import softuni.exam.models.entity.Town;
import softuni.exam.repository.TownRepository;
import softuni.exam.service.TownService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TownServiceImpl implements TownService {
    private final TownRepository townRepository;
    private final Gson gson;
    private final Validator validator;
    private final ModelMapper mapper;

    @Autowired
    public TownServiceImpl(TownRepository townRepository) {
        this.townRepository = townRepository;
        this.gson = new GsonBuilder().create();
        this.validator = Validation
                .buildDefaultValidatorFactory()
                .getValidator();
        this.mapper = new ModelMapper();
    }

    @Override
    public boolean areImported() {
        return this.townRepository.count() > 0;
    }

    @Override
    public String readTownsFileContent() throws IOException {
        return String.join("\n", Files.readAllLines(Path.of("src/main/resources/files/json/towns.json")));
    }

    @Override
    public String importTowns() throws IOException {
        ImportTownDTO[] importTownDTOS = this.gson.fromJson(this.readTownsFileContent(), ImportTownDTO[].class);

        List<String> result = new ArrayList<>();

        for (ImportTownDTO importTownDTO : importTownDTOS) {
            Set<ConstraintViolation<ImportTownDTO>> validate =
                    this.validator.validate(importTownDTO);

            if (validate.isEmpty()) {
                Optional<Town> byTownName = this.townRepository.findByTownName(importTownDTO.getTownName());

                if (byTownName.isPresent()) {
                    result.add("Invalid town");
                }
                else {
                    Town town = this.mapper.map(importTownDTO, Town.class);

                    this.townRepository.save(town);

                    result.add("Successfully imported town " + town.getTownName() + " - " + town.getPopulation());
                }
            }
            else {
                result.add("Invalid town");
            }
        }
        return String.join("\n", result);
    }
}
