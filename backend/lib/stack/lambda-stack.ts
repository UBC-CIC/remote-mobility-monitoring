import * as cdk from 'aws-cdk-lib';
import * as lambda from 'aws-cdk-lib/aws-lambda';
import { DynamoDbStack } from "./dynamodb-stack";

export class LambdaStack extends cdk.Stack {
  private static codeAssetPath = './assets/function.jar';
  private static handlerPathPrefix = 'com.cpen491.remote_mobility_monitoring.function.handler.';

  public readonly exampleFunction: lambda.Function;
  public readonly createPatientFunction: lambda.Function;

  constructor(scope: cdk.App, id: string, dynamoDbStack: DynamoDbStack, props?: cdk.StackProps) {
    super(scope, id, props);

    // TODO: make roles for lambdas
    // TODO: DLQs? Layers?

    this.exampleFunction = new lambda.Function(this, 'ExampleFunction', {
      functionName: 'ExampleFunction',
      runtime: lambda.Runtime.JAVA_11,
      handler: LambdaStack.handlerPathPrefix + 'ExampleHandler',
      code: lambda.Code.fromAsset(LambdaStack.codeAssetPath),
    });

    this.createPatientFunction = this.createCreatePatientFunction(dynamoDbStack);
  }

  private createCreatePatientFunction(dynamoDbStack: DynamoDbStack): lambda.Function {
    const lambdaFunction = new lambda.Function(this, 'CreatePatientFunction', {
      functionName: 'CreatePatientFunction',
      runtime: lambda.Runtime.JAVA_11,
      handler: LambdaStack.handlerPathPrefix + 'CreatePatientHandler',
      code: lambda.Code.fromAsset(LambdaStack.codeAssetPath),
      // TODO: refactor to static variable
      timeout: cdk.Duration.seconds(300),
      memorySize: 512,
    })
    dynamoDbStack.patientTable.grantReadWriteData(lambdaFunction);
    dynamoDbStack.caregiverTable.grantReadWriteData(lambdaFunction);
    dynamoDbStack.organizationTable.grantReadData(lambdaFunction);
    return lambdaFunction;
  }
}
