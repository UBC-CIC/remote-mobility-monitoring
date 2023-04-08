from pycognito import Cognito
from misc import config

CLIENT_ID = config.get('CLIENT_ID')
USERPOOL_ID = config.get('USERPOOL_ID')


def authenticate(username, password):
    u = Cognito(USERPOOL_ID, CLIENT_ID, username=username)
    u.authenticate(password=password)
    return u  # u.id_token, u.access_token, u.refresh_token


def change_password(old_password, new_password, user: Cognito):
    u = Cognito(
        USERPOOL_ID,
        CLIENT_ID,
        id_token=user.id_token,
        access_token=user.access_token,
        refresh_token=user.refresh_token
    )
    u.change_password(old_password, new_password)


def auth_and_change_password(username, old_password, new_password) -> Cognito:
    u = Cognito(USERPOOL_ID, CLIENT_ID, username=username)
    u.authenticate(password=old_password)
    u.change_password(old_password, new_password)
    return u


def main():
    u = authenticate('patient6@roydu.me', 'Password.123')
    print(u.access_token)


if __name__ == '__main__':
    main()
