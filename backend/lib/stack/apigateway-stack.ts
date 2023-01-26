import * as cdk from 'aws-cdk-lib';
import * as lambda from 'aws-cdk-lib/aws-lambda';
import * as apigateway from 'aws-cdk-lib/aws-apigateway';

interface ApiGatewayStackProps extends cdk.StackProps {
  readonly defaultFunction: lambda.Function;
  readonly createCaregiverFunction: lambda.Function;
  readonly createPatientFunction: lambda.Function;
  readonly verifyPatientFunction: lambda.Function;
}

export class ApiGatewayStack extends cdk.Stack {
  constructor(scope: cdk.App, id: string, props: ApiGatewayStackProps) {
    super(scope, id, props);

    const api = new apigateway.LambdaRestApi(this, 'RemoteMobilityMonitoringApi', {
      handler: props.defaultFunction,
      proxy: false,
    });
    const createCaregiverFunctionIntegration = ApiGatewayStack.createLambdaIntegration(props.createCaregiverFunction);
    const createPatientFunctionIntegration = ApiGatewayStack.createLambdaIntegration(props.createPatientFunction);
    const verifyPatientFunctionIntegration = ApiGatewayStack.createLambdaIntegration(props.verifyPatientFunction);

    const caregivers = api.root.addResource('caregivers');
    caregivers.addMethod('POST', createCaregiverFunctionIntegration);
    const patients = api.root.addResource('patients');
    patients.addMethod('POST', createPatientFunctionIntegration);
    const patient_id = patients.addResource('{patient_id}');
    patient_id.addResource('verify').addMethod('POST', verifyPatientFunctionIntegration);
  }

  private static createLambdaIntegration(lambdaFunction: lambda.Function) {
    return new apigateway.LambdaIntegration(lambdaFunction, { proxy: true });
  }
}
