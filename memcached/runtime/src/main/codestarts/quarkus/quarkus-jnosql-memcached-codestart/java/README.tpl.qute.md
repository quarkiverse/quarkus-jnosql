{#include readme-header /}

Memcached is a cache, not durable storage. Configure `jnosql.memcached.host.1`
with a `host:port` address; additional servers use `.2`, `.3`, and so on.
The tests start `memcached:latest` and override the address with its dynamically mapped port.

The sample entity implements `Serializable` for the driver's default object serialization.
The sample exercises key-based insert, find, and delete operations, not Jakarta Data repositories.
Native compilation and entity CRUD have been verified for the extension's integration
application with Mandrel 25.0.4.1 in a Linux container. The extension registers entity
hierarchies, `String`, and `ArrayList` for native Java serialization. Other concrete
runtime types, including the immutable lists used in these JVM tests, may require
explicit `@RegisterForReflection(serialization = true)` registration for native applications.
