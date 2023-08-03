- Add database access layer and migrations, several tables: newsletters, newsletter issues, and subscribers
- Possible to create a newsletter via REST API, CRUD operations
- Possible to add a subscriber to a newsletter via REST API
- Possible to add a subscriber via an SQS message
- Possible to publish a new newsletter issue via REST API
- Possible to publish a new newsletter issue via an SQS message
- When a new newletter issue is pushed or updated it is possible to set a flag to publish it also. 
- If the flag is set the newsletter gets published: the subscribers get the emails sent using AWS SES.

- Local development environment with localstack, kind
- Deploy "production ready" service to AWS/EKS - Kubernetes