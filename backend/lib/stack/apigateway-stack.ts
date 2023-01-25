import * as cdk from 'aws-cdk-lib';
import * as lambda from 'aws-cdk-lib/aws-lambda';
import * as apigateway from 'aws-cdk-lib/aws-apigateway';
import { LambdaStack } from "./lambda-stack";

export class ApiGatewayStack extends cdk.Stack {
  constructor(scope: cdk.App, id: string, lambdaStack: LambdaStack, props?: cdk.StackProps) {
    super(scope, id, props);

    const api = new apigateway.LambdaRestApi(this, 'RemoteMobilityMonitoringApi', {
      handler: lambdaStack.defaultFunction,
      proxy: false
    });
    const createCaregiverFunctionIntegration = ApiGatewayStack.createLambdaIntegration(lambdaStack.createCaregiverFunction);
    const createPatientFunctionIntegration = ApiGatewayStack.createLambdaIntegration(lambdaStack.createPatientFunction);
    const verifyPatientFunctionIntegration = ApiGatewayStack.createLambdaIntegration(lambdaStack.verifyPatientFunction);

    const caregivers = api.root.addResource('caregivers');
    caregivers.addMethod('POST', createCaregiverFunctionIntegration);
    const patients = api.root.addResource('patients');
    patients.addMethod('POST', createPatientFunctionIntegration);
    const verify = api.root.addResource('verify');
    verify.addMethod('POST', verifyPatientFunctionIntegration);
  }

  private static createLambdaIntegration(lambdaFunction: lambda.Function) {
    return new apigateway.LambdaIntegration(lambdaFunction.currentVersion, {proxy: true});
  }
}
