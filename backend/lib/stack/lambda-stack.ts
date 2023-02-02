import * as cdk from 'aws-cdk-lib';
import * as dynamodb from 'aws-cdk-lib/aws-dynamodb';
import * as lambda from 'aws-cdk-lib/aws-lambda';
import * as iam from 'aws-cdk-lib/aws-iam';
import * as cognito from 'aws-cdk-lib/aws-cognito';

interface LambdaStackProps extends cdk.StackProps {
  readonly table: dynamodb.Table;
  readonly userPool: cognito.UserPool;
}

export class LambdaStack extends cdk.Stack {
  private static runtime = lambda.Runtime.JAVA_11;
  private static codeAssetPath = './assets/function.jar';
  private static handlerPathPrefix = 'com.cpen491.remote_mobility_monitoring.function.handler.';
  private static timeout = cdk.Duration.seconds(300);
  private static memorySize = 512;
  private readonly userPool: cognito.UserPool;

  public readonly lambdaRole: iam.Role
  public readonly defaultFunction: lambda.Function;
  public readonly getOrganizationFunction: lambda.Function;
  public readonly createCaregiverFunction: lambda.Function;
  public readonly addPatientFunction: lambda.Function;
  public readonly removePatientFunction: lambda.Function;
  public readonly getCaregiverFunction: lambda.Function;
  public readonly getAllPatientsFunction: lambda.Function;
  public readonly deleteCaregiverFunction: lambda.Function;
  public readonly createPatientFunction: lambda.Function;
  public readonly updatePatientDeviceFunction: lambda.Function;
  public readonly verifyPatientFunction: lambda.Function;
  public readonly getPatientFunction: lambda.Function;
  public readonly deletePatientFunction: lambda.Function;
  public readonly testFunction: lambda.Function;

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

    // Adds Cognito policies to the lambda role
    this.lambdaRole.addManagedPolicy(
        iam.ManagedPolicy.fromAwsManagedPolicyName('AmazonCognitoPowerUser')
    )
    this.lambdaRole.addManagedPolicy(
        iam.ManagedPolicy.fromAwsManagedPolicyName('AmazonCognitoDeveloperAuthenticatedIdentities')
    )

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

    this.userPool = props.userPool;

    this.getOrganizationFunction = this.createGetOrganizationFunction();

    this.createCaregiverFunction = this.createCreateCaregiverFunction();
    this.addPatientFunction = this.createAddPatientFunction();
    this.removePatientFunction = this.createRemovePatientFunction();
    this.getCaregiverFunction = this.createGetCaregiverFunction();
    this.getAllPatientsFunction = this.createGetAllPatientsFunction();
    this.deleteCaregiverFunction = this.createDeleteCaregiverFunction();

    this.createPatientFunction = this.createCreatePatientFunction();
    this.updatePatientDeviceFunction = this.createUpdatePatientDeviceFunction();
    this.verifyPatientFunction = this.createVerifyPatientFunction();
    this.getPatientFunction = this.createGetPatientFunction();
    this.deletePatientFunction = this.createDeletePatientFunction();
    this.testFunction = this.createTestFunction();
  }

  private createGetOrganizationFunction(): lambda.Function {
    const lambdaFunction = new lambda.Function(
      this,
      'GetOrganizationFunction',
      LambdaStack.createLambdaFunctionProps('GetOrganizationFunction', 'organization.GetOrganizationHandler', this.lambdaRole, this.userPool),
    )

    return lambdaFunction;
  }

  private createCreateCaregiverFunction(): lambda.Function {
    const lambdaFunction = new lambda.Function(
      this,
      'CreateCaregiverFunction',
      LambdaStack.createLambdaFunctionProps('CreateCaregiverFunction', 'caregiver.CreateCaregiverHandler', this.lambdaRole, this.userPool),
    );

    return lambdaFunction;
  }

  private createAddPatientFunction(): lambda.Function {
    const lambdaFunction = new lambda.Function(
      this,
      'AddPatientFunction',
      LambdaStack.createLambdaFunctionProps('AddPatientFunction', 'caregiver.AddPatientHandler', this.lambdaRole, this.userPool),
    )

    return lambdaFunction;
  }

  private createRemovePatientFunction(): lambda.Function {
    const lambdaFunction = new lambda.Function(
      this,
      'RemovePatientFunction',
      LambdaStack.createLambdaFunctionProps('RemovePatientFunction', 'caregiver.RemovePatientHandler', this.lambdaRole, this.userPool),
    )

    return lambdaFunction;
  }

  private createGetCaregiverFunction(): lambda.Function {
    const lambdaFunction = new lambda.Function(
      this,
      'GetCaregiverFunction',
      LambdaStack.createLambdaFunctionProps('GetCaregiverFunction', 'caregiver.GetCaregiverHandler', this.lambdaRole, this.userPool),
    )

    return lambdaFunction;
  }

  private createGetAllPatientsFunction(): lambda.Function {
    const lambdaFunction = new lambda.Function(
      this,
      'GetAllPatientsFunction',
      LambdaStack.createLambdaFunctionProps('GetAllPatientsFunction', 'caregiver.GetAllPatientsHandler', this.lambdaRole, this.userPool),
    )

    return lambdaFunction;
  }

  private createDeleteCaregiverFunction(): lambda.Function {
    const lambdaFunction = new lambda.Function(
      this,
      'DeleteCaregiverFunction',
      LambdaStack.createLambdaFunctionProps('DeleteCaregiverFunction', 'caregiver.DeleteCaregiverHandler', this.lambdaRole, this.userPool),
    );

    return lambdaFunction;
  }

  private createCreatePatientFunction(): lambda.Function {
    const lambdaFunction = new lambda.Function(
      this,
      'CreatePatientFunction',
      LambdaStack.createLambdaFunctionProps('CreatePatientFunction', 'patient.CreatePatientHandler', this.lambdaRole, this.userPool),
    );

    // (lambdaFunction.node.defaultChild as lambda.CfnFunction).addPropertyOverride('SnapStart', {
    //   ApplyOn: 'PublishedVersions',
    // });
    // new lambda.Version(this, 'MyVersion', {
    //   lambda: lambdaFunction,
    // });

    return lambdaFunction;
  }

  private createUpdatePatientDeviceFunction(): lambda.Function {
    const lambdaFunction = new lambda.Function(
      this,
      'UpdatePatientDeviceFunction',
      LambdaStack.createLambdaFunctionProps('UpdatePatientDeviceFunction', 'patient.UpdatePatientDeviceHandler', this.lambdaRole, this.userPool),
    );

    return lambdaFunction;
  }

  private createVerifyPatientFunction(): lambda.Function {
    const lambdaFunction = new lambda.Function(
      this,
      'VerifyPatientFunction',
      LambdaStack.createLambdaFunctionProps('VerifyPatientFunction', 'patient.VerifyPatientHandler', this.lambdaRole, this.userPool),
    );

    return lambdaFunction;
  }

  private createGetPatientFunction(): lambda.Function {
    const lambdaFunction = new lambda.Function(
      this,
      'GetPatientFunction',
      LambdaStack.createLambdaFunctionProps('GetPatientFunction', 'patient.GetPatientHandler', this.lambdaRole, this.userPool),
    )

    return lambdaFunction;
  }

  private createDeletePatientFunction(): lambda.Function {
    const lambdaFunction = new lambda.Function(
      this,
      'DeletePatientFunction',
      LambdaStack.createLambdaFunctionProps('DeletePatientFunction', 'patient.DeletePatientHandler', this.lambdaRole, this.userPool),
    )

    return lambdaFunction;
  }

  private createTestFunction(): lambda.Function {
    const lambdaFunction = new lambda.Function(
      this,
      'TestFunction',
      LambdaStack.createLambdaFunctionProps('TestFunction', 'test.TestHandler', this.lambdaRole, this.userPool),
    )

    return lambdaFunction;
  }

  private static createLambdaFunctionProps(name: string, handler: string, role: iam.Role, userPool: cognito.UserPool): lambda.FunctionProps {
    return {
      functionName: name,
      runtime: LambdaStack.runtime,
      handler: LambdaStack.handlerPathPrefix + handler,
      code: lambda.Code.fromAsset(LambdaStack.codeAssetPath),
      timeout: LambdaStack.timeout,
      memorySize: LambdaStack.memorySize,
      role: role,
      environment: {
        'COGNITO_USERPOOL_ID': userPool.userPoolId,
        'COGNITO_USERPOOL_ARN': userPool.userPoolArn,
      }
    };
  }
}
