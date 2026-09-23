{#include readme-header /}

Memcached is a cache, not durable storage. Configure `jnosql.memcached.host.1`
with a `host:port` address; additional servers use `.2`, `.3`, and so on.
The tests start `memcached:latest` and override the address with its dynamically mapped port.

The sample entity implements `Serializable` for the driver's default object serialization.
The sample exercises key-based insert, find, and delete operations, not Jakarta Data repositories.
Native-image support is not verified: compilation currently fails on the client's
optional metrics dependency (`com.codahale.metrics.MetricRegistry`).
