package com.wampart.wampart.service;



import com.wampart.wampart.dto.response.AdminCarResponse;
import com.wampart.wampart.dto.response.ToggleAvailabilityResponse;
import com.wampart.wampart.model.CarEntity;
import com.wampart.wampart.repositoty.CarRepository;
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
