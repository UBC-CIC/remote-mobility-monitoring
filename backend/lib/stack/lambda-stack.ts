import * as cdk from 'aws-cdk-lib';
import * as dynamodb from 'aws-cdk-lib/aws-dynamodb';
import * as lambda from 'aws-cdk-lib/aws-lambda';
import * as iam from 'aws-cdk-lib/aws-iam';

interface LambdaStackProps extends cdk.StackProps {
  readonly table: dynamodb.Table;
}

export class LambdaStack extends cdk.Stack {
  private static codeAssetPath = './assets/function.jar';
  private static handlerPathPrefix = 'com.cpen491.remote_mobility_monitoring.function.handler.';

  public readonly lambdaRole: iam.Role
  public readonly defaultFunction: lambda.Function;
  public readonly createCaregiverFunction: lambda.Function;
  public readonly createPatientFunction: lambda.Function;
  public readonly updatePatientDeviceFunction: lambda.Function;
  public readonly verifyPatientFunction: lambda.Function;

  constructor(scope: cdk.App, id: string, props: LambdaStackProps) {
    super(scope, id, props);

    // TODO: make roles for lambdas
    this.lambdaRole = new iam.Role(this, 'RemoteMobilityMonitoringLambdaRole', {
      roleName: 'RemoteMobilityMonitoringLambdaRole',
      assumedBy: new iam.ServicePrincipal('lambda.amazonaws.com'),
    });
    this.lambdaRole.addToPolicy(new iam.PolicyStatement({
      effect: iam.Effect.ALLOW,
      actions: [
        'dynamodb:Scan',
        'dynamodb:GetItem',
        'dynamodb:PutItem',
        'dynamodb:Query',
        'dynamodb:UpdateItem',
        'dynamodb:DeleteItem',
        'dynamodb:BatchWriteItem',
        'dynamodb:BatchGetItem',
      ],
      resources: [
        props.table.tableArn,
        props.table.tableArn + '/index/*',
      ],
    }));
    this.lambdaRole.addToPolicy(new iam.PolicyStatement({
      effect: iam.Effect.ALLOW,
      actions: ['logs:*'],
      resources: ['*'],
    }));

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
    this.createCaregiverFunction = this.createCreateCaregiverFunction();
    this.createPatientFunction = this.createCreatePatientFunction();
    this.updatePatientDeviceFunction = this.createUpdatePatientDeviceFunction();
    this.verifyPatientFunction = this.createVerifyPatientFunction();
  }

  private createCreateCaregiverFunction(): lambda.Function {
    const lambdaFunction = new lambda.Function(this, 'CreateCaregiverFunction', {
      functionName: 'CreateCaregiverFunction',
      runtime: lambda.Runtime.JAVA_11,
      handler: LambdaStack.handlerPathPrefix + 'caregiver.CreateCaregiverHandler',
      code: lambda.Code.fromAsset(LambdaStack.codeAssetPath),
      // TODO: refactor to static variable
      timeout: cdk.Duration.seconds(300),
      memorySize: 512,
      role: this.lambdaRole,
    });

    return lambdaFunction;
  }

  private createCreatePatientFunction(): lambda.Function {
    const lambdaFunction = new lambda.Function(this, 'CreatePatientFunction', {
      functionName: 'CreatePatientFunction',
      runtime: lambda.Runtime.JAVA_11,
      handler: LambdaStack.handlerPathPrefix + 'patient.CreatePatientHandler',
      code: lambda.Code.fromAsset(LambdaStack.codeAssetPath),
      // TODO: refactor to static variable
      timeout: cdk.Duration.seconds(300),
      memorySize: 512,
      role: this.lambdaRole,
    });

    // (lambdaFunction.node.defaultChild as lambda.CfnFunction).addPropertyOverride('SnapStart', {
    //   ApplyOn: 'PublishedVersions',
    // });
    // new lambda.Version(this, 'MyVersion', {
    //   lambda: lambdaFunction,
    // });

    return lambdaFunction;
  }

  private createUpdatePatientDeviceFunction(): lambda.Function {
    const lambdaFunction = new lambda.Function(this, 'UpdatePatientDeviceFunction', {
      functionName: 'UpdatePatientDeviceFunction',
      runtime: lambda.Runtime.JAVA_11,
      handler: LambdaStack.handlerPathPrefix + 'patient.UpdatePatientDeviceHandler',
      code: lambda.Code.fromAsset(LambdaStack.codeAssetPath),
      // TODO: refactor to static variable
      timeout: cdk.Duration.seconds(300),
      memorySize: 512,
      role: this.lambdaRole,
    });

    return lambdaFunction;
  }

  private createVerifyPatientFunction(): lambda.Function {
    const lambdaFunction = new lambda.Function(this, 'VerifyPatientFunction', {
      functionName: 'VerifyPatientFunction',
      runtime: lambda.Runtime.JAVA_11,
      handler: LambdaStack.handlerPathPrefix + 'patient.VerifyPatientHandler',
      code: lambda.Code.fromAsset(LambdaStack.codeAssetPath),
      // TODO: refactor to static variable
      timeout: cdk.Duration.seconds(300),
      memorySize: 512,
      role: this.lambdaRole,
    });

    return lambdaFunction;
  }
}
