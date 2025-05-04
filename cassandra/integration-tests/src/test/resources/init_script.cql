CREATE KEYSPACE IF NOT EXISTS test
    WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};

CREATE COLUMNFAMILY IF NOT EXISTS test.person
(
    "_id" text PRIMARY KEY,
    name text,
    phones list<text>
);
CREATE COLUMNFAMILY IF NOT EXISTS test.personrecord
(
    "_id" text PRIMARY KEY,
    name text,
    phones list<text>
);