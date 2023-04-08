import random

from locust import TaskSet, task, tag

import misc.utils as utils
from misc import cognito

import requestformatter.caregiver as caregiver_rf
import requestformatter.patient as patient_rf
from misc import tokens


# Caregiver tasks
@tag('caregiver')
class CaregiverTasks(TaskSet):
    organization_id = None
    admin_token = tokens.admin_auth

    def __init__(self, parent: "User"):
        super().__init__(parent)
        self.caregiver_token = None
        self.caregiver_id = None
        self.caregiver_email = None
        self.caregiver_password = None
        self.patients = []

    def on_start(self):
        self.caregiver_id = None
        self.caregiver_email = None
        self.caregiver_password = None
        self.caregiver_token = None
        self.patients = []
        self.create_caregiver()
        pass

    def create_caregiver(self):
        email = "LoadTest-" + utils.random_email()
        password = "LoadTest-" + utils.random_string(10, upper_letters=True, digits=True, punctuation=True)
        with self.client.post(
                f'caregivers',
                headers={'Authorization': self.admin_token},
                json=caregiver_rf.create(
                    email=email,
                    password=password
                )
        ) as response:
            if response.status_code == 200:
                resp = response.json()
                self.caregiver_id = resp['caregiver_id']
                self.caregiver_email = email
                self.caregiver_password = password
                self.caregiver_token = cognito.authenticate(self.caregiver_email, self.caregiver_password).access_token
            else:
                print(response.text)
                self.interrupt()

    @task
    def get_caregiver(self):
        self.client.get(
            f'caregivers/{self.caregiver_id}',
            name='/prod/caregivers/{caregiver_id}',
            headers={'Authorization': f"Bearer {self.caregiver_token}"}
        )

    @task
    def add_patient(self):
        # Create Patient
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
                self.patients.append((
                    resp['patient_id'],
                    email,
                    password,
                    cognito.authenticate(email, password).access_token
                ))
            else:
                print(response.text)
                return

        # Add Patient to Caregiver
        auth_code = ""
        with self.client.post(
            f'caregivers/{self.caregiver_id}/patients',
            name='/prod/caregivers/{caregiver_id}/patients',
            json=caregiver_rf.add_patient(self.patients[-1][1]),
            headers={'Authorization': f"Bearer {self.caregiver_token}"}
        ) as response:
            if response.status_code != 200:
                print(response.text)
            else:
                resp = response.json()
                auth_code = resp['auth_code']

        # Accept caregiver request
        self.client.post(
            f'caregivers/{self.caregiver_id}/patients/{self.patients[-1][0]}/accept',
            name='/prod/caregivers/{caregiver_id}/patients/{patient_id}/accept',
            json=caregiver_rf.patient_accept_invite(auth_code),
            headers={"Authorization": f"Bearer {self.patients[-1][3]}"}
        )



    @task
    def remove_patients(self):
        while(len(self.patients) > 0):
            patient = self.patients.pop()
            self.client.delete(
                f'caregivers/{self.caregiver_id}/patients/{patient[0]}',
                name='/prod/caregivers/{caregiver_id}/patients/{patient_id}',
                headers={"Authorization": f"Bearer {self.caregiver_token}"}
            )
            self.client.delete(
                f'patients/{patient[0]}',
                name='/prod/patients/{patient_id}',
                headers={'Authorization': f"Bearer {patient[3]}"}
            )

    @task
    def update_caregiver(self):
        self.client.put(
            f'caregivers/{self.caregiver_id}',
            name='/prod/caregivers/{caregiver_id}',
            json=caregiver_rf.update(),
            headers={'Authorization': f"Bearer {self.caregiver_token}"}
        )

    @task
    def get_caregiver_patients(self):
        self.client.get(
            f'caregivers/{self.caregiver_id}/patients',
            name='/prod/caregivers/{caregiver_id}/patients',
            headers={'Authorization': f"Bearer {self.caregiver_token}"}
        )

    @task
    def query_metrics(self):
        if len(self.patients) == 0:
            return
        patient = random.choice(self.patients)
        with self.client.get(
            f'metrics',
            name='/prod/metrics',
            params={
                'patients': patient[0],
                'start': '2000-01-01T00:00:00',
                'end': utils.current_timestamp(),
                'min_age': 0,
                'max_age': 100,
                'min_weight': 0,
                'max_weight': 1000,
                'min_height': 0,
                'max_height': 300
            },
            headers={'Authorization': f"Bearer {self.caregiver_token}"}
        ) as response:
            if response.status_code != 200:
                print(response.text)

    def delete_caregiver(self):
        self.client.delete(
            f'caregivers/{self.caregiver_id}',
            name='/post/caregivers/{caregiver_id}',
            headers={'Authorization': self.admin_token}
        )

    @task
    def stop(self):
        self.delete_caregiver()
        self.remove_patients()
        self.interrupt()
