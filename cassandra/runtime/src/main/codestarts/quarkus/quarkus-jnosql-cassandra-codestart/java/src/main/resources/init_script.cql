CREATE KEYSPACE IF NOT EXISTS garage
    WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};

CREATE COLUMNFAMILY IF NOT EXISTS garage.Car
(
    "_id" text PRIMARY KEY,
    vin text,
    model text,
    make text,
    transmission text
);

CREATE INDEX IF NOT EXISTS car_transmission_idx ON garage.Car (transmission);