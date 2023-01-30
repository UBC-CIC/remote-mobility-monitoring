import { App } from 'aws-cdk-lib';
import { DynamoDbStack } from './stack/dynamodb-stack';
import { LambdaStack } from './stack/lambda-stack';
import { CognitoStack } from './stack/cognito-stack';
import { TimestreamStack } from './stack/timestream-stack';

const app = new App();
new DynamoDbStack(app, 'RemoteMobilityMonitoringDynamoStack');
new LambdaStack(app, 'RemoteMobilityMonitoringLambdaStack');
new CognitoStack(app, 'RemoteMobilityMonitoringCognitoStack');
new TimestreamStack(app, 'RemoteMobilityMonitoringTimeStreamStack');
app.synth();
