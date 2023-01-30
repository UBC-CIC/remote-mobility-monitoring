import * as cdk from 'aws-cdk-lib';
import * as lambda from 'aws-cdk-lib/aws-lambda';
import * as apigateway from 'aws-cdk-lib/aws-apigateway';

interface ApiGatewayStackProps extends cdk.StackProps {
  readonly defaultFunction: lambda.Function;
  readonly getOrganizationFunction: lambda.Function;
  readonly createCaregiverFunction: lambda.Function;
  readonly addPatientFunction: lambda.Function;
  readonly removePatientFunction: lambda.Function;
  readonly getCaregiverFunction: lambda.Function;
  readonly getAllPatientsFunction: lambda.Function;
  readonly updateCaregiverFunction: lambda.Function;
  readonly deleteCaregiverFunction: lambda.Function;
  readonly createPatientFunction: lambda.Function;
  readonly updatePatientDeviceFunction: lambda.Function;
  readonly verifyPatientFunction: lambda.Function;
  readonly getPatientFunction: lambda.Function;
  readonly getAllCaregiversFunction: lambda.Function;
  readonly updatePatientFunction: lambda.Function;
  readonly deletePatientFunction: lambda.Function;
}

export class ApiGatewayStack extends cdk.Stack {
  constructor(scope: cdk.App, id: string, props: ApiGatewayStackProps) {
    super(scope, id, props);

    const api = new apigateway.LambdaRestApi(this, 'RemoteMobilityMonitoringApi', {
      handler: props.defaultFunction,
      proxy: false,
    });

    const getOrganizationFunctionIntegration = ApiGatewayStack.createLambdaIntegration(props.getOrganizationFunction);

    const createCaregiverFunctionIntegration = ApiGatewayStack.createLambdaIntegration(props.createCaregiverFunction);
    const addPatientFunctionIntegration = ApiGatewayStack.createLambdaIntegration(props.addPatientFunction);
    const removePatientFunctionIntegration = ApiGatewayStack.createLambdaIntegration(props.removePatientFunction);
    const getCaregiverFunctionIntegration = ApiGatewayStack.createLambdaIntegration(props.getCaregiverFunction);
    const getAllPatientsFunctionIntegration = ApiGatewayStack.createLambdaIntegration(props.getAllPatientsFunction);
    const updateCaregiverFunctionIntegration = ApiGatewayStack.createLambdaIntegration(props.updateCaregiverFunction);
    const deleteCaregiverFunctionIntegration = ApiGatewayStack.createLambdaIntegration(props.deleteCaregiverFunction);

    const createPatientFunctionIntegration = ApiGatewayStack.createLambdaIntegration(props.createPatientFunction);
    const updatePatientDeviceFunctionIntegration = ApiGatewayStack.createLambdaIntegration(props.updatePatientDeviceFunction);
    const verifyPatientFunctionIntegration = ApiGatewayStack.createLambdaIntegration(props.verifyPatientFunction);
    const getPatientFunctionIntegration = ApiGatewayStack.createLambdaIntegration(props.getPatientFunction);
    const getAllCaregiversFunctionIntegration = ApiGatewayStack.createLambdaIntegration(props.getAllCaregiversFunction);
    const updatePatientFunctionIntegration = ApiGatewayStack.createLambdaIntegration(props.updatePatientFunction);
    const deletePatientFunctionIntegration = ApiGatewayStack.createLambdaIntegration(props.deletePatientFunction);

    const organizations = api.root.addResource('organizations');
    const organization_id = organizations.addResource('{organization_id}');
    organization_id.addMethod('GET', getOrganizationFunctionIntegration);

    const caregivers = api.root.addResource('caregivers');
    caregivers.addMethod('POST', createCaregiverFunctionIntegration);
    const caregiver_id = caregivers.addResource('{caregiver_id}');
    caregiver_id.addMethod('GET', getCaregiverFunctionIntegration);
    caregiver_id.addMethod('PUT', updateCaregiverFunctionIntegration);
    caregiver_id.addMethod('DELETE', deleteCaregiverFunctionIntegration);
    const caregiver_patients = caregiver_id.addResource('patients');
    caregiver_patients.addMethod('GET', getAllPatientsFunctionIntegration);
    const caregiver_patient_id = caregiver_patients.addResource('{patient_id}');
    caregiver_patient_id.addMethod('POST', addPatientFunctionIntegration);
    caregiver_patient_id.addMethod('DELETE', removePatientFunctionIntegration);

    const patients = api.root.addResource('patients');
    patients.addMethod('POST', createPatientFunctionIntegration);
    const patient_id = patients.addResource('{patient_id}');
    patient_id.addResource('device').addMethod('POST', updatePatientDeviceFunctionIntegration);
    patient_id.addResource('verify').addMethod('POST', verifyPatientFunctionIntegration);
    patient_id.addResource('caregivers').addMethod('GET', getAllCaregiversFunctionIntegration);
    patient_id.addMethod('GET', getPatientFunctionIntegration);
    patient_id.addMethod('PUT', updatePatientFunctionIntegration);
    patient_id.addMethod('DELETE', deletePatientFunctionIntegration);
  }

  private static createLambdaIntegration(lambdaFunction: lambda.Function) {
    return new apigateway.LambdaIntegration(lambdaFunction, { proxy: true });
  }
}
