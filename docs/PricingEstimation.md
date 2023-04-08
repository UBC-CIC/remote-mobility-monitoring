# Pricing Estimation

This page presents a rough price estimation for a fairly big clinic with 1000 patients and 100 caregivers. The price estimation is based on the following assumptions:
- Patients send metrics once per day
- Caregivers query metrics 10 times per day
- There are 20 working days per month
- Each Caregiver cares for 50 patients
- Each record in DynamoDB is around 350 bytes
- 20 new patients sign up every month
- Region is us-west-2

### Cognito
The number of active users is 1100, which falls under free tier for Cognito.

**Monthly Cost**: $0 USD

### API Gateway
The number of requests per month is calculated as follows:
- 1000 patients * 1 request per day * 30 days per month = 30,000 requests per month
- 100 caregivers * 10 queries per day * 5 requests per query * 20 working days per month = 100,000 requests per month

Total number of requests per month = 130,000 requests per month. This means both API Gateway is invoked 130,000 times per month.

**Monthly Cost**: $0.46 USD

### Lambda
The number of Lambda invocations per month is 130,000. If the duration of each invocation is 150ms and memory allocated is 1024 MB, then the Lambda usage falls under free tier.

**Monthly Cost**: $0 USD

### SES
Since the number of new patients per month is 20, the number of emails sent per month is at most 20. This means that the number of emails sent per month falls under free tier.

**Monthly Cost**: $0 USD

### DynamoDB
If there are 100,000 writes per month and 500,000 reads per month for DynamoDB on demand mode.

**Monthly Cost**: $0.44 USD

### Timestream
The number of memory writes is number of patient requests per month * the number of metric types per request, which is 30,000 x 7 = 210,000 writes per month. The number of queries is 100,000 per month.

**Monthly Cost**: $9.77 USD

## Total Monthly Cost

## Summary
The total monthly cost is 0 + 0.46 + 0 + 0 + 0.44 + 9.77 = $10.67 USD.
Linked here is the calculation used for the above estimation [pricing estimation](./images/pricing_estimation.pdf).

Please keep in mind that this is a rough estimation and the actual cost may vary depending on the actual usage. For more information, please refer to the [AWS Pricing Calculator](https://calculator.aws/#/).

