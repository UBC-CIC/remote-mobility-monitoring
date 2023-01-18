import { App } from 'aws-cdk-lib';
import { DynamoDbStack } from './lib/dynamodb-stack';

const app = new App();
new DynamoDbStack(app, 'RemoteMobilityMonitoringStack');
app.synth();
