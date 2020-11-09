package com.marcu.vrp.backend.service;

import com.marcu.vrp.backend.dto.DriverDTO;
import com.marcu.vrp.backend.mapper.DriverDTOMapper;
import com.marcu.vrp.backend.model.Driver;
import com.marcu.vrp.backend.repository.DriverRepository;
import com.marcu.vrp.backend.validator.ValidatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


@Service
public class DriverServiceImpl implements DriverService {
    private static final Logger log = LoggerFactory.getLogger(DriverServiceImpl.class);

    private DriverRepository driverRepository;
    private DriverDTOMapper driverDTOMapper;

    public DriverServiceImpl(final DriverRepository driverRepository, DriverDTOMapper driverDTOMapper) {
        this.driverRepository = driverRepository;
        this.driverDTOMapper = driverDTOMapper;
    }

    @Override
    public List<DriverDTO> findAll() {
        log.debug("Retrieving all drivers");
        List<DriverDTO> allDrivers = driverDTOMapper.toDtos(driverRepository.findAll());
        log.debug("All drivers retrieved");
        return allDrivers;
    }

    @Override
    public DriverDTO add(DriverDTO driverDTO) {
        log.debug("Trying to add driver {}.", driverDTO.toString());
        Driver possibleExistingDriver = driverRepository.findFirstByPhoneOrEmail( driverDTO.getPhone(), driverDTO.getEmail());
        if (possibleExistingDriver != null) {
            if (Objects.equals(possibleExistingDriver.getEmail(), driverDTO.getEmail()))
                throw new ValidatorException("There already exists a driver with this email.");
            if (Objects.equals(possibleExistingDriver.getPhone(), driverDTO.getPhone()))
                throw new ValidatorException("There already exists a driver with this Phone no.");
        }

        Driver newDriver = driverDTOMapper.toEntity(driverDTO);
        return driverDTOMapper.toDto(driverRepository.save(newDriver));
    }

    @Override
    public DriverDTO findById(Long id) {
        return driverDTOMapper.toDto(this.driverRepository.findById(id).orElseThrow(() -> new IllegalStateException("Driver with id " + id + " should exist!")));
    }

    @Override
    public DriverDTO edit(DriverDTO driverDTO, Long id) {
        Driver oldDriver = this.driverRepository.findById(id).orElseThrow(() -> new IllegalStateException("Driver with id " + id + " should exist!"));

        List<Driver> possibleExistingDrivers = driverRepository.findByPhoneOrEmail( driverDTO.getPhone(), driverDTO.getEmail());
        if (possibleExistingDrivers != null && possibleExistingDrivers.size() > 0) {
            possibleExistingDrivers.forEach(possibleExistingDriver -> {
                if (Objects.equals(possibleExistingDriver.getEmail(), driverDTO.getEmail()) && !oldDriver.getId().equals(possibleExistingDriver.getId()))
                    throw new ValidatorException("There already exists a driver with this email.");
                if (Objects.equals(possibleExistingDriver.getPhone(), driverDTO.getPhone()) && !oldDriver.getId().equals(possibleExistingDriver.getId()))
                    throw new ValidatorException("There already exists a driver with this Phone no.");
            });
        }

        oldDriver.setCar(driverDTO.getCar());
        oldDriver.setEmail(driverDTO.getEmail());
        oldDriver.setFirstName(driverDTO.getFirstName());
        oldDriver.setLastName(driverDTO.getLastName());
        oldDriver.setPhone(driverDTO.getPhone());

        return driverDTOMapper.toDto(driverRepository.save(oldDriver));
    }

    @Override
    public void deleteById(Long id) {
        this.driverRepository.deleteById(id);
    }
}
