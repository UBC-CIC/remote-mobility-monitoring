import { App } from 'aws-cdk-lib';
import { DynamoDbStack } from './stack/dynamodb-stack';

const app = new App();
new DynamoDbStack(app, 'RemoteMobilityMonitoringStack');
app.synth();
