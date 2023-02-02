import { App } from 'aws-cdk-lib';
import { DynamoDbStack } from './stack/dynamodb-stack';
import { LambdaStack } from './stack/lambda-stack';
import { CognitoStack } from './stack/cognito-stack';
import { TimestreamStack } from './stack/timestream-stack';
import { ApiGatewayStack } from './stack/apigateway-stack';

const app = new App();
const cognitoStack = new CognitoStack(app, 'RemoteMobilityMonitoringCognitoStack');
new TimestreamStack(app, 'RemoteMobilityMonitoringTimestreamStack');
const dynamoDbStack = new DynamoDbStack(app, 'RemoteMobilityMonitoringDynamoStack');
const lambdaStack = new LambdaStack(app, 'RemoteMobilityMonitoringLambdaStack', {
  table: dynamoDbStack.remoteMobilityMonitoringTable,
  userPool: cognitoStack.userPool,
});
new ApiGatewayStack(app, 'RemoteMobilityMonitoringApiGatewayStack', {
  defaultFunction: lambdaStack.defaultFunction,
  getOrganizationFunction: lambdaStack.getOrganizationFunction,
  createCaregiverFunction: lambdaStack.createCaregiverFunction,
  addPatientFunction: lambdaStack.addPatientFunction,
  removePatientFunction: lambdaStack.removePatientFunction,
  getCaregiverFunction: lambdaStack.getCaregiverFunction,
  getAllPatientsFunction: lambdaStack.getAllPatientsFunction,
  deleteCaregiverFunction: lambdaStack.deleteCaregiverFunction,
  createPatientFunction: lambdaStack.createPatientFunction,
  updatePatientDeviceFunction: lambdaStack.updatePatientDeviceFunction,
  verifyPatientFunction: lambdaStack.verifyPatientFunction,
  getPatientFunction: lambdaStack.getPatientFunction,
  deletePatientFunction: lambdaStack.deletePatientFunction,
  testFunction: lambdaStack.testFunction
});
app.synth();
