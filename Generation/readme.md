Example Requests:

URL: http://localhost:8089/graphql
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

