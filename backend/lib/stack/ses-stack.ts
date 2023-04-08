import * as cdk from 'aws-cdk-lib';
import * as ses from 'aws-cdk-lib/aws-ses';

export class SesStack extends cdk.Stack {
  public readonly senderEmailIdentity: ses.EmailIdentity;

  constructor(scope: cdk.App, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    const sesName = 'RemoteMobilityMonitoringSes';
    this.senderEmailIdentity = new ses.EmailIdentity(this, sesName, {
      identity: ses.Identity.email('mobimonemail@gmail.com'),
    });
  }
}
