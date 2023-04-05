import random
import string

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

    return ''.join(random.choices(string.ascii_lowercase + string.digits, k=length))


def random_title() -> str:
    return random.choice(titles)
