# Requirements
Before you deploy, you must have the following in place:
*  [Git](https://git-scm.com/)
*  [GitHub Account](https://github.com/)
*  [AWS Account](https://aws.amazon.com/account/)
*  [AWS CLI](https://aws.amazon.com/cli/)
*  [AWS CDK](https://docs.aws.amazon.com/cdk/latest/guide/getting_started.html)
*  [Amazon Corretto 11](https://docs.aws.amazon.com/corretto/latest/corretto-11-ug/downloads-list.html)
*  [Maven](https://maven.apache.org/)
*  [Node.js](https://nodejs.org/en/)
*  [Docker](https://www.docker.com/)

# Step 1: Clone The Repository
First, clone the GitHub repository onto your machine. To do this:
1. Create a folder on your desktop to contain the code.
2. Open terminal (or command prompt if on windows) and **cd** into the above folder.
3. Clone the GitHub repository by entering the following:
```bash
git clone https://github.com/UBC-CIC/remote-mobility-monitoring.git
```

The code should now be in the above folder. Now navigate into the remote-mobility-monitoring folder by running the following command:
```bash
cd remote-mobility-monitoring
```

# Step 2: Backend Deployment
Now that you have the code, you can deploy the backend. To do this:
1. Navigate into the backend folder by running the following command:
```bash
cd backend
```

2. Run the following command to install the CDK dependencies:
```bash
npm install
```

3. Run the following command to build the CDK code:
```bash
npm run build
```

4. Run the following command to spin up the AWS DynamoDB docker container for local testing:
```bash
docker-compose up -docker
```

5. Run the following command to build the Java AWS Lambda code:
```bash
mvn clean package
```

6. Run the following commands to initialize the CDK stacks (required only if you have not deployed this stack before). Note that by default, all stacks are created in `us-west-2` due to region restrictions for Amazon Timestream.
```bash
cdk synth
cdk bootstrap aws://YOUR_AWS_ACCOUNT_ID/us-west-2
```

If you have multiple AWS CLI profiles, you can specify the profile to use by running the following commands instead:
```bash
cdk synth --profile YOUR_AWS_CLI_PROFILE
cdk bootstrap aws://YOUR_AWS_ACCOUNT_ID/us-west-2 --profile YOUR_AWS_CLI_PROFILE
```

7. Lastly, deploy the CDK stacks:
```bash
cdk deploy --all --profile YOUR_AWS_CLI_PROFILE
```

8. Optionally, the CDK stacks can be deployed individually by running the following commands:
```bash
cdk deploy RemoteMobilityMonitoringCognitoStack-dev --profile YOUR_AWS_CLI_PROFILE
cdk deploy RemoteMobilityMonitoringCognitoStack-prod --profile YOUR_AWS_CLI_PROFILE
cdk deploy RemoteMobilityMonitoringDynamoStack-dev --profile YOUR_AWS_CLI_PROFILE
cdk deploy RemoteMobilityMonitoringDynamoStack-prod --profile YOUR_AWS_CLI_PROFILE
cdk deploy RemoteMobilityMonitoringSesStack --profile YOUR_AWS_CLI_PROFILE
cdk deploy RemoteMobilityMonitoringTimestreamStack-dev --profile YOUR_AWS_CLI_PROFILE
cdk deploy RemoteMobilityMonitoringTimestreamStack-prod --profile YOUR_AWS_CLI_PROFILE
cdk deploy RemoteMobilityMonitoringLambdaStack-dev --profile YOUR_AWS_CLI_PROFILE
cdk deploy RemoteMobilityMonitoringLambdaStack-prod --profile YOUR_AWS_CLI_PROFILE
cdk deploy RemoteMobilityMonitoringApiGatewayStack-dev --profile YOUR_AWS_CLI_PROFILE
cdk deploy RemoteMobilityMonitoringApiGatewayStack-prod --profile YOUR_AWS_CLI_PROFILE
```

# Step 3: Frontend Deployment
