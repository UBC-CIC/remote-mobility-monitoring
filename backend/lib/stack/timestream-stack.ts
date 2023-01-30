import * as cdk from 'aws-cdk-lib';
import * as timestream from 'aws-cdk-lib/aws-timestream';

export class TimestreamStack extends cdk.Stack {
  public static databaseName = 'RemoteMobilityMonitoringTimestreamDatabase';

  public readonly database: timestream.CfnDatabase;
  public readonly mobilityData: timestream.CfnTable;

  constructor(scope: cdk.App, id: string, props?: cdk.StackProps) {
    super(scope, id, props);
    
    this.database = new timestream.CfnDatabase(this, 'RemoteMobilityMonitoringTimestreamDatabase', {
      databaseName: TimestreamStack.databaseName
    });

    this.mobilityData = new timestream.CfnTable(this, 'RemoteMobilityMonitoringTimestreamTable', {
      databaseName: this.database.ref,
      tableName: 'RemoteMobilityMonitoringTimestreamTable'
    });
  }
}
