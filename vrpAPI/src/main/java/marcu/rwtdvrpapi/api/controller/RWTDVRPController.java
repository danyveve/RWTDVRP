package marcu.rwtdvrpapi.api.controller;

import marcu.rwtdvrpapi.api.domain.Solution;
import marcu.rwtdvrpapi.api.domain.VRPInstance;
import marcu.rwtdvrpapi.api.service.RWTDVRPService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rwtdvrp")
public class RWTDVRPController {

    private RWTDVRPService rwtdvrpService;

    public RWTDVRPController(final RWTDVRPService rwtdvrpService) {
        this.rwtdvrpService = rwtdvrpService;
    }

    @PostMapping("/start")
    @ResponseBody
    public ResponseEntity<String> startSolvingVrp(@RequestBody VRPInstance vrpInstance) throws Exception {
        String id = rwtdvrpService.start(vrpInstance);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(id, httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/stop/{id}")
    @ResponseBody
    public ResponseEntity<Solution> stopSolvingVrp(@PathVariable String id) throws Exception {
        Solution solution = rwtdvrpService.stop(id);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(solution, httpHeaders, HttpStatus.OK);
    }
}
