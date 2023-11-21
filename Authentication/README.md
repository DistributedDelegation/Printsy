# Authentication Service README

## Overview

## Docker Compose Setup

The service is part of a multi-container Docker application. Key components and their respective ports are as follows:

- **MongoDB**: Database running on port `8082`.
- **Mongo Express**: Web-based MongoDB admin interface accessible on port `8081`.
- **MySQL**: Database running on port `8084`.
- **phpMyAdmin**: MySQL administration tool accessible on port `8083`.
- **Authentication Service**: The main service of interest, running on port `8085`.

Refer to the `docker-compose.yml` file for more details on the service configuration.

## Authentication Service

The service is built using Maven and packaged into a Docker container. The Dockerfile for the service handles the build and packaging process.

### GraphQL Schema

The service uses the following GraphQL schema:

```graphql
type Query {
  currentUser(bearerToken: String!): User
  isUsernameAvailable(username: String!): Boolean
}

type Mutation {
  authenticate(userCredentialInput: UserCredentialInput!): String
  register(userCredentialInput: UserCredentialInput!): String
}

type User {
  userID: ID!
  emailAddress: String!
}

input UserCredentialInput {
  emailAddress: String!
  password: String!
}
```

### Accessing the Service

The service can be accessed at `http://localhost:8085/graphql`.

### GraphQL Operations

#### Queries

1. **currentUser**: Fetches the current user based on the bearer token.
   - Parameters: `bearerToken` (String)
   - Returns: `User`

2. **isUsernameAvailable**: Checks if a username is available.
   - Parameters: `username` (String)
   - Returns: Boolean

#### Mutations

1. **authenticate**: Authenticates a user and returns a JWT token.
   - Input: `UserCredentialInput` (emailAddress, password)
   - Returns: JWT Token (String)

2. **register**: Registers a new user.
   - Input: `UserCredentialInput` (emailAddress, password)
   - Returns: Confirmation Message (String)

### Current Limitations

- The service currently does not support functionality to update usernames.

## How to Use

To perform GraphQL operations, send HTTP POST requests to `http://localhost:8085/graphql` with the appropriate query or mutation structure in the request body.

### Example Request

```json
{
  "query": "mutation($input: UserCredentialInput!) { register(userCredentialInput: $input) }",
  "variables": {
    "input": {
      "emailAddress": "user@example.com",
      "password": "yourpassword"
    }
  }
}
```

Replace the query and variables as needed based on the operation you want to perform.