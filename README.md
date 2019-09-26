
# platform-status-frontend

Front end half of the platform status application, designed to fun in the public zone and test various infrastructure elements.

## Endpoints

### Status Code Test Endpoint

The Status Code endpoint can be used to generate HTTP responses with a specified status and body.
The response can also be configured to be sent after a fixed delay.

```
{
    "status_code": 500,
    "delay_in_ms": 100,
    "msg": "Something has gone wrong" 
}
```

The `delay_in_ms` field is optional and can be omitted, defaulting to 0ms.

Curl example:

```
curl "https://[url-for-platform-status-fronted]/platform-status/fail"  -H "Content-Type: application/json"  -d '{"status_code": 499, "delay_in_ms":4, "msg":"test message"}'
```


### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
