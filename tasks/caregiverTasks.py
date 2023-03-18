from locust import TaskSet, task, tag

import misc.utils as utils

from misc import config
import requestformatter.caregiver as caregiver_rf


# Caregiver tasks
@tag('caregiver')
class CaregiverTasks(TaskSet):
    organization_id = None
    admin_token = "Bearer " + config.get('admin_token')

    created_caregivers = []

    def on_start(self):
        pass

    @task
    def create_caregiver(self):
        with self.client.post(
                f'caregivers',
                headers={'Authorization': self.admin_token},
                json=caregiver_rf.create()
        ) as response:
            if response.status_code == 200:
                resp = response.json()
                self.created_caregivers.append(resp['caregiver_id'])

    @task
    def delete_caregiver(self):
        if len(self.created_caregivers) > 0:
            caregiver_id = self.created_caregivers.pop()
            self.client.delete(
                f'caregivers/{caregiver_id}',
                name='/post/caregivers/{caregiver_id}',
                headers={'Authorization': self.admin_token}
            )

    @task
    def stop(self):
        self.interrupt()
