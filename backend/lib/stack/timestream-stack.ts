import * as cdk from 'aws-cdk-lib';
import * as timestream from 'aws-cdk-lib/aws-timestream';
import { formResourceName } from "../utility";

interface TimestreamStackProp extends cdk.StackProps {
  readonly stage: string;
}

export class TimestreamStack extends cdk.Stack {
  public static DATABASE_NAME = 'REMOTE_MOBILITY_MONITORING_DATABASE';
  public static TABLE_NAME = 'METRICS';

  public readonly database: timestream.CfnDatabase;
  public readonly mobilityData: timestream.CfnTable;

  constructor(scope: cdk.App, id: string, props: TimestreamStackProp) {
    super(scope, id, props);

    const databaseName = formResourceName(TimestreamStack.DATABASE_NAME, props.stage);
    this.database = new timestream.CfnDatabase(this, 'RemoteMobilityMonitoringTimestreamDatabase', {
      databaseName: databaseName,
    });

    const tableName = formResourceName(TimestreamStack.TABLE_NAME, props.stage);
    this.mobilityData = new timestream.CfnTable(this, 'RemoteMobilityMonitoringTimestreamTable', {
      databaseName: this.database.ref,
      tableName: tableName,
    });
  }
}
