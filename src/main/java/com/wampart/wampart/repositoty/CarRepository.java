package com.wampart.wampart.repositoty;


import com.wampart.wampart.model.CarEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends MongoRepository<CarEntity, String> {
    Optional<CarEntity> findByNumberPlate(String NumberPlate);
    Boolean existsByNumberPlate(String NumberPlate);
    List<CarEntity> findByIsAvailable(Boolean isAvailable);
    List<CarEntity> findByIsInsuranceActive(Boolean isInsuranceActive);
    List<CarEntity> findByBrand(String brand);
    List<CarEntity> numberOfPassengers(Integer numberOfPassengers);
    List<CarEntity> findByPricePerDay(Double pricePerDay);

    List<CarEntity> isAvailable(Boolean isAvailable);
}
