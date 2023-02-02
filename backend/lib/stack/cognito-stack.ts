import * as cdk from 'aws-cdk-lib';
import * as cognito from 'aws-cdk-lib/aws-cognito';

export class CognitoStack extends cdk.Stack {
  private static CALLBACK_URLS = ['https://example.com/callback'];
  private static LOGOUT_URLS = ['https://example.com/logout'];
  public readonly userPool: cognito.UserPool;
  public readonly userPoolClient: cognito.UserPoolClient;
    public readonly userPoolDomain: cognito.UserPoolDomain;

  constructor(scope: cdk.App, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    this.userPool = new cognito.UserPool(this, 'RemoteMobilityMonitoringUserPool', {
      userPoolName: 'RemoteMobilityMonitoringUserPool',
      selfSignUpEnabled: false,
      signInCaseSensitive: true
    });

    this.userPoolClient = this.userPool.addClient('app-client', {
      oAuth: {
        flows: {
            authorizationCodeGrant: true,
            implicitCodeGrant: true
        },
        scopes: [cognito.OAuthScope.OPENID, cognito.OAuthScope.EMAIL, cognito.OAuthScope.PROFILE, cognito.OAuthScope.PHONE],
        callbackUrls: CognitoStack.CALLBACK_URLS,
        logoutUrls: CognitoStack.LOGOUT_URLS
      }
    });

    this.userPoolDomain = this.userPool.addDomain('app-domain', {
      cognitoDomain: {
        domainPrefix: 'mobimon'
      }
    });
  }
}
