package com.wampart.wampart.service;


import com.wampart.wampart.dto.request.CarRequest;
import com.wampart.wampart.dto.response.AdminCarResponse;
import com.wampart.wampart.dto.response.CustomerCarResponse;
import com.wampart.wampart.enums.BookingStatus;
import com.wampart.wampart.model.CarEntity;
import com.wampart.wampart.repositoty.BookingRepository;
import com.wampart.wampart.repositoty.CarRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarService {
    private final CarRepository carRepository;
    private final FileUploadService fileUploadService;
    private final BookingRepository bookingRepository;



    public AdminCarResponse addCar(CarRequest request) {
        if(carRepository.existsByNumberPlate(request.getNumberPlate())) {
            throw new RuntimeException("Car with number plate " + request.getNumberPlate() + " already exist" );

        }


        CarEntity car = CarEntity.builder()
                .brand(request.getBrand())
                .model(request.getModel())
                .yearOfManufacture(request.getYearOfManufacture())
                .color(request.getColor())
                .typeOfFuel(request.getTypeOfFuel())
                .transmission(request.getTransmission())
                .numberPlate(request.getNumberPlate())
                .description(request.getDescription())
                .pricePerDay(request.getPricePerDay())
                .currentMileage(request.getCurrentMileage())
                .serviceMileageInterval(request.getServiceMileageInterval())
                .insuranceExpiryDate(request.getInsuranceExpiryDate())
                .isAvailable(true)
                .build();



        CarEntity savedCar = carRepository.save(car);
        return mapToAdminCarResponse(savedCar);

    }


    public List<AdminCarResponse> getAllCars() {
        return carRepository.findAll()
                .stream()
                .map(this::mapToAdminCarResponse)
                .collect(Collectors.toList());
    }


    public List<CustomerCarResponse> getAllCarsByUserId() {
        return carRepository.findByIsAvailable(true)
                .stream()
                .map(this::mapToCustomerResponse)
                .collect(Collectors.toList());
    }

    public AdminCarResponse getCarById(String id) {
        CarEntity car = carRepository.findById(id).orElseThrow(()-> new RuntimeException("Car not found"));
        return mapToAdminCarResponse(car);
    }

    CustomerCarResponse getCarByUserId(String id) {
        CarEntity car = carRepository.findById(id).orElseThrow(()-> new RuntimeException("Car not found"));
        return mapToCustomerResponse(car);
    }

    public AdminCarResponse updateCar(String id, CarRequest request) {


        CarEntity car = carRepository.findById(id).orElseThrow(()-> new RuntimeException("Car not found"));


        car.setBrand(request.getBrand());
        car.setModel(request.getModel());
        car.setYearOfManufacture(request.getYearOfManufacture());
        car.setColor(request.getColor());
        car.setTypeOfFuel(request.getTypeOfFuel());
        car.setTransmission(request.getTransmission());
        car.setNumberPlate(request.getNumberPlate());
        car.setDescription(request.getDescription());
        car.setPricePerDay(request.getPricePerDay());
        car.setCurrentMileage(request.getCurrentMileage());
        car.setServiceMileageInterval(request.getServiceMileageInterval());
        car.setInsuranceExpiryDate(request.getInsuranceExpiryDate());
        car.setImages(request.getImages());
        car.setNumberOfPassengers(request.getNumberOfPassengers());
        if (request.getInsuranceExpiryDate() != null &&  car.getInsuranceExpiryDate().isBefore(LocalDateTime.now())) {
            car.setIsAvailable(false);
        } else {
            car.setIsAvailable(request.getIsAvailable());
        }
        car.setUpdatedAt(LocalDateTime.now());



        CarEntity updatedCar = carRepository.save(car);
        return mapToAdminCarResponse(updatedCar);
    }

    public AdminCarResponse toggleCarAvailability(String id) {
        CarEntity car = carRepository.findById(id).orElseThrow(()-> new RuntimeException("Car not found"));
            car.setIsAvailable(!car.getIsAvailable());
            CarEntity updatedCar = carRepository.save(car);
            return mapToAdminCarResponse(updatedCar);

    }

    public AdminCarResponse toggleCarMaintenance(String id) {
        CarEntity car = carRepository.findById(id).orElseThrow(()-> new RuntimeException("Car not found"));
        return mapToAdminCarResponse(car);
    }




    public AdminCarResponse mapToAdminCarResponse(CarEntity car) {
        boolean isInUse = bookingRepository.findByCarId(car.getId())
                .stream()
                .anyMatch(booking -> booking.getBookingStatus() == BookingStatus.CONFIRMED &&
                        !booking.getStartDate().isAfter(LocalDateTime.now()) &&
                        !booking.getEndDate().isBefore(LocalDateTime.now()));


        return AdminCarResponse.builder()
                .id(car.getId())
                .brand(car.getBrand())
                .model(car.getModel())
                .yearOfManufacture(car.getYearOfManufacture())
                .color(car.getColor())
                .typeOfFuel(car.getTypeOfFuel().name())
                .transmission(car.getTransmission().name())
                .numberOfPassengers(car.getNumberOfPassengers())
                .numberPlate(car.getNumberPlate())
                .description(car.getDescription())
                .images(car.getImages())
                .pricePerDay(car.getPricePerDay())
                .isAvailable(car.getIsAvailable())
                .isInUse(isInUse)
                .currentMileage(car.getCurrentMileage())
                .serviceMileageInterval(car.getServiceMileageInterval())
                .insuranceExpiryDate(car.getInsuranceExpiryDate())
                .createdAt(car.getCreatedAt())
                .updatedAt(car.getUpdatedAt())
                .build();
    }

    public CustomerCarResponse mapToCustomerResponse(CarEntity car) {
        boolean isInUse = bookingRepository.findByCarId(car.getId())
                .stream()
                .anyMatch(booking -> booking.getBookingStatus() == BookingStatus.CONFIRMED &&
                        !booking.getStartDate().isAfter(LocalDateTime.now()) &&
                        !booking.getEndDate().isBefore(LocalDateTime.now()));

        return CustomerCarResponse.builder()
                .id(car.getId())
                .brand(car.getBrand())
                .model(car.getModel())
                .yearOfManufacture(car.getYearOfManufacture())
                .color(car.getColor())
                .typeOfFuel(car.getTypeOfFuel().name())
                .transmission(car.getTransmission().name())
                .numberOfPassengers(car.getNumberOfPassengers())
                .numberPlate(car.getNumberPlate())
                .description(car.getDescription())
                .images(car.getImages())
                .pricePerDay(car.getPricePerDay())
                .isAvailable(car.getIsAvailable())
                .isInUse(isInUse)
                .createdAt(car.getCreatedAt())
                .updatedAt(car.getUpdatedAt())
                .build();
    }

    public List<CustomerCarResponse> getAllAvailableCars() {
        return carRepository.findByIsAvailable(true)
                .stream()
                .map(this::mapToCustomerResponse)
                .collect(Collectors.toList());
    }

    public CustomerCarResponse getCarByIdForCustomer(String id) {
        CarEntity car = carRepository.findById(id).orElseThrow(()-> new RuntimeException("Car not found"));
        return mapToCustomerResponse(car);

    }

    public String deleteCar(String id) {
        carRepository.deleteById(id);
        return "Car deleted successfully";
    }

    public AdminCarResponse addCarImages(String id, List<String>  imageUrls ) {
       CarEntity car =  carRepository.findById(id).orElseThrow(()-> new RuntimeException("Car not found"));

     if (car.getImages() == null) {
         car.setImages(new ArrayList<>());
     }

     car.getImages().addAll(imageUrls);

     CarEntity updatedCar = carRepository.save(car);
     return mapToAdminCarResponse(updatedCar);

    }

//    public AdminCarResponse updateCarImages(String id, List<String> imageUrls) {
//        CarEntity car = carRepository.findById(id).orElseThrow(()-> new RuntimeException("Car not found"));
//
//        if(car.getImages() == null && !car.getImages().isEmpty() ) {
//            for(String oldImageUrl : car.getImages()) {
//                try {
//                    fileUploadService.deleteFile(oldImageUrl);
//                } catch (Exception e) {
//
//                }
//            }
//
//        }
//        car.setImages(imageUrls);
//        CarEntity updatedCar = carRepository.save(car);
//        return mapToAdminCarResponse(updatedCar);
//    }

    public AdminCarResponse removeCarImage(String id, String imageUrl) {

        CarEntity car = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found"));

        List<String> images = new ArrayList<>(
                Optional.ofNullable(car.getImages())
                        .orElse(new ArrayList<>())
        );

        if (images.isEmpty()) {
            throw new RuntimeException("Car does not have any images");
        }

        if (!images.contains(imageUrl)) {
            throw new RuntimeException("Image not found on this car");
        }

        images.remove(imageUrl);

        car.setImages(images);

        CarEntity updatedCar = carRepository.save(car);

        return mapToAdminCarResponse(updatedCar);
    }


}
