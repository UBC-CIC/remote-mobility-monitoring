from misc import cognito
from misc import config

admin_token = cognito.authenticate(config.get('ADMIN_EMAIL'), config.get('ADMIN_PASSWORD')).access_token
admin_auth = f"Bearer {admin_token}"