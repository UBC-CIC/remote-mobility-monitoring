# Mobimon Load Testing
This is a load testing project for Mobimon. It uses [Locust](https://locust.io/) to simulate user behavior in the backend.

## Prerequisites
1. Install [Python](https://www.python.org/downloads/)
2. Install requirements: `pip install -r requirements.txt`
3. Retrieve admin username / password, and the client and userpool ID from Mobimon cognito pool
4. Copy config.ini from config.ini.example and fill in appropriate fields

## Running Locust
1. Run Locust: `locust -f user.py`
2. Open Locust in your browser: `http://localhost:8089`
3. Set the number of users, spawn rate, and Mobimon base URL
4. Click "Start Swarming"
5. Profit

## Locust Options
### Useful options for running Locust:
- `-host`: Host to load test (format: `http://localhost:8080/`)
- `-u`: Number of users to simulate
- `-r`: Spawn rate (users per second)
- `-t`: Time to run test (format: `10s`, `1m`, `1h`, `1h30m`, etc.) Only used together with --headless or --autostart
- `--headless`: Run Locust in headless mode (no web interface)
- `--autostart`: Start swarming as soon as the test starts (only used together with --headless)

### Tags:
- `-T [TAG [TAG ...]]`: Only run tests with the specified tag (ex`-T organization`)
- `-E [TAG [TAG ...]]`: Exclude tests with the specified tag (ex `-E caregiver`)

use `locust -h` to see all options

## Running User Pruning Scripts
Also included are two scripts to prune users from the AWS Cognito user pool, and the DynamoDB tables. These are useful 
for cleaning up after a load test.
To run the scripts, you must have the AWS CLI installed and configured with credentials that have access to the AWS 
account where Mobimon is deployed.
1. Run `python delete_cognito_users.py` to delete all loadtest users from the Cognito user pool
2. Run `python delete_dynamodb_users.py` to delete all loadtest users from the DynamoDB tables
Note: The scripts will delete all users with emails starting with "LoadTest". If you have other users in your Cognito
user pool or DynamoDB tables with emails starting with "LoadTest", they will be deleted as well.
