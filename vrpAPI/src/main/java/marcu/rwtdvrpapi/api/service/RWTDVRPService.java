package marcu.rwtdvrpapi.api.service;

import marcu.rwtdvrpapi.api.domain.Solution;
import marcu.rwtdvrpapi.api.domain.VRPInstance;

public interface RWTDVRPService {
    String start(VRPInstance vrpInstance) throws Exception;
    Solution stop(String id) throws Exception;
}
