## Build

Make sure you are in backend directory, then run the following:
```
npm i -g aws-cdk
npm i
npm run build
```
This will install the necessary CDK, then the project's dependencies, and then build the TypeScript files.

## Deploy

Initialize the CDK stacks (required only if you have not deployed this stack before). Note that by default, all stacks are created in `us-west-2` due to region restrictions for Amazon Timestream.
```
cdk synth
cdk bootstrap aws://YOUR_AWS_ACCOUNT_ID/us-west-2
```

Deploy the CDK stacks:
```
cdk deploy
```