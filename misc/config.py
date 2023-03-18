import configparser


def get_organization_id():
    return get('organization_id')


def get(attribute):
    config_parser = configparser.ConfigParser()
    config_parser.read('config.ini')
    return config_parser['load-testing'][attribute]
