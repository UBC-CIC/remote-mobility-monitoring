import { App } from 'aws-cdk-lib';
import { DynamoDbStack } from './stack/dynamodb-stack';
import { LambdaStack } from './stack/lambda-stack';
import { ApiGatewayStack } from './stack/apigateway-stack';

const app = new App();
const dynamoDbStack = new DynamoDbStack(app, 'RemoteMobilityMonitoringDynamoStack');
const lambdaStack = new LambdaStack(app, 'RemoteMobilityMonitoringLambdaStack', {
  table: dynamoDbStack.remoteMobilityMonitoringTable,
});
new ApiGatewayStack(app, 'RemoteMobilityMonitoringApiGatewayStack', {
  defaultFunction: lambdaStack.defaultFunction,
  createCaregiverFunction: lambdaStack.createCaregiverFunction,
  getCaregiverFunction: lambdaStack.getCaregiverFunction,
  deleteCaregiverFunction: lambdaStack.deleteCaregiverFunction,
  createPatientFunction: lambdaStack.createPatientFunction,
  updatePatientDeviceFunction: lambdaStack.updatePatientDeviceFunction,
  verifyPatientFunction: lambdaStack.verifyPatientFunction,
  getPatientFunction: lambdaStack.getPatientFunction,
  deletePatientFunction: lambdaStack.deletePatientFunction,
});
app.synth();
