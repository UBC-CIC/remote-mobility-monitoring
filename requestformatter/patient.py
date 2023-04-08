import random

import misc.config as config
import misc.utils as utils

organization_id = config.get_organization_id()


def create(email=None, password=None, first_name=None, last_name=None, phone_number=None, sex=None, birthday=None,
           height=None, weight=None):
    if email is None:
        email = "LoadTest-" + utils.random_email()
    if first_name is None:
        first_name = "LoadTest-" + utils.random_string(10, upper_letters=False, digits=False, punctuation=False)
    if last_name is None:
        last_name = "LoadTest-" + utils.random_string(10, upper_letters=False, digits=False, punctuation=False)
    if phone_number is None:
        phone_number = utils.random_phone_number()
    if password is None:
        password = utils.random_string(10, upper_letters=True, digits=True, punctuation=True)
    if sex is None:
        sex = utils.random_sex()
    if birthday is None:
        birthday = utils.random_date()
    if height is None:
        height = random.randint(100, 200)
    if weight is None:
        weight = random.randint(50, 200)

    return {
        "email": email,
        "password": password,
        "first_name": first_name,
        "last_name": last_name,
        "phone_number": phone_number,
        "sex": sex,
        "birthday": birthday,
        "height": height,
        "weight": weight
    }


def update(first_name=None, last_name=None, phone_number=None, sex=None, birthday=None, height=None, weight=None):
    if first_name is None:
        first_name = "LoadTest-" + utils.random_string(10, upper_letters=False, digits=False, punctuation=False)
    if last_name is None:
        last_name = "LoadTest-" + utils.random_string(10, upper_letters=False, digits=False, punctuation=False)
    if phone_number is None:
        phone_number = utils.random_phone_number()
    if sex is None:
        sex = utils.random_sex()
    if birthday is None:
        birthday = utils.random_date()
    if height is None:
        height = random.randint(100, 200)
    if weight is None:
        weight = random.randint(50, 200)

    return {
        "first_name": first_name,
        "last_name": last_name,
        "phone_number": phone_number,
        "sex": sex,
        "birthday": birthday,
        "height": height,
        "weight": weight
    }


def generate_metric():
    return {
        "step_length": f"{random.randint(50, 100)}",
        "double_support_time": f"{random.randint(10, 100) / 100}",
        "walking_speed": f"{random.randint(40, 80) / 10}",
        "walking_asymmetry": f"{random.randint(10, 100) / 1000}",
        "distance_walked": f"{random.randint(10, 200) / 10}",
        "step_count": f"{random.randint(100, 10000)}",
        "walking_steadiness": f"{random.randint(10, 100) / 100}",
        "timestamp": utils.current_timestamp()
    }


def add_metrics(patient_id: str):
    return {
        'patient_id': patient_id,
        'metrics': [generate_metric() for _ in range(random.randint(1, 1))]
    }
