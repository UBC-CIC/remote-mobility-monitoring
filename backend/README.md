Java version: Amazon corretto 11


## Build

Run the AWS DynamoDB docker container for local testing:
```
docker-compose up -d
```
 - Alternatively, you can run DynamoDB natively by downloading the DynamoDB Local JAR file and running it with Java. For more information, see [Running DynamoDB Local](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.DownloadingAndRunning.html) in the Amazon DynamoDB Developer Guide.

Make sure you are in backend directory, then run the following:
```
mvn clean package
```
This will build the AWS Lambda files written in Java and package it into ```./assets/function.jar```.

After that, run the following:
```
npm i -g aws-cdk
npm i
npm run build
```
This will install the CDK dependencies, and then build the CDK files written in TypeScript.

## Deploy

Initialize the CDK stacks (required only if you have not deployed this stack before). Note that by default, all stacks are created in `us-west-2` due to region restrictions for Amazon Timestream.
```
cdk synth
cdk bootstrap aws://YOUR_AWS_ACCOUNT_ID/us-west-2
```

Deploy the CDK stacks:
```
cdk deploy --all
```