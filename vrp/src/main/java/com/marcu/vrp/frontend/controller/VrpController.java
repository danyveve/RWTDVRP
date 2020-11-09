package com.marcu.vrp.frontend.controller;

import com.marcu.vrp.backend.dto.GeographicPointToRouteAssignmentDTO;
import com.marcu.vrp.backend.dto.StartVrpSolverRequestDTO;
import com.marcu.vrp.backend.dto.VrpInstanceDTO;
import com.marcu.vrp.backend.model.VrpInstance;
import com.marcu.vrp.backend.service.UserService;
import com.marcu.vrp.backend.service.VrpService;
import com.mysql.cj.xdevapi.Collection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/vrp")
@PreAuthorize("isAuthenticated()")
public class VrpController {
    private VrpService vrpService;
    private UserService userService;

    public VrpController(final VrpService vrpService,
                         final UserService userService) {
        this.vrpService = vrpService;
        this.userService = userService;
    }

    @PostMapping("/start")
    @ResponseBody
    public ResponseEntity<Long> startVrp(@RequestBody StartVrpSolverRequestDTO startVrpSolverRequestDTO, Principal principal) {
        startVrpSolverRequestDTO.setUserId(userService.loadUserByUsername(principal.getName()).getId());
        Long id = vrpService.startVrp(startVrpSolverRequestDTO);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(id, httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/find/all")
    public List<VrpInstanceDTO> findAll(Principal principal) {
        return this.vrpService.findAll(principal);
    }

    @GetMapping("/get/{id}")
    public VrpInstanceDTO findById(@PathVariable Long id) {

        VrpInstanceDTO vrp = this.vrpService.findById(id);
        vrp.getRoutes().forEach(routeDTO -> {
            routeDTO.getGeographicPointToRouteAssignments().sort(Comparator.comparing(GeographicPointToRouteAssignmentDTO::getIndexInRoute));
        });
        return vrp;
    }

    @GetMapping("/stop/{id}")
    public VrpInstanceDTO stop(@PathVariable Long id) {
        return this.vrpService.stopVrp(id);
    }

    @GetMapping("/find/filtered")
    public List<VrpInstanceDTO> findFiltered(@RequestParam(name = "filter") String filter, @RequestParam(name = "sort") String sort, @RequestParam(name = "isAscending") Boolean isAscending, Principal principal) {
        List<VrpInstanceDTO> all = this.vrpService.findAll(principal);

        if (sort != null && !sort.isEmpty()) {
            switch (sort) {
                case "createdOn":
                    all.sort(Comparator.comparing(VrpInstanceDTO::getCreatedOn));
                    break;
                case "depot":
                    all.sort(Comparator.comparing(o -> o.getDepot().getAddress()));
                    break;
                case "deliveryPoints":
                    all.sort(Comparator.comparing(o -> o.getDeliveryPoints().size()));
                    break;
                case "drivers":
                    all.sort(Comparator.comparing(o -> o.getRoutes().size()));
                    break;
                case "preferredDeparture":
                    all.sort(Comparator.comparing(VrpInstanceDTO::getPreferredDepartureTime));
                    break;
                case "suggestedDeparture":
                    List<VrpInstanceDTO> isNull = all.stream().filter(vrp -> vrp.getSuggestedDepartureTime() == null).collect(Collectors.toList());
                    all = all.stream().filter(vrp -> vrp.getSuggestedDepartureTime() != null).collect(Collectors.toList());
                    all.sort(Comparator.comparing(VrpInstanceDTO::getSuggestedDepartureTime));
                    all.addAll(0, isNull);
                    break;
                case "totalTime":
                    List<VrpInstanceDTO> isNull2 = all.stream().filter(vrp -> vrp.getTotalCost() == null).collect(Collectors.toList());
                    all = all.stream().filter(vrp -> vrp.getTotalCost() != null).collect(Collectors.toList());
                    all.sort(Comparator.comparing(VrpInstanceDTO::getTotalCost));
                    all.addAll(0, isNull2);
                    break;
            }

            if (!isAscending) {
                Collections.reverse(all);
            }
        }


        if (filter != null && !filter.isEmpty()) {
            filter = filter.toLowerCase();
            String finalFilter = filter;
            all = all.stream().filter(vrp -> {
                if (vrp.getDepot().getAddress().toLowerCase().contains(finalFilter)) {
                    return true;
                }
                if (vrp.getDeliveryPoints().stream().map(dp -> dp.getGeographicPoint().getAddress()).reduce("", String::concat).toLowerCase().contains(finalFilter)) {
                    return true;
                }
                if (vrp.getRoutes().stream().map(r -> r.getDriver().getCar() + r.getDriver().getEmail() + r.getDriver().getFirstName() + r.getDriver().getLastName() + r.getDriver().getPhone()).reduce("", String::concat).toLowerCase().contains(finalFilter)) {
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
        }

        return all;
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable(name = "id") Long id) {
        this.vrpService.deleteById(id);
    }
}
