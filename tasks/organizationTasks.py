from locust import TaskSet, task
import configparser
import misc.config as config


# Organization tests
class OrganizationTasks(TaskSet):
    organization_id = None
    admin_token = config.get('admin_token')

    def on_start(self):
        self.organization_id = config.get_organization_id()
        pass

    @task
    def get_organization(self):
        self.client.get(f'organizations/{self.organization_id}')

    @task
    def stop(self):
        self.interrupt()
