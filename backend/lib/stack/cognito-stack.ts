import * as cdk from 'aws-cdk-lib';
import * as cognito from 'aws-cdk-lib/aws-cognito';
import { formResourceName } from "../utility";

interface CognitoStackProps extends cdk.StackProps {
  readonly stage: string;
}

export class CognitoStack extends cdk.Stack {
  private static CALLBACK_URLS = ['https://example.com/callback'];
  private static LOGOUT_URLS = ['https://example.com/logout'];
  private static DOMAIN_PREFIX = process.env.COGNITO_DOMAIN_PREFIX || 'mobimon'; // Put domain in env when deploying, unless deploying to CIC
  public readonly userPool: cognito.UserPool;
  public readonly userPoolClient: cognito.UserPoolClient;
  public readonly userPoolDomain: cognito.UserPoolDomain;
  public readonly userPoolAdminGroup: cognito.CfnUserPoolGroup;

  constructor(scope: cdk.App, id: string, props: CognitoStackProps) {
    super(scope, id, props);

    const userPoolName = formResourceName('RemoteMobilityMonitoringUserPool', props.stage)
    this.userPool = new cognito.UserPool(this, userPoolName, {
      userPoolName: userPoolName,
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

    const userPoolDomainName = props.stage === 'prod' ? CognitoStack.DOMAIN_PREFIX : formResourceName(CognitoStack.DOMAIN_PREFIX, props.stage);
    this.userPoolDomain = this.userPool.addDomain('app-domain', {
      cognitoDomain: {
        domainPrefix: userPoolDomainName
      }
    });

    this.userPoolAdminGroup = new cognito.CfnUserPoolGroup(this, 'RemoteMobilityMonitoringUserPoolAdminGroup', {
      userPoolId: this.userPool.userPoolId,
      groupName: 'Admin',
      description: 'Admin group for Remote Mobility Monitoring',
    });
  }
}
