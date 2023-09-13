# Clustering

In this example, learn how to use the Feed Service SDK's Clustering feature. Clustering lets multiple servers, also known as instances, read a flow's feed events more efficiently. Clustering creates a high availability model where a flow's shards can be read across multiple instances. It also allows the SDK to balance shards across instances when new instances are added and when an existing instance becomes unavailable for any reason.

Refer to [`ExampleConfig.java`](src/main/java/com/autonomic/tmc/example/feed/cluster/ExampleConfig.java) to see how this example configures a  `FeedStream` using the `FileInstanceStore` to save instance statuses to local files.

`FileInstanceStore` is for demonstration purposes and **should be used for development only**. A new implementation of the `InstanceStore` interface should be configured for Production and should use a reliable, persistent storage solution such as Redis, Hazelcast or other database.

> Learn more about the Clustering feature and how this example works on our Developer portal at [Example: Clustering](https://tmcfeedsdk.page.link/example-clustering).

## Before you run the example

This example is configured to run against Au Production environment. Run the following commands with Production specific values to set these environment variables:

*Mac/Linux*:

```shell
export TMC_CLIENT_ID=<your-client-id>
export TMC_CLIENT_SECRET=<your-client-secret>
export TMC_FLOW=<your-flow>
```

*Windows*:

```shell script
set TMC_CLIENT_ID=<your-client-id>
set TMC_CLIENT_SECRET=<your-client-secret>
set TMC_FLOW=<your-flow>
```

## How to run the example

Once you have set the environment variables, you can run the example with the following command:

*Mac/Linux*:

```shell script
./run.sh
```

*Windows*:

```shell script
.\run.bat
```

## Understand the output

In this section we instruct you to call the API endpoints. If you use Postman, you can import [this Postman API collection](Feed_Service_SDK_Clustering_Example.postman_collection.json) to access these endpoints more quickly.

After the example application starts, there is no instance available to read data from a flow. The example exposes the endpoints below:

1. `PUT` `/start` : Start a new instance. You can start as many instances as you like.

2. `GET` `/view-all` : View the available instances, and the shards they are reading as a JSON response. It will look similar to:

  ```json
  {
      "1": {
        "reading": [
          "ABCDEFGHI/j/////AQ==",
          "ABCDEFGHI/j/////ARgB",
          "ABCDEFGHI/j/////ARgC",
          ...
        ],
        "starting": [
          "ABCDEFGHI/j/////ARgD",
          "ABCDEFGHI/j/////ARgE",
          "ABCDEFGHI/j/////ARgF",
          ...
        ],
        "stopping": []
      },
      "2": {
        "reading": [
          "ABCDEFGHI/j/////ARgG",
          "ABCDEFGHI/j/////ARgH",
          ...
        ],
        "starting": [
          "ABCDEFGHI/j/////ARgI",
          "ABCDEFGHI/j/////ARgJ",
          ...
        ],
        "stopping": []
      }
  }
  ```

3. `DELETE` `/stop/{instanceNumber}` : Stop a specific instance. You can stop multiple instances by calling this endpoint again with a different `instanceNumber`.

## How to stop the example

To stop the example, you will need to terminate the process with `Ctrl+C`.

> For troubleshooting issues visit the Developer portal at [Troubleshooting](https://tmcfeedsdk.page.link/feed-service-sdk/troubleshooting).
