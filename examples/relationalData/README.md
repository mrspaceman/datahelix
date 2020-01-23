Generates related data where a user has an address

expected output will be similar to:

```json
[
    {
      "id": 123,
      "name": "Simon",
      "registrationDate": "2020-01-01",
      "latestOrderDate": "2020-01-02",
      "orders": [
        { "id": 456, "date": "2020-01-01" },
        { "id": 789, "date": "2020-01-02" }
      ]
    },
    {
      "id": 122,
      "name": "Jason",
      "registrationDate": "2019-04-28",
      "latestOrderDate": "2019-12-18",
      "orders": [
        { "id": 1456, "date": "2019-12-18" },
        { "id": 1789, "date": "2019-04-28" }
      ]
    }
]
```
