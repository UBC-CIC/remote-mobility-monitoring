import random
import string
from datetime import datetime

email_domains = ['gmail.com', 'yahoo.com', 'hotmail.com', 'outlook.com', 'aol.com', 'protonmail.com', 'icloud.com']
titles = ['Mr.', 'Mrs.', 'Ms.', 'Dr.', 'Prof.']


# Generate random valid email address
def random_email(length: int = 10, email_domain: str | None = None) -> str:
    if email_domain is None:
        email_domain = random.choice(email_domains)
    return random_string(punctuation=False, lower_letters=False) + '@' + email_domain


# Generate random valid phone number
def random_phone_number(country_code: str = '+1') -> str:
    return country_code + ''.join(random.choices(string.digits, k=10))


# Generate random string
def random_string(length: int = 10, lower_letters: bool = True, upper_letters: bool = True, digits: bool = True,
                  punctuation: bool = True) -> str:
    charset = ''
    if lower_letters:
        charset += string.ascii_lowercase
    if upper_letters:
        charset += string.ascii_uppercase
    if digits:
        charset += string.digits
    if punctuation:
        charset += string.punctuation

    ret_val = ''.join(random.choices(string.ascii_lowercase + string.digits, k=length))
    if lower_letters:
        ret_val += random.choice(string.ascii_lowercase)
    if upper_letters:
        ret_val += random.choice(string.ascii_uppercase)
    if digits:
        ret_val += random.choice(string.digits)
    if punctuation:
        ret_val += random.choice(string.punctuation)
    return ret_val


# Generate random sex
def random_sex() -> str:
    return random.choice(['M', 'F', 'O'])


# Generate random date
def random_date() -> str:
    return str(random.randint(1900, 2010)) + '-' + str("%02d" % random.randint(1, 12)) + '-' + str(
        "%02d" % random.randint(1, 28))

# Generate current timestamp
def current_timestamp() -> str:
    return str(datetime.now().replace(microsecond=0).isoformat())


def random_title() -> str:
    return random.choice(titles)
