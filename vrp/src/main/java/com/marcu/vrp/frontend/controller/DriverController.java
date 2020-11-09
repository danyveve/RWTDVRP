package com.marcu.vrp.frontend.controller;

import com.marcu.vrp.backend.dto.DriverDTO;
import com.marcu.vrp.backend.dto.RegisterUserDTO;
import com.marcu.vrp.backend.dto.VrpInstanceDTO;
import com.marcu.vrp.backend.model.Driver;
import com.marcu.vrp.backend.service.DriverService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/driver")
@PreAuthorize("isAuthenticated()")
public class DriverController {
    private DriverService driverService;

    public DriverController(final DriverService driverService) {
        this.driverService = driverService;
    }

    @GetMapping("/find/all")
    public List<DriverDTO> findAll() {
        return driverService.findAll();
    }

    @GetMapping("/find/filtered")
    public List<DriverDTO> findFiltered(@RequestParam(name = "filter") String filter, @RequestParam(name = "sort") String sort, @RequestParam(name = "isAscending") Boolean isAscending) {
        List<DriverDTO> all = this.driverService.findAll();
        if (sort != null && !sort.isEmpty()) {
            switch (sort) {
                case "firstName":
                    all.sort(Comparator.comparing(DriverDTO::getFirstName));
                    break;
                case "lastName":
                    all.sort(Comparator.comparing(DriverDTO::getLastName));
                    break;
                case "phone":
                    all.sort(Comparator.comparing(DriverDTO::getPhone));
                    break;
                case "email":
                    all.sort(Comparator.comparing(DriverDTO::getEmail));
                    break;
                case "car":
                    all.sort(Comparator.comparing(DriverDTO::getCar));
                    break;
            }

            if (!isAscending) {
                Collections.reverse(all);
            }
        }


        if (filter != null && !filter.isEmpty()) {
            filter = filter.toLowerCase();
            String finalFilter = filter;
            all = all.stream().filter(driverDTO -> {
                if (driverDTO.getFirstName().toLowerCase().contains(finalFilter)) {
                    return true;
                }
                if (driverDTO.getLastName().toLowerCase().contains(finalFilter)) {
                    return true;
                }
                if (driverDTO.getPhone().toLowerCase().contains(finalFilter)) {
                    return true;
                }
                if (driverDTO.getEmail().toLowerCase().contains(finalFilter)) {
                    return true;
                }
                if (driverDTO.getCar().toLowerCase().contains(finalFilter)) {
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
        }

        return all;
    }

    @PostMapping("/add")
    public DriverDTO add(@RequestBody DriverDTO driverDTO){
        return driverService.add(driverDTO);
    }

    @GetMapping("/find/{id}")
    public DriverDTO findById(@PathVariable(name = "id") Long id) {
        return this.driverService.findById(id);
    }

    @PostMapping("/edit/{id}")
    public DriverDTO edit(@RequestBody DriverDTO driverDTO, @PathVariable(name = "id") Long id){
        return driverService.edit(driverDTO, id);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable(name = "id") Long id) {
        this.driverService.deleteById(id);
    }

}
