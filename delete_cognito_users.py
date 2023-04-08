import queue
import threading

import boto3 as aws
import misc.config as config

users = queue.Queue(maxsize=20)

def delete_user(client, userPoolId):
    try:
        while True:
            username = users.get(block=True, timeout=3)
            client.admin_delete_user(UserPoolId=userPoolId, Username=username)
            print(f"Deleted user {username}")
    except queue.Empty:
        print("Queue is empty, exiting thread")
        return


def main():
    userPoolId = config.get('USERPOOL_ID')
    cognito = aws.client('cognito-idp')
    ddb = aws.resource('dynamodb')
    # delete all users
    paginator = cognito.get_paginator('list_users')
    page_iterator = paginator.paginate(UserPoolId=userPoolId)

    workers = []
    for i in range(3):
        t = threading.Thread(target=delete_user, args=(cognito, userPoolId))
        t.start()
        workers.append(t)

    for page in page_iterator:
        for user in page['Users']:
            # check if string starts with LoadTest-
            if user['Username'].startswith('LoadTest-'):
                users.put(user['Username'], block=True)

    for t in workers:
        t.join()
    print("All LoadTest users deleted successfully")


if __name__ == '__main__':
    main()
