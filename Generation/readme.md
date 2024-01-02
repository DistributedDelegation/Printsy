# Generation Service
By Florian Lechner

## Overview

This service provides the image generation through the OpenAI API. As part of that, it provides the following features:
- fetchFeatures:
This provides a list of dropdowns and dropdown options that can be used to refine the generation prompt.

- createImage:
Given a prompt this generates a Image through the OpenAI Api and returns the temporary link provided from the API

- uploadImage:
This saves the image from the provided OpenAI link and downloads the image and reuploads it to our IBM Cloud storage. It returns the link to our permanent cloud storage.

- deleteImage:
Given an image ID the image will be removed from the cloud.

Example Requests:

URL: http://localhost:8080/generation/graphql
POST - raw/java

createImage
```
{
    "query": "mutation($prompt: String!) { createImage(prompt: $prompt) { id imageURL } }",
    "variables": {
        "prompt": "A cute baby sea otter"
    }
}

```

deleteImage
```
{
    "query": "mutation($id: ID!) { deleteImage(id: $id) }",
    "variables": {
        "id": <ID>"
    }
}

```

