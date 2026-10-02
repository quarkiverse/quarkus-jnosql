{#include readme-header /}

## Infinispan dependencies

This project aligns the embedded stack with the Infinispan BOM. Keep its explicit
dependency-management overrides: they take precedence over the older Infinispan
versions managed by the Quarkus BOM. When upgrading Infinispan, update
`infinispan.protostream.version` to the version declared by that Infinispan BOM.
