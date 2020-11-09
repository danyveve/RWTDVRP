package com.marcu.vrp.backend.adapter;

import com.marcu.vrp.backend.model.VrpInstance;

public interface VrpToProviderAdapter<RESULT, SOLUTION> {
    RESULT doVrpRequest(VrpInstance vrpInstance);
    SOLUTION stopVrpRequest(Long id);
}
