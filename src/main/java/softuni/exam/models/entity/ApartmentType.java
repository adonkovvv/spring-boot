package softuni.exam.models.entity;

public enum ApartmentType {
    twoRooms("two_rooms"), threeRooms("three_rooms"), fourRooms("four_rooms");

    private final String label;

    private ApartmentType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
