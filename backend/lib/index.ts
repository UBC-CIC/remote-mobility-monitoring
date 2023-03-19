import { App } from 'aws-cdk-lib';
import { DynamoDbStack } from './stack/dynamodb-stack';
import { LambdaStack } from './stack/lambda-stack';
import { CognitoStack } from './stack/cognito-stack';
import { TimestreamStack } from './stack/timestream-stack';
import { ApiGatewayStack, ApiGatewayStackProps } from './stack/apigateway-stack';
import { SesStack } from "./stack/ses-stack";

const stages = ['dev', 'prod'];

const app = new App();
const sesStack = new SesStack(app, 'RemoteMobilityMonitoringSesStack');
stages.forEach((stage) => {
  const cognitoStack = new CognitoStack(app, `RemoteMobilityMonitoringCognitoStack-${stage}`, {
    stage: stage,
  });
  const timestreamStack = new TimestreamStack(app, `RemoteMobilityMonitoringTimestreamStack-${stage}`, {
    stage: stage,
  });
  const dynamoDbStack = new DynamoDbStack(app, `RemoteMobilityMonitoringDynamoStack-${stage}`, {
    stage: stage,
  });
  const lambdaStack = new LambdaStack(app, `RemoteMobilityMonitoringLambdaStack-${stage}`, {
    stage: stage,
    ddbTable: dynamoDbStack.remoteMobilityMonitoringTable,
    timestreamDatabase: timestreamStack.database,
    timestreamTable: timestreamStack.metricsTable,
    userPool: cognitoStack.userPool,
    sesSender: sesStack.senderEmailIdentity.emailIdentityName,
  });
  const apigatewayStackProps: ApiGatewayStackProps = {
    stage: stage,
    userPool: cognitoStack.userPool,
    defaultFunction: lambdaStack.defaultFunction,
    getOrganizationFunction: lambdaStack.getOrganizationAlias,
    getAdminFunction: lambdaStack.getAdminAlias,
    deleteAdminFunction: lambdaStack.deleteAdminAlias,
    createCaregiverFunction: lambdaStack.createCaregiverAlias,
    addPatientPrimaryFunction: lambdaStack.addPatientPrimaryAlias,
    acceptPatientPrimaryFunction: lambdaStack.acceptPatientPrimaryAlias,
    addPatientFunction: lambdaStack.addPatientAlias,
    removePatientFunction: lambdaStack.removePatientAlias,
    getCaregiverFunction: lambdaStack.getCaregiverAlias,
    getAllPatientsFunction: lambdaStack.getAllPatientsAlias,
    updateCaregiverFunction: lambdaStack.updateCaregiverAlias,
    deleteCaregiverFunction: lambdaStack.deleteCaregiverAlias,
    createPatientFunction: lambdaStack.createPatientAlias,
    getPatientFunction: lambdaStack.getPatientAlias,
    getAllCaregiversFunction: lambdaStack.getAllCaregiversAlias,
    addMetricsFunction: lambdaStack.addMetricsAlias,
    queryMetricsFunction: lambdaStack.queryMetricsAlias,
    updatePatientFunction: lambdaStack.updatePatientAlias,
    deletePatientFunction: lambdaStack.deletePatientAlias,
  }
  new ApiGatewayStack(app, `RemoteMobilityMonitoringApiGatewayStack-${stage}`, apigatewayStackProps);
})
app.synth();
