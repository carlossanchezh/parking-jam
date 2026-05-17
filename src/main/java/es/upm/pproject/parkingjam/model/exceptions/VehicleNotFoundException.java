package es.upm.pproject.parkingjam.model.exceptions;

public class VehicleNotFoundException extends RuntimeException {
    public VehicleNotFoundException(char vehicleId) {
        super("Vehicle with id " + vehicleId + " not found");
    }
}
