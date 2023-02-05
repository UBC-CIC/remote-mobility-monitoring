import * as cdk from 'aws-cdk-lib';
import * as dynamodb from 'aws-cdk-lib/aws-dynamodb';
import * as lambda from 'aws-cdk-lib/aws-lambda';
import * as iam from 'aws-cdk-lib/aws-iam';
import * as cognito from 'aws-cdk-lib/aws-cognito';
import { formResourceName } from "../utility";

interface LambdaStackProps extends cdk.StackProps {
  readonly stage: string;
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
  public readonly dynamoDbTableName: string;
  public readonly defaultFunction: lambda.Function;
  public readonly createOrganizationFunction: lambda.Function;
  public readonly createOrganizationAlias: lambda.Alias;
  public readonly getOrganizationFunction: lambda.Function;
  public readonly getOrganizationAlias: lambda.Alias;
  public readonly createAdminFunction: lambda.Function;
  public readonly createAdminAlias: lambda.Alias;
  public readonly getAdminFunction: lambda.Function;
  public readonly getAdminAlias: lambda.Alias;
  public readonly createCaregiverFunction: lambda.Function;
  public readonly createCaregiverAlias: lambda.Alias;
  public readonly addPatientFunction: lambda.Function;
  public readonly addPatientAlias: lambda.Alias;
  public readonly removePatientFunction: lambda.Function;
  public readonly removePatientAlias: lambda.Alias;
  public readonly getCaregiverFunction: lambda.Function;
  public readonly getCaregiverAlias: lambda.Alias;
  public readonly getAllPatientsFunction: lambda.Function;
  public readonly getAllPatientsAlias: lambda.Alias;
  public readonly updateCaregiverFunction: lambda.Function;
  public readonly updateCaregiverAlias: lambda.Alias;
  public readonly deleteCaregiverFunction: lambda.Function;
  public readonly deleteCaregiverAlias: lambda.Alias;
  public readonly createPatientFunction: lambda.Function;
  public readonly createPatientAlias: lambda.Alias;
  public readonly updatePatientDeviceFunction: lambda.Function;
  public readonly updatePatientDeviceAlias: lambda.Alias;
  public readonly verifyPatientFunction: lambda.Function;
  public readonly verifyPatientAlias: lambda.Alias;
  public readonly getPatientFunction: lambda.Function;
  public readonly getPatientAlias: lambda.Alias;
  public readonly getAllCaregiversFunction: lambda.Function;
  public readonly getAllCaregiversAlias: lambda.Alias;
  public readonly updatePatientFunction: lambda.Function;
  public readonly updatePatientAlias: lambda.Alias;
  public readonly deletePatientFunction: lambda.Function;
  public readonly deletePatientAlias: lambda.Alias;

  constructor(scope: cdk.App, id: string, props: LambdaStackProps) {
    super(scope, id, props);

    const roleName = `RemoteMobilityMonitoringLambdaRole-${props.stage}`
    this.lambdaRole = this.createLambdaRole(roleName, props.table);
    this.dynamoDbTableName = props.table.tableName;
    this.userPool = props.userPool;

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

    const createOrganizationFunctionName = formResourceName('CreateOrganizationFunction', props.stage);
    this.createOrganizationFunction = this.createCreateOrganizationFunction(createOrganizationFunctionName);
    this.createOrganizationAlias = this.createLambdaAlias(createOrganizationFunctionName, this.createOrganizationFunction);
    const getOrganizationFunctionName = formResourceName('GetOrganizationFunction', props.stage);
    this.getOrganizationFunction = this.createGetOrganizationFunction(getOrganizationFunctionName);
    this.getOrganizationAlias = this.createLambdaAlias(getOrganizationFunctionName, this.getOrganizationFunction);

    const createAdminFunctionName = formResourceName('CreateAdminFunction', props.stage);
    this.createAdminFunction = this.createCreateAdminFunction(createAdminFunctionName);
    this.createAdminAlias = this.createLambdaAlias(createAdminFunctionName, this.createAdminFunction);
    const getAdminFunctionName = formResourceName('GetAdminFunction', props.stage);
    this.getAdminFunction = this.createGetAdminFunction(getAdminFunctionName);
    this.getAdminAlias = this.createLambdaAlias(getAdminFunctionName, this.getAdminFunction);

    const createCaregiverFunctionName = formResourceName('CreateCaregiverFunction', props.stage);
    this.createCaregiverFunction = this.createCreateCaregiverFunction(createCaregiverFunctionName);
    this.createCaregiverAlias = this.createLambdaAlias(createCaregiverFunctionName, this.createCaregiverFunction);
    const addPatientFunctionName = formResourceName('AddPatientFunction', props.stage);
    this.addPatientFunction = this.createAddPatientFunction(addPatientFunctionName);
    this.addPatientAlias = this.createLambdaAlias(addPatientFunctionName, this.addPatientFunction);
    const removePatientFunctionName = formResourceName('RemovePatientFunction', props.stage);
    this.removePatientFunction = this.createRemovePatientFunction(removePatientFunctionName);
    this.removePatientAlias = this.createLambdaAlias(removePatientFunctionName, this.removePatientFunction);
    const getCaregiverFunctionName = formResourceName('GetCaregiverFunction', props.stage);
    this.getCaregiverFunction = this.createGetCaregiverFunction(getCaregiverFunctionName);
    this.getCaregiverAlias = this.createLambdaAlias(getCaregiverFunctionName, this.getCaregiverFunction);
    const getAllPatientsFunctionName = formResourceName('GetAllPatientsFunction', props.stage);
    this.getAllPatientsFunction = this.createGetAllPatientsFunction(getAllPatientsFunctionName);
    this.getAllPatientsAlias = this.createLambdaAlias(getAllPatientsFunctionName, this.getAllPatientsFunction);
    const updateCaregiverFunctionName = formResourceName('UpdateCaregiverFunction', props.stage);
    this.updateCaregiverFunction = this.createUpdateCaregiverFunction(updateCaregiverFunctionName);
    this.updateCaregiverAlias = this.createLambdaAlias(updateCaregiverFunctionName, this.updateCaregiverFunction);
    const deleteCaregiverFunctionName = formResourceName('DeleteCaregiverFunction', props.stage);
    this.deleteCaregiverFunction = this.createDeleteCaregiverFunction(deleteCaregiverFunctionName);
    this.deleteCaregiverAlias = this.createLambdaAlias(deleteCaregiverFunctionName, this.deleteCaregiverFunction);

    const createPatientFunctionName = formResourceName('CreatePatientFunction', props.stage);
    this.createPatientFunction = this.createCreatePatientFunction(createPatientFunctionName);
    this.createPatientAlias = this.createLambdaAlias(createPatientFunctionName, this.createPatientFunction);
    const updatePatientDeviceFunctionName = formResourceName('UpdatePatientDeviceFunction', props.stage);
    this.updatePatientDeviceFunction = this.createUpdatePatientDeviceFunction(updatePatientDeviceFunctionName);
    this.updatePatientDeviceAlias = this.createLambdaAlias(updatePatientDeviceFunctionName, this.updatePatientDeviceFunction);
    const verifyPatientFunctionName = formResourceName('VerifyPatientFunction', props.stage);
    this.verifyPatientFunction = this.createVerifyPatientFunction(verifyPatientFunctionName);
    this.verifyPatientAlias = this.createLambdaAlias(verifyPatientFunctionName, this.verifyPatientFunction);
    const getPatientFunctionName = formResourceName('GetPatientFunction', props.stage);
    this.getPatientFunction = this.createGetPatientFunction(getPatientFunctionName);
    this.getPatientAlias = this.createLambdaAlias(getPatientFunctionName, this.getPatientFunction);
    const getAllCaregiversFunctionName = formResourceName('GetAllCaregiversFunction', props.stage);
    this.getAllCaregiversFunction = this.createGetAllCaregiversFunction(getAllCaregiversFunctionName);
    this.getAllCaregiversAlias = this.createLambdaAlias(getAllCaregiversFunctionName, this.getAllCaregiversFunction);
    const updatePatientFunctionName = formResourceName('UpdatePatientFunction', props.stage);
    this.updatePatientFunction = this.createUpdatePatientFunction(updatePatientFunctionName);
    this.updatePatientAlias = this.createLambdaAlias(updatePatientFunctionName, this.updatePatientFunction);
    const deletePatientFunctionName = formResourceName('DeletePatientFunction', props.stage);
    this.deletePatientFunction = this.createDeletePatientFunction(deletePatientFunctionName);
    this.deletePatientAlias = this.createLambdaAlias(deletePatientFunctionName, this.deletePatientFunction);
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

    // Adds Cognito policies to the lambda role
    role.addManagedPolicy(
        iam.ManagedPolicy.fromAwsManagedPolicyName('AmazonCognitoPowerUser')
    )
    role.addManagedPolicy(
        iam.ManagedPolicy.fromAwsManagedPolicyName('AmazonCognitoDeveloperAuthenticatedIdentities')
    )

    return role;
  }

  private createCreateOrganizationFunction(functionName: string): lambda.Function {
    return this.createLambdaFunction(functionName, 'organization.CreateOrganizationHandler');
  }

  private createGetOrganizationFunction(functionName: string): lambda.Function {
    return this.createLambdaFunction(functionName, 'organization.GetOrganizationHandler');
  }

  private createCreateAdminFunction(functionName: string): lambda.Function {
    return this.createLambdaFunction(functionName, 'admin.CreateAdminHandler');
  }

  private createGetAdminFunction(functionName: string): lambda.Function {
    return this.createLambdaFunction(functionName, 'admin.GetAdminHandler');
  }

  private createCreateCaregiverFunction(functionName: string): lambda.Function {
    return this.createLambdaFunction(functionName, 'caregiver.CreateCaregiverHandler');
  }

  private createAddPatientFunction(functionName: string): lambda.Function {
    return this.createLambdaFunction(functionName, 'caregiver.AddPatientHandler');
  }

  private createRemovePatientFunction(functionName: string): lambda.Function {
    return this.createLambdaFunction(functionName, 'caregiver.RemovePatientHandler');
  }

  private createGetCaregiverFunction(functionName: string): lambda.Function {
    return this.createLambdaFunction(functionName, 'caregiver.GetCaregiverHandler');
  }

  private createGetAllPatientsFunction(functionName: string): lambda.Function {
    return this.createLambdaFunction(functionName, 'caregiver.GetAllPatientsHandler');
  }

  private createUpdateCaregiverFunction(functionName: string): lambda.Function {
    return this.createLambdaFunction(functionName, 'caregiver.UpdateCaregiverHandler');
  }

  private createDeleteCaregiverFunction(functionName: string): lambda.Function {
    return this.createLambdaFunction(functionName, 'caregiver.DeleteCaregiverHandler');
  }

  private createCreatePatientFunction(functionName: string): lambda.Function {
    return this.createLambdaFunction(functionName, 'patient.CreatePatientHandler');
  }

  private createUpdatePatientDeviceFunction(functionName: string): lambda.Function {
    return this.createLambdaFunction(functionName, 'patient.UpdatePatientDeviceHandler');
  }

  private createVerifyPatientFunction(functionName: string): lambda.Function {
    return this.createLambdaFunction(functionName, 'patient.VerifyPatientHandler');
  }

  private createGetPatientFunction(functionName: string): lambda.Function {
    return this.createLambdaFunction(functionName, 'patient.GetPatientHandler');
  }

  private createGetAllCaregiversFunction(functionName: string): lambda.Function {
    return this.createLambdaFunction(functionName, 'patient.GetAllCaregiversHandler');
  }

  private createUpdatePatientFunction(functionName: string): lambda.Function {
    return this.createLambdaFunction(functionName, 'patient.UpdatePatientHandler');
  }

  private createDeletePatientFunction(functionName: string): lambda.Function {
    return this.createLambdaFunction(functionName, 'patient.DeletePatientHandler');
  }

  private createLambdaFunction(functionName: string, handler: string): lambda.Function {
    const lambdaFunction = new lambda.Function(this, functionName, {
      functionName: functionName,
      runtime: LambdaStack.runtime,
      handler: LambdaStack.handlerPathPrefix + handler,
      code: lambda.Code.fromAsset(LambdaStack.codeAssetPath),
      timeout: LambdaStack.timeout,
      memorySize: LambdaStack.memorySize,
      role: this.lambdaRole,
      environment: {
        'DYNAMO_DB_TABLE_NAME': this.dynamoDbTableName,
        'COGNITO_USERPOOL_ID': this.userPool.userPoolId,
        'COGNITO_USERPOOL_ARN': this.userPool.userPoolArn,
      },
    });
    LambdaStack.enableSnapStart(lambdaFunction);
    return lambdaFunction;
  }

  private static enableSnapStart(lambdaFunction: lambda.Function) {
    (lambdaFunction.node.defaultChild as lambda.CfnFunction).snapStart = {
      applyOn: 'PublishedVersions'
    };
  }

  private createLambdaAlias(functionName: string, lambdaFunction: lambda.Function): lambda.Alias {
    const aliasName = `${functionName}Alias`;
    return new lambda.Alias(this, aliasName, {
      aliasName: aliasName,
      version: lambdaFunction.currentVersion,
    });
  }
}
