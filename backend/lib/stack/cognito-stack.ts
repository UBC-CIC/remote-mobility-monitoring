import * as cdk from 'aws-cdk-lib';
import * as cognito from 'aws-cdk-lib/aws-cognito';

export class CognitoStack extends cdk.Stack {
  public readonly userPool: cognito.UserPool;

  constructor(scope: cdk.App, id: string, props?: cdk.StackProps) {
    super(scope, id, props);
    
    this.userPool = new cognito.UserPool(this, 'RemoteMobilityMonitoringUserPool', {
      userPoolName: 'RemoteMobilityMonitoringUserPool',
      selfSignUpEnabled: false,
      signInCaseSensitive: true
    });
  }
}
