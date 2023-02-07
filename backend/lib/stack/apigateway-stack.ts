import * as cdk from 'aws-cdk-lib';
import * as lambda from 'aws-cdk-lib/aws-lambda';
import * as apigateway from 'aws-cdk-lib/aws-apigateway';
import * as cognito from "aws-cdk-lib/aws-cognito";
import { formResourceName } from "../utility";

export interface ApiGatewayStackProps extends cdk.StackProps {
  readonly stage: string;
  readonly userPool: cognito.UserPool;
  readonly defaultFunction: lambda.Function;
  readonly getOrganizationFunction: lambda.Alias;
  readonly getAdminFunction: lambda.Alias;
  readonly createCaregiverFunction: lambda.Alias;
  readonly addPatientFunction: lambda.Alias;
  readonly removePatientFunction: lambda.Alias;
  readonly getCaregiverFunction: lambda.Alias;
  readonly getAllPatientsFunction: lambda.Alias;
  readonly updateCaregiverFunction: lambda.Alias;
  readonly deleteCaregiverFunction: lambda.Alias;
  readonly createPatientFunction: lambda.Alias;
  readonly updatePatientDeviceFunction: lambda.Alias;
  readonly verifyPatientFunction: lambda.Alias;
  readonly getPatientFunction: lambda.Alias;
  readonly getAllCaregiversFunction: lambda.Alias;
  readonly updatePatientFunction: lambda.Alias;
  readonly deletePatientFunction: lambda.Alias;
}

export class ApiGatewayStack extends cdk.Stack {
  constructor(scope: cdk.App, id: string, props: ApiGatewayStackProps) {
    super(scope, id, props);

    const restApiName = formResourceName('RemoteMobilityMonitoringApi', props.stage);
    const api = new apigateway.LambdaRestApi(this, restApiName, {
      restApiName: restApiName,
      handler: props.defaultFunction,
      proxy: false,
    });

    const getOrganizationFunctionIntegration = ApiGatewayStack.createLambdaIntegration(props.getOrganizationFunction);

    const getAdminFunctionIntegration = ApiGatewayStack.createLambdaIntegration(props.getAdminFunction);

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

    let methodOptions: apigateway.MethodOptions = {}
    if (props.stage === 'prod') {
      const authorizerName = 'CognitoAuthorizer';
      const cognitoAuthorizer = new apigateway.CognitoUserPoolsAuthorizer(this, authorizerName, {
        authorizerName: authorizerName,
        cognitoUserPools: [props.userPool],
      });

      methodOptions = {
        authorizer: cognitoAuthorizer,
        authorizationType: apigateway.AuthorizationType.COGNITO,
      }
    }

    const organizations = api.root.addResource('organizations');
    const organization_id = organizations.addResource('{organization_id}');
    organization_id.addMethod('GET', getOrganizationFunctionIntegration, methodOptions); // GET /organizations/{organization_id}

    const admins = api.root.addResource('admins');
    const admin_id = admins.addResource('{admin_id}');
    admin_id.addMethod('GET', getAdminFunctionIntegration, methodOptions); // GET /admins/{admin_id}

    const caregivers = api.root.addResource('caregivers');
    caregivers.addMethod('POST', createCaregiverFunctionIntegration, methodOptions); // POST /caregivers
    const caregiver_id = caregivers.addResource('{caregiver_id}');
    caregiver_id.addMethod('GET', getCaregiverFunctionIntegration, methodOptions); // GET /caregivers/{caregiver_id}
    caregiver_id.addMethod('PUT', updateCaregiverFunctionIntegration, methodOptions); // PUT /caregivers/{caregiver_id}
    caregiver_id.addMethod('DELETE', deleteCaregiverFunctionIntegration, methodOptions); // DELETE /caregivers/{caregiver_id}
    const caregiver_patients = caregiver_id.addResource('patients');
    caregiver_patients.addMethod('GET', getAllPatientsFunctionIntegration, methodOptions); // GET /caregivers/{caregiver_id}/patients
    const caregiver_patient_id = caregiver_patients.addResource('{patient_id}');
    caregiver_patient_id.addMethod('POST', addPatientFunctionIntegration, methodOptions); // POST /caregivers/{caregiver_id}/patients/{patient_id}
    caregiver_patient_id.addMethod('DELETE', removePatientFunctionIntegration, methodOptions); // DELETE /caregivers/{caregiver_id}/patients/{patient_id}

    const patients = api.root.addResource('patients');
    patients.addMethod('POST', createPatientFunctionIntegration, methodOptions); // POST /patients
    const patient_id = patients.addResource('{patient_id}');
    patient_id.addResource('device').addMethod('POST', updatePatientDeviceFunctionIntegration, methodOptions); // POST /patients/{patient_id}/device
    patient_id.addResource('verify').addMethod('POST', verifyPatientFunctionIntegration, methodOptions); // POST /patients/{patient_id}/verify
    patient_id.addResource('caregivers').addMethod('GET', getAllCaregiversFunctionIntegration, methodOptions); // GET /patients/{patient_id}/caregivers
    patient_id.addMethod('GET', getPatientFunctionIntegration, methodOptions); // GET /patients/{patient_id}
    patient_id.addMethod('PUT', updatePatientFunctionIntegration, methodOptions); // PUT /patients/{patient_id}
    patient_id.addMethod('DELETE', deletePatientFunctionIntegration, methodOptions); // DELETE /patients/{patient_id}
  }

  private static createLambdaIntegration(lambdaFunction: lambda.Alias | lambda.Function) {
    return new apigateway.LambdaIntegration(lambdaFunction, { proxy: true });
  }
}
