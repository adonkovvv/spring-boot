package softuni.exam.models.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

public class ImportTownDTO {
    @Size(min = 2)
    @NotNull
    private String townName;

    @Positive
    @NotNull
    private int population;

    public ImportTownDTO() {}

    public String getTownName() {
        return townName;
    }

    public int getPopulation() {
        return population;
    }
}
