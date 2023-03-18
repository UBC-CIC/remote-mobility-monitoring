import random
import string
import misc.config as config

from locust import HttpUser, task, between
from tasks.caregiverTasks import CaregiverTasks
from tasks.organizationTasks import OrganizationTasks


class QuickstartUser(HttpUser):
    wait_time = between(1, 3)
    tasks = [CaregiverTasks, OrganizationTasks]

    organization_id = None
    admin_token = config.get('admin_token')

    def on_start(self):
        self.organization_id = config.get_organization_id()
        self.wait()
        pass
