import * as cdk from 'aws-cdk-lib';
import * as dynamodb from 'aws-cdk-lib/aws-dynamodb';
import * as lambda from 'aws-cdk-lib/aws-lambda';
import * as iam from 'aws-cdk-lib/aws-iam';
import { formResourceName } from "../utility";

interface LambdaStackProps extends cdk.StackProps {
  readonly stage: string;
  readonly table: dynamodb.Table;
}

export class LambdaStack extends cdk.Stack {
  private static runtime = lambda.Runtime.JAVA_11;
  private static codeAssetPath = './assets/function.jar';
  private static handlerPathPrefix = 'com.cpen491.remote_mobility_monitoring.function.handler.';
  private static timeout = cdk.Duration.seconds(300);
  private static memorySize = 512;

  public readonly lambdaRole: iam.Role
  public readonly dynamoDbTableName: string;
  public readonly defaultFunction: lambda.Function;
  public readonly createOrganizationFunction: lambda.Function;
  public readonly getOrganizationFunction: lambda.Function;
  public readonly createAdminFunction: lambda.Function;
  public readonly getAdminFunction: lambda.Function;
  public readonly createCaregiverFunction: lambda.Function;
  public readonly addPatientFunction: lambda.Function;
  public readonly removePatientFunction: lambda.Function;
  public readonly getCaregiverFunction: lambda.Function;
  public readonly getAllPatientsFunction: lambda.Function;
  public readonly updateCaregiverFunction: lambda.Function;
  public readonly deleteCaregiverFunction: lambda.Function;
  public readonly createPatientFunction: lambda.Function;
  public readonly updatePatientDeviceFunction: lambda.Function;
  public readonly verifyPatientFunction: lambda.Function;
  public readonly getPatientFunction: lambda.Function;
  public readonly getAllCaregiversFunction: lambda.Function;
  public readonly updatePatientFunction: lambda.Function;
  public readonly deletePatientFunction: lambda.Function;

  constructor(scope: cdk.App, id: string, props: LambdaStackProps) {
    super(scope, id, props);

    const roleName = `RemoteMobilityMonitoringLambdaRole-${props.stage}`
    this.lambdaRole = this.createLambdaRole(roleName, props.table);
    this.dynamoDbTableName = props.table.tableName;

    // TODO: DLQs? Layers?

    const defaultFunctionName = `DefaultFunction-${props.stage}`;
    this.defaultFunction = new lambda.Function(this, defaultFunctionName, {
      functionName: defaultFunctionName,
      runtime: lambda.Runtime.NODEJS_16_X,
      code: lambda.Code.fromInline(`
      exports.handler = async (event) => {
        console.log('event: ', event)
      };
      `),
      handler: 'index.handler',
    });

    this.createOrganizationFunction = this.createCreateOrganizationFunction(formResourceName('CreateOrganizationFunction', props.stage));
    this.getOrganizationFunction = this.createGetOrganizationFunction(formResourceName('GetOrganizationFunction', props.stage));

    this.createAdminFunction = this.createCreateAdminFunction(formResourceName('CreateAdminFunction', props.stage));
    this.getAdminFunction = this.createGetAdminFunction(formResourceName('GetAdminFunction', props.stage));

    this.createCaregiverFunction = this.createCreateCaregiverFunction(formResourceName('CreateCaregiverFunction', props.stage));
    this.addPatientFunction = this.createAddPatientFunction(formResourceName('AddPatientFunction', props.stage));
    this.removePatientFunction = this.createRemovePatientFunction(formResourceName('RemovePatientFunction', props.stage));
    this.getCaregiverFunction = this.createGetCaregiverFunction(formResourceName('GetCaregiverFunction', props.stage));
    this.getAllPatientsFunction = this.createGetAllPatientsFunction(formResourceName('GetAllPatientsFunction', props.stage));
    this.updateCaregiverFunction = this.createUpdateCaregiverFunction(formResourceName('UpdateCaregiverFunction', props.stage));
    this.deleteCaregiverFunction = this.createDeleteCaregiverFunction(formResourceName('DeleteCaregiverFunction', props.stage));

    this.createPatientFunction = this.createCreatePatientFunction(formResourceName('CreatePatientFunction', props.stage));
    this.updatePatientDeviceFunction = this.createUpdatePatientDeviceFunction(formResourceName('UpdatePatientDeviceFunction', props.stage));
    this.verifyPatientFunction = this.createVerifyPatientFunction(formResourceName('VerifyPatientFunction', props.stage));
    this.getPatientFunction = this.createGetPatientFunction(formResourceName('GetPatientFunction', props.stage));
    this.getAllCaregiversFunction = this.createGetAllCaregiversFunction(formResourceName('GetAllCaregiversFunction', props.stage));
    this.updatePatientFunction = this.createUpdatePatientFunction(formResourceName('UpdatePatientFunction', props.stage));
    this.deletePatientFunction = this.createDeletePatientFunction(formResourceName('DeletePatientFunction', props.stage));
  }

  private createLambdaRole(roleName: string, table: dynamodb.Table): iam.Role {
    const role = new iam.Role(this, roleName, {
      roleName: roleName,
      assumedBy: new iam.ServicePrincipal('lambda.amazonaws.com'),
    });
    role.addToPolicy(new iam.PolicyStatement({
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
        table.tableArn,
        table.tableArn + '/index/*',
      ],
    }));
    role.addToPolicy(new iam.PolicyStatement({
      effect: iam.Effect.ALLOW,
      actions: ['logs:*'],
      resources: ['*'],
    }));

    return role;
  }

  private createCreateOrganizationFunction(functionName: string): lambda.Function {
    const lambdaFunction = new lambda.Function(
      this,
      functionName,
      this.createLambdaFunctionProps(functionName, 'organization.CreateOrganizationHandler'),
    )

    return lambdaFunction;
  }

  private createGetOrganizationFunction(functionName: string): lambda.Function {
    const lambdaFunction = new lambda.Function(
      this,
      functionName,
      this.createLambdaFunctionProps(functionName, 'organization.GetOrganizationHandler'),
    )

    return lambdaFunction;
  }

  private createCreateAdminFunction(functionName: string): lambda.Function {
    const lambdaFunction = new lambda.Function(
      this,
      functionName,
      this.createLambdaFunctionProps(functionName, 'admin.CreateAdminHandler'),
    )

    return lambdaFunction;
  }

  private createGetAdminFunction(functionName: string): lambda.Function {
    const lambdaFunction = new lambda.Function(
      this,
      functionName,
      this.createLambdaFunctionProps(functionName, 'admin.GetAdminHandler'),
    )

    return lambdaFunction;
  }

  private createCreateCaregiverFunction(functionName: string): lambda.Function {
    const lambdaFunction = new lambda.Function(
      this,
      functionName,
      this.createLambdaFunctionProps(functionName, 'caregiver.CreateCaregiverHandler'),
    );

    return lambdaFunction;
  }

  private createAddPatientFunction(functionName: string): lambda.Function {
    const lambdaFunction = new lambda.Function(
      this,
      functionName,
      this.createLambdaFunctionProps(functionName, 'caregiver.AddPatientHandler'),
    )

    return lambdaFunction;
  }

  private createRemovePatientFunction(functionName: string): lambda.Function {
    const lambdaFunction = new lambda.Function(
      this,
      functionName,
      this.createLambdaFunctionProps(functionName, 'caregiver.RemovePatientHandler'),
    )

    return lambdaFunction;
  }

  private createGetCaregiverFunction(functionName: string): lambda.Function {
    const lambdaFunction = new lambda.Function(
      this,
      functionName,
      this.createLambdaFunctionProps(functionName, 'caregiver.GetCaregiverHandler'),
    )

    return lambdaFunction;
  }

  private createGetAllPatientsFunction(functionName: string): lambda.Function {
    const lambdaFunction = new lambda.Function(
      this,
      functionName,
      this.createLambdaFunctionProps(functionName, 'caregiver.GetAllPatientsHandler'),
    )

    return lambdaFunction;
  }

  private createUpdateCaregiverFunction(functionName: string): lambda.Function {
    const lambdaFunction = new lambda.Function(
      this,
      functionName,
      this.createLambdaFunctionProps(functionName, 'caregiver.UpdateCaregiverHandler'),
    )

    return lambdaFunction;
  }

  private createDeleteCaregiverFunction(functionName: string): lambda.Function {
    const lambdaFunction = new lambda.Function(
      this,
      functionName,
      this.createLambdaFunctionProps(functionName, 'caregiver.DeleteCaregiverHandler'),
    );

    return lambdaFunction;
  }

  private createCreatePatientFunction(functionName: string): lambda.Function {
    const lambdaFunction = new lambda.Function(
      this,
      functionName,
      this.createLambdaFunctionProps(functionName, 'patient.CreatePatientHandler'),
    );

    // TODO: snap start
    // (lambdaFunction.node.defaultChild as lambda.CfnFunction).addPropertyOverride('SnapStart', {
    //   ApplyOn: 'PublishedVersions',
    // });
    // new lambda.Version(this, 'MyVersion', {
    //   lambda: lambdaFunction,
    // });

    return lambdaFunction;
  }

  private createUpdatePatientDeviceFunction(functionName: string): lambda.Function {
    const lambdaFunction = new lambda.Function(
      this,
      functionName,
      this.createLambdaFunctionProps(functionName, 'patient.UpdatePatientDeviceHandler'),
    );

    return lambdaFunction;
  }

  private createVerifyPatientFunction(functionName: string): lambda.Function {
    const lambdaFunction = new lambda.Function(
      this,
      functionName,
      this.createLambdaFunctionProps(functionName, 'patient.VerifyPatientHandler'),
    );

    return lambdaFunction;
  }

  private createGetPatientFunction(functionName: string): lambda.Function {
    const lambdaFunction = new lambda.Function(
      this,
      functionName,
      this.createLambdaFunctionProps(functionName, 'patient.GetPatientHandler'),
    )

    return lambdaFunction;
  }

  private createGetAllCaregiversFunction(functionName: string): lambda.Function {
    const lambdaFunction = new lambda.Function(
      this,
      functionName,
      this.createLambdaFunctionProps(functionName, 'patient.GetAllCaregiversHandler'),
    )

    return lambdaFunction
  }

  private createUpdatePatientFunction(functionName: string): lambda.Function {
    const lambdaFunction = new lambda.Function(
      this,
      functionName,
      this.createLambdaFunctionProps(functionName, 'patient.UpdatePatientHandler'),
    )

    return lambdaFunction;
  }

  private createDeletePatientFunction(functionName: string): lambda.Function {
    const lambdaFunction = new lambda.Function(
      this,
      functionName,
      this.createLambdaFunctionProps(functionName, 'patient.DeletePatientHandler'),
    )

    return lambdaFunction;
  }

  private createLambdaFunctionProps(name: string, handler: string): lambda.FunctionProps {
    return {
      functionName: name,
      runtime: LambdaStack.runtime,
      handler: LambdaStack.handlerPathPrefix + handler,
      code: lambda.Code.fromAsset(LambdaStack.codeAssetPath),
      timeout: LambdaStack.timeout,
      memorySize: LambdaStack.memorySize,
      role: this.lambdaRole,
      environment: {
        'DYNAMO_DB_TABLE_NAME': this.dynamoDbTableName,
      },
    };
  }
}
