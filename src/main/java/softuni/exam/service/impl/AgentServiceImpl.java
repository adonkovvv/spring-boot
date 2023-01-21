package softuni.exam.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportAgentDTO;
import softuni.exam.models.entity.Agent;
import softuni.exam.models.entity.Town;
import softuni.exam.repository.AgentRepository;
import softuni.exam.repository.TownRepository;
import softuni.exam.service.AgentService;

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
public class AgentServiceImpl implements AgentService {
    private final AgentRepository agentRepository;
    private final TownRepository townRepository;
    private final Gson gson;
    private final Validator validator;
    private final ModelMapper mapper;

    @Autowired
    public AgentServiceImpl(AgentRepository agentRepository, TownRepository townRepository) {
        this.agentRepository = agentRepository;
        this.townRepository = townRepository;
        this.gson = new GsonBuilder().create();
        this.validator = Validation
                .buildDefaultValidatorFactory()
                .getValidator();
        this.mapper = new ModelMapper();
    }

    @Override
    public boolean areImported() {
        return this.agentRepository.count() > 0;
    }

    @Override
    public String readAgentsFromFile() throws IOException {
        return String.join("\n", Files.readAllLines(Path.of("src/main/resources/files/json/agents.json")));
    }

    @Override
    public String importAgents() throws IOException {
        ImportAgentDTO[] importAgentDTOS = this.gson.fromJson(this.readAgentsFromFile(), ImportAgentDTO[].class);

        List<String> result = new ArrayList<>();

        for (ImportAgentDTO importAgentDTO : importAgentDTOS) {
            Set<ConstraintViolation<ImportAgentDTO>> validate =
                    this.validator.validate(importAgentDTO);

            if (validate.isEmpty()) {
                Optional<Agent> byEmail = this.agentRepository.findByEmail(importAgentDTO.getEmail());
                Optional<Agent> byFirstName = this.agentRepository.findByFirstName(importAgentDTO.getFirstName());

                if (byEmail.isEmpty() && byFirstName.isEmpty()) {
                    Agent agent = this.mapper.map(importAgentDTO, Agent.class);

                    Optional<Town> byTownName = this.townRepository.findByTownName(importAgentDTO.getTown());

                    agent.setTown(byTownName.get());

                    this.agentRepository.save(agent);

                    result.add("Successfully imported agent - " + agent.getFirstName() + " " + agent.getLastName());
                }
                else {
                    result.add("Invalid agent");
                }
            }
            else {
                result.add("Invalid agent");
            }
        }

        return String.join("\n", result);
    }
}
