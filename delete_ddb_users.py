import queue
import threading

import misc.config as config

import boto3 as aws

ddb = aws.client('dynamodb')

users = queue.Queue(maxsize=20)
org_id = config.get('ORGANIZATION_ID')


def delete_user():
    try:
        while True:
            car_id = users.get(block=True, timeout=2)
            paginator = ddb.get_paginator('query')
            page_iterator = paginator.paginate(
                TableName=config.get('DYNAMODB_TABLE'),
                KeyConditionExpression='pid = :pid',
                ExpressionAttributeValues={
                    ':pid': {'S': car_id}
                }
            )

            index_paginator = ddb.get_paginator('query')
            index_page_iterator = index_paginator.paginate(
                TableName=config.get('DYNAMODB_TABLE'),
                KeyConditionExpression='sid = :pid',
                ExpressionAttributeValues={
                    ':pid': {'S': car_id}
                },
                IndexName='sid-gsi'
            )

            for page in page_iterator:
                for item in page['Items']:
                    ddb.delete_item(
                        TableName=config.get('DYNAMODB_TABLE'),
                        Key={
                            'pid': {'S': item['sid']['S']},
                            'sid': {'S': item['sid']['S']}
                        }
                    )

            for page in index_page_iterator:
                for item in page['Items']:
                    ddb.delete_item(
                        TableName=config.get('DYNAMODB_TABLE'),
                        Key={
                            'pid': {'S': item['pid']['S']},
                            'sid': {'S': item['sid']['S']}
                        }
                    )
            print(f"Deleted user {car_id}")
    except queue.Empty:
        print("Queue is empty, exiting thread")
        return


def main():
    workers = []
    for i in range(3):
        t = threading.Thread(target=delete_user)
        t.start()
        workers.append(t)

    caregiver_paginator = ddb.get_paginator('scan')
    caregiver_page_iterator = caregiver_paginator.paginate(
        TableName=config.get('DYNAMODB_TABLE'),
        FilterExpression='begins_with(sid, :sid)',
        ExpressionAttributeValues={
            ':sid': {'S': 'car-'}
        }
    )
    for page in caregiver_page_iterator:
        for item in page['Items']:
            if item['car-email']['S'].startswith('LoadTest'):
                users.put(item['sid']['S'], block=True)

    patient_paginator = ddb.get_paginator('scan')
    patient_page_iterator = patient_paginator.paginate(
        TableName=config.get('DYNAMODB_TABLE'),
        FilterExpression='begins_with(sid, :sid)',
        ExpressionAttributeValues={
            ':sid': {'S': 'pat-'}
        }
    )
    for page in patient_page_iterator:
        for item in page['Items']:
            if item['pat-email']['S'].startswith('LoadTest'):
                users.put(item['sid']['S'], block=True)



if __name__ == '__main__':
    main()
