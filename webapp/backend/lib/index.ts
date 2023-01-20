import { App } from 'aws-cdk-lib';
import { DynamoDbStack } from './stack/dynamodb-stack';
import { LambdaStack } from './stack/lambda-stack';

const app = new App();
new DynamoDbStack(app, 'RemoteMobilityMonitoringDynamoStack');
new LambdaStack(app, 'RemoteMobilityMonitoringLambdaStack')
app.synth();
