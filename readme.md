# log-play-app

This project contains two applications for tracking and logging board game sessions. It is designed for personal use and consists of two main components:

1. **Backend (log-play-lambda)**: A basic backend built to follow the OpenAPI specifications.
2. **Frontend (log-play-page)**: A frontend that is still under development, with plans to use React, Typescript, Ant Design, and Next.JS.

## Backend (log-play-lambda)

### Overview

The backend is implemented in Java 17 as Lambda functions and is deployed with AWS SAM. It provides API endpoints to manipulate game logs and records, as well as health check functionality.

### API Documentation

The available endpoints are:

- **PUT /games/log**: Updates the entire game record log.
- **GET /games/log**: Retrieves the log of all games.
- **GET /games**: Gets a specific game record.
- **PUT /games**: Inserts a single game record.
- **GET /healthcheck**: Health check with database status.

#### Swagger UI

Complete API documentation is available through [Swagger UI for log-play-app](http://log-play-app-swagger-ui.s3-website.eu-central-1.amazonaws.com/)

### Deployment

The backend is deployed using AWS SAM, as described in the provided SAM template. It utilizes AWS services such as DynamoDB and ApiGateway.

#### Continuous Integration (CI)

The CI process is handled through GitHub Actions, including the setup of Java JDK 17, integration testing, packaging, and deployment to AWS.

## Frontend (log-play-page)

### Overview

The frontend is planned to be built using the following technologies:

- React
- TypeScript
- Ant Design
- Next.JS

### Status

The frontend is currently under development. The current mocked static page can be found at [log-play-page](http://log-play-page-test.s3-website.eu-central-1.amazonaws.com).
