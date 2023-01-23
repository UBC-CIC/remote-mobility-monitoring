import * as cdk from 'aws-cdk-lib';
import * as dynamodb from 'aws-cdk-lib/aws-dynamodb';

export class DynamoDbStack extends cdk.Stack {
  public static ORGANIZATION_TABLE_NAME = 'organization';
  public static ADMIN_TABLE_NAME = 'admin';
  public static CAREGIVER_TABLE_NAME = 'caregiver';
  public static PATIENT_TABLE_NAME = 'patient';

  public static ORGANIZATION_TABLE_NAME_GSI_NAME = 'name-gsi';
  public static ADMIN_TABLE_EMAIL_GSI_NAME = 'email-gsi';
  public static ADMIN_TABLE_ORGANIZATION_ID_GSI_NAME = 'organization_id-gsi';
  public static CAREGIVER_TABLE_EMAIL_GSI_NAME = 'email-gsi';
  public static CAREGIVER_TABLE_ORGANIZATION_ID_GSI_NAME = 'organization_id-gsi';
  public static PATIENT_TABLE_DEVICE_ID_GSI_NAME = 'device_id-gsi';

  public readonly organizationTable: dynamodb.Table;
  public readonly adminTable: dynamodb.Table;
  public readonly caregiverTable: dynamodb.Table;
  public readonly patientTable: dynamodb.Table;

  constructor(scope: cdk.App, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    this.organizationTable = this.createOrganizationTable();
    this.adminTable = this.createAdminTable();
    this.caregiverTable = this.createCaregiverTable();
    this.patientTable = this.createPatientTable();
  }

  private createOrganizationTable(): dynamodb.Table {
    const table = new dynamodb.Table(
      this,
      DynamoDbStack.ORGANIZATION_TABLE_NAME,
      DynamoDbStack.createTableProps(DynamoDbStack.ORGANIZATION_TABLE_NAME, 'id')
    );
    table.addGlobalSecondaryIndex(DynamoDbStack.createGsiProps(DynamoDbStack.ORGANIZATION_TABLE_NAME_GSI_NAME, 'name'));
    return table;
  }

  private createAdminTable(): dynamodb.Table {
    const table = new dynamodb.Table(
      this,
      DynamoDbStack.ADMIN_TABLE_NAME,
      DynamoDbStack.createTableProps(DynamoDbStack.ADMIN_TABLE_NAME, 'id')
    );
    table.addGlobalSecondaryIndex(DynamoDbStack.createGsiProps(DynamoDbStack.ADMIN_TABLE_EMAIL_GSI_NAME, 'email'));
    table.addGlobalSecondaryIndex(DynamoDbStack.createGsiProps(DynamoDbStack.ADMIN_TABLE_ORGANIZATION_ID_GSI_NAME, 'organization_id'));
    return table;
  }

  private createCaregiverTable(): dynamodb.Table {
    const table = new dynamodb.Table(
      this,
      DynamoDbStack.CAREGIVER_TABLE_NAME,
      DynamoDbStack.createTableProps(DynamoDbStack.CAREGIVER_TABLE_NAME, 'id')
    );
    table.addGlobalSecondaryIndex(DynamoDbStack.createGsiProps(DynamoDbStack.CAREGIVER_TABLE_EMAIL_GSI_NAME, 'email'));
    table.addGlobalSecondaryIndex(DynamoDbStack.createGsiProps(DynamoDbStack.CAREGIVER_TABLE_ORGANIZATION_ID_GSI_NAME, 'organization_id'));
    return table;
  }

  private createPatientTable(): dynamodb.Table {
    const table = new dynamodb.Table(
      this,
      DynamoDbStack.PATIENT_TABLE_NAME,
      DynamoDbStack.createTableProps(DynamoDbStack.PATIENT_TABLE_NAME, 'id')
    );
    table.addGlobalSecondaryIndex(DynamoDbStack.createGsiProps(DynamoDbStack.PATIENT_TABLE_DEVICE_ID_GSI_NAME, 'device_id'));
    return table;
  }

  private static createTableProps(tableName: string, partitionKey: string): dynamodb.TableProps {
    return {
      tableName: tableName,
      partitionKey: {
        name: partitionKey,
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
