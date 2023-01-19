import * as cdk from 'aws-cdk-lib';
import * as dynamodb from 'aws-cdk-lib/aws-dynamodb';

export class DynamoDbStack extends cdk.Stack {
  public static ORGANIZATION_TABLE_NAME = 'organization';
  public static ADMIN_TABLE_NAME = 'admin';
  public static CAREGIVER_TABLE_NAME = 'caregiver';

  public static ADMIN_TABLE_EMAIL_GSI_NAME = 'adminEmailGsi';
  public static CAREGIVER_TABLE_EMAIL_GSI_NAME = 'caregiverEmailGsi';

  public readonly organizationTable: dynamodb.Table;
  public readonly adminTable: dynamodb.Table;
  public readonly caregiverTable: dynamodb.Table;

  constructor(scope: cdk.App, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    this.organizationTable = this.createOrganizationTable();
    this.adminTable = this.createAdminTable();
    this.caregiverTable = this.createCaregiverTable();
  }

  private createOrganizationTable(): dynamodb.Table {
    return new dynamodb.Table(
      this,
      DynamoDbStack.ORGANIZATION_TABLE_NAME,
      DynamoDbStack.createTableProps(DynamoDbStack.ORGANIZATION_TABLE_NAME)
    );
  }

  private createAdminTable(): dynamodb.Table {
    const table = new dynamodb.Table(
      this,
      DynamoDbStack.ADMIN_TABLE_NAME,
      DynamoDbStack.createTableProps(DynamoDbStack.ADMIN_TABLE_NAME)
    );
    table.addGlobalSecondaryIndex(DynamoDbStack.createGsiProps(DynamoDbStack.ADMIN_TABLE_EMAIL_GSI_NAME, 'email'));
    return table;
  }

  private createCaregiverTable(): dynamodb.Table {
    const table = new dynamodb.Table(
      this,
      DynamoDbStack.CAREGIVER_TABLE_NAME,
      DynamoDbStack.createTableProps(DynamoDbStack.CAREGIVER_TABLE_NAME)
    );
    table.addGlobalSecondaryIndex(DynamoDbStack.createGsiProps(DynamoDbStack.CAREGIVER_TABLE_EMAIL_GSI_NAME, 'email'));
    return table;
  }

  private static createTableProps(tableName: string): dynamodb.TableProps {
    return {
      tableName: tableName,
      partitionKey: {
        name: 'id',
        type: dynamodb.AttributeType.STRING,
      },
      billingMode: dynamodb.BillingMode.PAY_PER_REQUEST,
      // TODO: encryption
    };
  }

  private static createGsiProps(gsiName: string, partitionKeyName: string): dynamodb.GlobalSecondaryIndexProps {
    return {
      indexName: gsiName,
      partitionKey: {
        name: partitionKeyName,
        type: dynamodb.AttributeType.STRING,
      },
      projectionType: dynamodb.ProjectionType.ALL,
    };
  }
}
