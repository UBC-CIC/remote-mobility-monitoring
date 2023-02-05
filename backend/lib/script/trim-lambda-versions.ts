import {
  DeleteFunctionCommand,
  FunctionConfiguration,
  FunctionVersion,
  LambdaClient, ListAliasesCommand,
  ListFunctionsCommand,
} from '@aws-sdk/client-lambda';

interface CounterWithFunctions {
  numVersions: number;
  functions: FunctionConfiguration[];
}

const lambdaClient = new LambdaClient({region: 'us-west-2'});

// including LATEST, so minimum should be 2
const VERSIONS_TO_KEEP = 3;

async function trimLambdaVersions(): Promise<number> {
  console.log('Starting to trim Lambda versions');

  const counterWithFunctions = await buildCounter();

  let numTrimmedVersions = 0;
  for (const [functionName, counterElement] of counterWithFunctions.entries()) {
    numTrimmedVersions += await processFunction(functionName, counterElement.numVersions, counterElement.functions);
  }
  return numTrimmedVersions;
}

async function buildCounter(): Promise<Map<string, CounterWithFunctions>> {
  const functionCounter = new Map<string, CounterWithFunctions>();
  let marker: string | undefined;
  do {
    const listFunctionCommand = new ListFunctionsCommand({
      FunctionVersion: FunctionVersion.ALL,
      Marker: marker,
    })
    const { Functions: functions, NextMarker: nextMarker } = await lambdaClient.send(listFunctionCommand);
    if (!functions) {
      continue;
    }

    for (const lambdaFunction of functions) {
      const functionName = lambdaFunction.FunctionName!;
      if (functionCounter.has(functionName)) {
        const counterElement = functionCounter.get(functionName)!;
        counterElement.numVersions++;
        counterElement.functions.push(lambdaFunction);
      } else {
        functionCounter.set(functionName, {
          numVersions: 1,
          functions: [lambdaFunction],
        });
      }
    }
    marker = nextMarker;
  } while (marker);

  return functionCounter;
}

async function processFunction(functionName: string, numVersions: number, functions: FunctionConfiguration[]): Promise<number> {
  console.log(`Trimming Lambda ${functionName}`);

  let numTrimmedVersions = 0;
  if (numVersions <= VERSIONS_TO_KEEP) {
    return numTrimmedVersions;
  }

  const listAliasCommand = new ListAliasesCommand({
    FunctionName: functionName,
  });
  const { Aliases: aliases } = await lambdaClient.send(listAliasCommand);
  if (!aliases || aliases.length <= 0) {
    return numTrimmedVersions;
  }
  const alias = aliases[0];

  functions.sort((a, b) => {
    const lastModifiedA = new Date(a.LastModified!);
    const lastModifiedB = new Date(b.LastModified!);
    if (lastModifiedA < lastModifiedB) {
      return -1;
    } else if (lastModifiedA > lastModifiedB) {
      return 1;
    } else {
      return 0;
    }
  });

  for (let i = 0; i < numVersions - VERSIONS_TO_KEEP; i++) {
    const functionToDelete = functions[i];
    console.log(`Deleting function ${functionToDelete.FunctionArn}`);

    if (functionToDelete.Version === alias.FunctionVersion) {
      console.log(`Trying to delete function ${functionToDelete.FunctionArn} but alias is pointing to it. Continuing`);
      continue;
    }

    const deleteFunctionCommand = new DeleteFunctionCommand({
      FunctionName: functionToDelete.FunctionArn,
    })
    await lambdaClient.send(deleteFunctionCommand);
    numTrimmedVersions++;
  }

  return numTrimmedVersions;
}

trimLambdaVersions().then((numTrimmedVersions) => {
  console.log(`Trimmed ${numTrimmedVersions} Lambda versions`)
}).catch((error) => {
  console.error(`Encountered error ${error}`)
});
