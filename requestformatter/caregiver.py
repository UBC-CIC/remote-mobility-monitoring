import misc.config as config
import misc.utils as utils

organization_id = config.get_organization_id()


def create(email=None, first_name=None, last_name=None, title=None, phone_number=None, password=None, org_id=organization_id):
    if email is None:
        email = "LoadTest-" + utils.random_email()
    if first_name is None:
        first_name = "LoadTest-" + utils.random_string(10, upper_letters=False, digits=False, punctuation=False)
    if last_name is None:
        last_name = "LoadTest-" + utils.random_string(10, upper_letters=False, digits=False, punctuation=False)
    if title is None:
        title = utils.random_title()
    if phone_number is None:
        phone_number = utils.random_phone_number()
    if password is None:
        password = utils.random_string(10, upper_letters=True, digits=True, punctuation=True)

    return {
        "email": email,
        "first_name": first_name,
        "last_name": last_name,
        "title": title,
        "phone_number": phone_number,
        "organization_id": org_id,
        "password": password,
        "suppress_email": True,
        "skip_password_change": True
    }

def add_patient(email:str):
    return {
        "patient_email": email
    }

def patient_accept_invite(auth_code:str):
    return {
        "auth_code": auth_code
    }

def update(first_name=None, last_name=None, title=None, phone_number=None):
    if first_name is None:
        first_name = "LoadTest-" + utils.random_string(10, upper_letters=False, digits=False, punctuation=False)
    if last_name is None:
        last_name = "LoadTest-" + utils.random_string(10, upper_letters=False, digits=False, punctuation=False)
    if title is None:
        title = utils.random_title()
    if phone_number is None:
        phone_number = utils.random_phone_number()

    return {
        "first_name": first_name,
        "last_name": last_name,
        "title": title,
        "phone_number": phone_number
    }

