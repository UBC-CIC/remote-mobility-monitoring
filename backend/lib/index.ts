import { App } from 'aws-cdk-lib';
import { DynamoDbStack } from './stack/dynamodb-stack';
import { LambdaStack } from './stack/lambda-stack';
import { ApiGatewayStack } from './stack/apigateway-stack';

const app = new App();
const dynamoDbStack = new DynamoDbStack(app, 'RemoteMobilityMonitoringDynamoStack');
const lambdaStack = new LambdaStack(app, 'RemoteMobilityMonitoringLambdaStack', dynamoDbStack);
new ApiGatewayStack(app, 'RemoteMobilityMonitoringApiGatewayStack', {
  defaultFunction: lambdaStack.defaultFunction,
  createCaregiverFunction: lambdaStack.createCaregiverFunction,
  createPatientFunction: lambdaStack.createPatientFunction,
  verifyPatientFunction: lambdaStack.verifyPatientFunction,
});
app.synth();
