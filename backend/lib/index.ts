import { App } from 'aws-cdk-lib';
import { DynamoDbStack } from './stack/dynamodb-stack';
import { LambdaStack } from './stack/lambda-stack';
import { CognitoStack } from './stack/cognito-stack';
import { TimestreamStack } from './stack/timestream-stack';
import { ApiGatewayStack } from './stack/apigateway-stack';

// TODO: create dev stage
const app = new App();
new CognitoStack(app, 'RemoteMobilityMonitoringCognitoStack');
new TimestreamStack(app, 'RemoteMobilityMonitoringTimestreamStack');
const dynamoDbStack = new DynamoDbStack(app, 'RemoteMobilityMonitoringDynamoStack');
const lambdaStack = new LambdaStack(app, 'RemoteMobilityMonitoringLambdaStack', {
  table: dynamoDbStack.remoteMobilityMonitoringTable,
});
new ApiGatewayStack(app, 'RemoteMobilityMonitoringApiGatewayStack', {
  defaultFunction: lambdaStack.defaultFunction,
  getOrganizationFunction: lambdaStack.getOrganizationFunction,
  getAdminFunction: lambdaStack.getAdminFunction,
  createCaregiverFunction: lambdaStack.createCaregiverFunction,
  addPatientFunction: lambdaStack.addPatientFunction,
  removePatientFunction: lambdaStack.removePatientFunction,
  getCaregiverFunction: lambdaStack.getCaregiverFunction,
  getAllPatientsFunction: lambdaStack.getAllPatientsFunction,
  updateCaregiverFunction: lambdaStack.updateCaregiverFunction,
  deleteCaregiverFunction: lambdaStack.deleteCaregiverFunction,
  createPatientFunction: lambdaStack.createPatientFunction,
  updatePatientDeviceFunction: lambdaStack.updatePatientDeviceFunction,
  verifyPatientFunction: lambdaStack.verifyPatientFunction,
  getPatientFunction: lambdaStack.getPatientFunction,
  getAllCaregiversFunction: lambdaStack.getAllCaregiversFunction,
  updatePatientFunction: lambdaStack.updatePatientFunction,
  deletePatientFunction: lambdaStack.deletePatientFunction,
});
app.synth();
