package com.cpen491.remote_mobility_monitoring.function.handler;

import com.cpen491.remote_mobility_monitoring.function.Config;
import com.cpen491.remote_mobility_monitoring.function.service.AdminService;
import com.cpen491.remote_mobility_monitoring.function.service.AuthService;
import com.cpen491.remote_mobility_monitoring.function.service.CaregiverService;
import com.cpen491.remote_mobility_monitoring.function.service.OrganizationService;
import com.cpen491.remote_mobility_monitoring.function.service.PatientService;
import com.google.gson.Gson;
import org.crac.Core;
import org.crac.Resource;

public class HandlerParent implements Resource {
    protected final AuthService authService;
    protected final OrganizationService organizationService;
    protected final AdminService adminService;
    protected final CaregiverService caregiverService;
    protected final PatientService patientService;
    protected final Gson gson;

    public HandlerParent() {
        Core.getGlobalContext().register(this);
        Config config = Config.instance();
        this.authService = config.authService();
        this.organizationService = config.organizationService();
        this.adminService = config.adminService();
        this.caregiverService = config.caregiverService();
        this.patientService = config.patientService();
        this.gson = config.gson();
    }

    @Override
    public void beforeCheckpoint(org.crac.Context<? extends Resource> context) throws Exception {
        organizationService.prime();
        adminService.prime();
        caregiverService.prime();
        patientService.prime();
    }

    @Override
    public void afterRestore(org.crac.Context<? extends Resource> context) throws Exception {
        // Do nothing
    }
}
