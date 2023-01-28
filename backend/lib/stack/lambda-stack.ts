import * as cdk from 'aws-cdk-lib';
import * as dynamodb from 'aws-cdk-lib/aws-dynamodb';
import * as lambda from 'aws-cdk-lib/aws-lambda';

interface LambdaStackProps extends cdk.StackProps {
  readonly table: dynamodb.Table;
}

export class LambdaStack extends cdk.Stack {
  private static codeAssetPath = './assets/function.jar';
  private static handlerPathPrefix = 'com.cpen491.remote_mobility_monitoring.function.handler.';

  public readonly defaultFunction: lambda.Function;
  public readonly createCaregiverFunction: lambda.Function;
  public readonly createPatientFunction: lambda.Function;
  public readonly updatePatientDeviceFunction: lambda.Function;
  public readonly verifyPatientFunction: lambda.Function;

  constructor(scope: cdk.App, id: string, props: LambdaStackProps) {
    super(scope, id, props);

    // TODO: make roles for lambdas
    // TODO: DLQs? Layers?

    this.defaultFunction = new lambda.Function(this, 'DefaultFunction', {
      functionName: 'DefaultFunction',
      runtime: lambda.Runtime.NODEJS_16_X,
      code: lambda.Code.fromInline(`
      exports.handler = async (event) => {
        console.log('event: ', event)
      };
      `),
      handler: 'index.handler',
    });
    this.createCaregiverFunction = this.createCreateCaregiverFunction(props.table);
    this.createPatientFunction = this.createCreatePatientFunction(props.table);
    this.updatePatientDeviceFunction = this.createUpdatePatientDeviceFunction(props.table);
    this.verifyPatientFunction = this.createVerifyPatientFunction(props.table);
  }

  private createCreateCaregiverFunction(table: dynamodb.Table): lambda.Function {
    const lambdaFunction = new lambda.Function(this, 'CreateCaregiverFunction', {
      functionName: 'CreateCaregiverFunction',
      runtime: lambda.Runtime.JAVA_11,
      handler: LambdaStack.handlerPathPrefix + 'caregiver.CreateCaregiverHandler',
      code: lambda.Code.fromAsset(LambdaStack.codeAssetPath),
      // TODO: refactor to static variable
      timeout: cdk.Duration.seconds(300),
      memorySize: 512,
    });

    table.grantReadWriteData(lambdaFunction);
    return lambdaFunction;
  }

  private createCreatePatientFunction(table: dynamodb.Table): lambda.Function {
    const lambdaFunction = new lambda.Function(this, 'CreatePatientFunction', {
      functionName: 'CreatePatientFunction',
      runtime: lambda.Runtime.JAVA_11,
      handler: LambdaStack.handlerPathPrefix + 'patient.CreatePatientHandler',
      code: lambda.Code.fromAsset(LambdaStack.codeAssetPath),
      // TODO: refactor to static variable
      timeout: cdk.Duration.seconds(300),
      memorySize: 512,
    });

    // (lambdaFunction.node.defaultChild as lambda.CfnFunction).addPropertyOverride('SnapStart', {
    //   ApplyOn: 'PublishedVersions',
    // });
    // new lambda.Version(this, 'MyVersion', {
    //   lambda: lambdaFunction,
    // });

    table.grantReadWriteData(lambdaFunction);
    return lambdaFunction;
  }

  private createUpdatePatientDeviceFunction(table: dynamodb.Table): lambda.Function {
    const lambdaFunction = new lambda.Function(this, 'UpdatePatientDeviceFunction', {
      functionName: 'UpdatePatientDeviceFunction',
      runtime: lambda.Runtime.JAVA_11,
      handler: LambdaStack.handlerPathPrefix + 'patient.UpdatePatientDeviceHandler',
      code: lambda.Code.fromAsset(LambdaStack.codeAssetPath),
      // TODO: refactor to static variable
      timeout: cdk.Duration.seconds(300),
      memorySize: 512,
    });

    table.grantReadWriteData(lambdaFunction);
    return lambdaFunction;
  }

  private createVerifyPatientFunction(table: dynamodb.Table): lambda.Function {
    const lambdaFunction = new lambda.Function(this, 'VerifyPatientFunction', {
      functionName: 'VerifyPatientFunction',
      runtime: lambda.Runtime.JAVA_11,
      handler: LambdaStack.handlerPathPrefix + 'patient.VerifyPatientHandler',
      code: lambda.Code.fromAsset(LambdaStack.codeAssetPath),
      // TODO: refactor to static variable
      timeout: cdk.Duration.seconds(300),
      memorySize: 512,
    });

    table.grantReadWriteData(lambdaFunction);
    return lambdaFunction;
  }
}
