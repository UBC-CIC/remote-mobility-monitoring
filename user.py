import random
import string
import misc.config as config

from locust import HttpUser, task, between
from tasks.caregiverTasks import CaregiverTasks
from tasks.organizationTasks import OrganizationTasks
from tasks.patientTasks import PatientTasks
from misc import tokens


class QuickstartUser(HttpUser):
    wait_time = between(1, 3)
    tasks = [CaregiverTasks, OrganizationTasks, PatientTasks]

    organization_id = None
    admin_token = tokens.admin_auth

    def on_start(self):
        self.organization_id = config.get_organization_id()
        self.wait()
        pass
