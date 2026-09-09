package com.wampert.wampert.service;



import com.wampert.wampert.dto.response.AdminCarResponse;
import com.wampert.wampert.dto.response.ToggleAvailabilityResponse;
import com.wampert.wampert.model.CarEntity;
import com.wampert.wampert.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ToggleAvailabilityService {

    private final CarRepository carRepository;


    public ToggleAvailabilityResponse toggleAvailability(String carId) {
        CarEntity car = carRepository.findById(carId).orElseThrow(()-> new RuntimeException("Car not found"));
        car.setIsAvailable(!car.getIsAvailable());
     CarEntity updatedCar =   carRepository.save(car);
     return mapToToggleAvailabilityResponse(updatedCar);


    }

    ToggleAvailabilityResponse mapToToggleAvailabilityResponse(CarEntity car ) {
        return ToggleAvailabilityResponse.builder()
                .id(car.getId())
                .isAvailable(car.getIsAvailable())
                .build();

    }

}
