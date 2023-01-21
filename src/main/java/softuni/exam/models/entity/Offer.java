package softuni.exam.models.entity;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "offers")
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private Double price;

    @Column(name = "published_on", nullable = false)
    private LocalDate publishedOn;

    @ManyToOne
    @JoinColumn(name = "apartment_id")
    private Apartment apartment;

    @ManyToOne
    @JoinColumn(name = "agent_id")
    private Agent agent;

    public Agent getAgent() {
        return agent;
    }

    public Offer() {}

    public Offer(Double price, LocalDate publishedOn, Apartment apartment, Agent agent) {
        this.price = price;
        this.publishedOn = publishedOn;
        this.apartment = apartment;
        this.agent = agent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public LocalDate getPublishedOn() {
        return publishedOn;
    }

    public void setPublishedOn(LocalDate publishedOn) {
        this.publishedOn = publishedOn;
    }

    public Apartment getApartment() {
        return apartment;
    }

    public void setApartment(Apartment apartment) {
        this.apartment = apartment;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    @Override
    public String toString() {
        //•	"Agent {firstName} {lastName} with offer №{offerId}:
        //   		-Apartment area: {area}
        //   		--Town: {townName}
        //   		---Price: {price}$
        //. . . "
        StringBuilder sb = new StringBuilder();

        sb.append("Agent " + this.agent.getFirstName() + " " + this.agent.getLastName()
        + " with offer №" + this.id + ":\n");
        sb.append(String.format("\t\t-Apartment area: %.2f\n", this.apartment.getArea()));
        sb.append("\t\t--Town: " + this.apartment.getTown().getTownName() + "\n");
        sb.append(String.format("\t\t---Price: %.2f$", this.price));
        return sb.toString();
    }
}
