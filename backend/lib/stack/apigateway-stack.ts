import * as cdk from 'aws-cdk-lib';
import * as lambda from 'aws-cdk-lib/aws-lambda';
import * as apigateway from 'aws-cdk-lib/aws-apigateway';

interface ApiGatewayStackProps extends cdk.StackProps {
  readonly defaultFunction: lambda.Function;
  readonly createCaregiverFunction: lambda.Function;
  readonly getCaregiverFunction: lambda.Function;
  readonly getAllPatientsFunction: lambda.Function;
  readonly deleteCaregiverFunction: lambda.Function;
  readonly createPatientFunction: lambda.Function;
  readonly updatePatientDeviceFunction: lambda.Function;
  readonly verifyPatientFunction: lambda.Function;
  readonly getPatientFunction: lambda.Function;
  readonly deletePatientFunction: lambda.Function;
}

export class ApiGatewayStack extends cdk.Stack {
  constructor(scope: cdk.App, id: string, props: ApiGatewayStackProps) {
    super(scope, id, props);

    const api = new apigateway.LambdaRestApi(this, 'RemoteMobilityMonitoringApi', {
      handler: props.defaultFunction,
      proxy: false,
    });
    const createCaregiverFunctionIntegration = ApiGatewayStack.createLambdaIntegration(props.createCaregiverFunction);
    const getCaregiverFunctionIntegration = ApiGatewayStack.createLambdaIntegration(props.getCaregiverFunction);
    const getAllPatientsFunctionIntegration = ApiGatewayStack.createLambdaIntegration(props.getAllPatientsFunction);
    const deleteCaregiverFunctionIntegration = ApiGatewayStack.createLambdaIntegration(props.deleteCaregiverFunction);
    const createPatientFunctionIntegration = ApiGatewayStack.createLambdaIntegration(props.createPatientFunction);
    const updatePatientDeviceFunctionIntegration = ApiGatewayStack.createLambdaIntegration(props.updatePatientDeviceFunction);
    const verifyPatientFunctionIntegration = ApiGatewayStack.createLambdaIntegration(props.verifyPatientFunction);
    const getPatientFunctionIntegration = ApiGatewayStack.createLambdaIntegration(props.getPatientFunction);
    const deletePatientFunctionIntegration = ApiGatewayStack.createLambdaIntegration(props.deletePatientFunction);

    const caregivers = api.root.addResource('caregivers');
    caregivers.addMethod('POST', createCaregiverFunctionIntegration);
    const caregiver_id = caregivers.addResource('{caregiver_id}');
    caregiver_id.addMethod('GET', getCaregiverFunctionIntegration);
    caregiver_id.addMethod('DELETE', deleteCaregiverFunctionIntegration);
    const caregiverPatients = caregiver_id.addResource('patients');
    caregiverPatients.addMethod('GET', getAllPatientsFunctionIntegration);

    const patients = api.root.addResource('patients');
    patients.addMethod('POST', createPatientFunctionIntegration);
    const patient_id = patients.addResource('{patient_id}');
    patient_id.addResource('device').addMethod('POST', updatePatientDeviceFunctionIntegration);
    patient_id.addResource('verify').addMethod('POST', verifyPatientFunctionIntegration);
    patient_id.addMethod('GET', getPatientFunctionIntegration);
    patient_id.addMethod('DELETE', deletePatientFunctionIntegration);
  }

  private static createLambdaIntegration(lambdaFunction: lambda.Function) {
    return new apigateway.LambdaIntegration(lambdaFunction, { proxy: true });
  }
}
