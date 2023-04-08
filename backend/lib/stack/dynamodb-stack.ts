import * as cdk from 'aws-cdk-lib';
import * as dynamodb from 'aws-cdk-lib/aws-dynamodb';
import { formResourceName } from "../utility";

interface DynamoDbStackProps extends cdk.StackProps {
  readonly stage: string;
}

export class DynamoDbStack extends cdk.Stack {
  public static TABLE_NAME = 'REMOTE_MOBILITY_MONITORING';

  public static PK_NAME = 'pid';
  public static SK_NAME = 'sid';
  public static SID_GSI_NAME = 'sid-gsi';
  public static ORGANIZATION_NAME_GSI_NAME = 'org-name-gsi';
  public static ADMIN_EMAIL_GSI_NAME = 'adm-email-gsi';
  public static CAREGIVER_EMAIL_GSI_NAME = 'car-email-gsi';
  public static PATIENT_EMAIL_GSI_NAME = 'pat-email-gsi';
  public static PATIENT_DEVICE_ID_GSI_NAME = 'pat-device_id-gsi';

  public readonly remoteMobilityMonitoringTable: dynamodb.Table;

  constructor(scope: cdk.App, id: string, props: DynamoDbStackProps) {
    super(scope, id, props);

    const tableName = formResourceName(DynamoDbStack.TABLE_NAME, props.stage);
    this.remoteMobilityMonitoringTable = this.createRemoteMobilityMonitoringTable(tableName);
  }

  private createRemoteMobilityMonitoringTable(tableName: string): dynamodb.Table {
    const table = new dynamodb.Table(
      this,
      'RemoteMobilityMonitoringDynamoDB',
      DynamoDbStack.createTableProps(tableName, DynamoDbStack.PK_NAME, DynamoDbStack.SK_NAME)
    );
    table.addGlobalSecondaryIndex(DynamoDbStack.createGsiProps(DynamoDbStack.SID_GSI_NAME, DynamoDbStack.SK_NAME, DynamoDbStack.PK_NAME));
    table.addGlobalSecondaryIndex(DynamoDbStack.createGsiProps(DynamoDbStack.ORGANIZATION_NAME_GSI_NAME, 'org-name'));
    table.addGlobalSecondaryIndex(DynamoDbStack.createGsiProps(DynamoDbStack.ADMIN_EMAIL_GSI_NAME, 'adm-email'));
    table.addGlobalSecondaryIndex(DynamoDbStack.createGsiProps(DynamoDbStack.CAREGIVER_EMAIL_GSI_NAME, 'car-email'));
    table.addGlobalSecondaryIndex(DynamoDbStack.createGsiProps(DynamoDbStack.PATIENT_EMAIL_GSI_NAME, 'pat-email'));
    table.addGlobalSecondaryIndex(DynamoDbStack.createGsiProps(DynamoDbStack.PATIENT_DEVICE_ID_GSI_NAME, 'pat-device_id'));
    return table;
  }

  private static createTableProps(tableName: string, partitionKey: string, sortKey: string): dynamodb.TableProps {
    return {
      tableName: tableName,
      partitionKey: {
        name: partitionKey,
        type: dynamodb.AttributeType.STRING,
      },
      sortKey: {
        name: sortKey,
        type: dynamodb.AttributeType.STRING,
      },
      billingMode: dynamodb.BillingMode.PAY_PER_REQUEST,
    };
  }

  private static createGsiProps(gsiName: string, partitionKeyName: string, sortKeyName?: string): dynamodb.GlobalSecondaryIndexProps {
    const gsiProps = {
      indexName: gsiName,
      partitionKey: {
        name: partitionKeyName,
        type: dynamodb.AttributeType.STRING,
      },
      projectionType: dynamodb.ProjectionType.ALL,
    };
    if (sortKeyName) {
      const sortKey = {
        name: sortKeyName,
        type: dynamodb.AttributeType.STRING,
      }
      return { ...gsiProps, sortKey }
    }
    return gsiProps
  }
}
