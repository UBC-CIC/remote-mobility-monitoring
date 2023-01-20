import * as cdk from 'aws-cdk-lib';
import * as lambda from 'aws-cdk-lib/aws-lambda';

export class LambdaStack extends cdk.Stack {
  public readonly exampleFunction: lambda.Function;

  constructor(scope: cdk.App, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    this.exampleFunction = new lambda.Function(this, 'ExampleFunction', {
      functionName: 'ExampleFunction',
      runtime: lambda.Runtime.JAVA_11,
      handler: 'com.cpen491.remote_mobility_monitoring.function.handler.ExampleHandler',
      code: lambda.Code.fromAsset('./assets/function.jar'),
    });
  }
}
