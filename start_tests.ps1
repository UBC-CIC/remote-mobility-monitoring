python .\delete_cognito_users.py; python .\delete_ddb_users.py;
Start-Sleep -Seconds 300; locust -f .\user.py -H "https://tqd4aslpsj.execute-api.us-west-2.amazonaws.com/prod/" -u 20 -r 2 -t 5m --headless --html "$PWD/output/light_report.html" --csv "light_load"
python .\delete_cognito_users.py; python .\delete_ddb_users.py;
Start-Sleep -Seconds 300; locust -f .\user.py -H "https://tqd4aslpsj.execute-api.us-west-2.amazonaws.com/prod/" -u 100 -r 20 -t 5m --headless --html "$PWD/output/medium_light_report.html" --csv "medium_light_load"
python .\delete_cognito_users.py; python .\delete_ddb_users.py;
Start-Sleep -Seconds 300; locust -f .\user.py -H "https://tqd4aslpsj.execute-api.us-west-2.amazonaws.com/prod/" -u 200 -r 20 -t 5m --headless --html "$PWD/output/medium_heavy_report.html" --csv "medium_heavy_load"
python .\delete_cognito_users.py; python .\delete_ddb_users.py;
Start-Sleep -Seconds 300; locust -f .\user.py -H "https://tqd4aslpsj.execute-api.us-west-2.amazonaws.com/prod/" -u 400 -r 40 -t 5m --headless --html "$PWD/output/heavy_report.html" --csv "heavy_load"
python .\delete_cognito_users.py; python .\delete_ddb_users.py;