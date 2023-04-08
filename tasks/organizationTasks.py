from locust import TaskSet, task, tag
import configparser
import misc.config as config
from misc import tokens


# Organization tests
@tag('organization')
class OrganizationTasks(TaskSet):
    organization_id = None
    admin_token = tokens.admin_auth

    def on_start(self):
        self.organization_id = config.get_organization_id()
        pass

    @task(3)
    def get_organization(self):
        self.client.get(f'organizations/{self.organization_id}')

    @task
    def stop(self):
        self.interrupt()
