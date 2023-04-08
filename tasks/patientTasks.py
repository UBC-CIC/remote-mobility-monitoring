from locust import TaskSet, task, tag

import misc.utils as utils
from misc import cognito

import requestformatter.patient as patient_rf
from misc import tokens


# Patient tasks
@tag('patient')
class PatientTasks(TaskSet):
    organization_id = None
    admin_token = tokens.admin_auth

    def __init__(self, parent: "User"):
        super().__init__(parent)
        self.patient_id = None
        self.patient_email = None
        self.patient_password = None
        self.patient_token = None

    def on_start(self):
        self.patient_id = None
        self.patient_email = None
        self.patient_password = None
        self.patient_token = None
        self.create_patient()

    def create_patient(self):
        email = "LoadTest-" + utils.random_email()
        password = "LoadTest-" + utils.random_string(10, upper_letters=True, digits=True, punctuation=True)
        with self.client.post(
                f'patients',
                json=patient_rf.create(
                    email=email,
                    password=password
                )
        ) as response:
            if response.status_code == 200:
                resp = response.json()
                self.patient_id = resp['patient_id']
                self.patient_email = email
                self.patient_password = password
                self.patient_token = cognito.authenticate(email, password).access_token
            else:
                print(response.text)
                self.interrupt()

    @task(3)
    def update_patient(self):
        self.client.put(
            f'patients/{self.patient_id}',
            name='/prod/patients/{patient_id}',
            json=patient_rf.update(),
            headers={'Authorization': f"Bearer {self.patient_token}"}
        )

    @task(3)
    def get_patient(self):
        self.client.get(
            f'patients/{self.patient_id}',
            name='/prod/patients/{patient_id}',
            headers={'Authorization': f"Bearer {self.patient_token}"}
        )

    @task(3)
    def add_metrics(self):
        self.client.post(
            f'metrics',
            json=patient_rf.add_metrics(self.patient_id),
            headers={'Authorization': f"Bearer {self.patient_token}"}
        )

    def delete_patient(self):
        self.client.delete(
            f'patients/{self.patient_id}',
            name='/prod/patients/{patient_id}',
            headers={'Authorization': f"Bearer {self.patient_token}"}
        )

    @task
    def stop(self):
        self.delete_patient()
        self.interrupt()
