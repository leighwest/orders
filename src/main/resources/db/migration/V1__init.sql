CREATE TABLE address (
    id          BIGSERIAL PRIMARY KEY,
    unit_number VARCHAR(255),
    street_number VARCHAR(255) NOT NULL,
    street_name VARCHAR(255) NOT NULL,
    street_type VARCHAR(255) NOT NULL,
    suburb      VARCHAR(255) NOT NULL,
    post_code   VARCHAR(255) NOT NULL,
    state       VARCHAR(3)
);

CREATE TABLE images (
    id          BIGSERIAL PRIMARY KEY,
    product_code VARCHAR(255) NOT NULL UNIQUE,
    bucket_name VARCHAR(255) NOT NULL,
    object_key  VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE cupcakes (
    id           BIGSERIAL PRIMARY KEY,
    product_code VARCHAR(255) NOT NULL UNIQUE,
    flavour      VARCHAR(255) NOT NULL UNIQUE,
    unit_price   NUMERIC(19, 2) NOT NULL,
    image        VARCHAR(255) REFERENCES images(product_code)
);

CREATE TABLE customer (
    id         BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    surname    VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL UNIQUE
);

CREATE INDEX idx_email ON customer(email);

CREATE TABLE orders (
    id                BIGSERIAL PRIMARY KEY,
    uuid              UUID NOT NULL UNIQUE,
    customer_order_ref BIGINT NOT NULL,
    customer_id       BIGINT NOT NULL REFERENCES customer(id),
    address_id        BIGINT REFERENCES address(id),
    total_price       NUMERIC(19, 2) NOT NULL
);

CREATE TABLE order_items (
    id           BIGSERIAL PRIMARY KEY,
    order_id     BIGINT REFERENCES orders(id),
    product_code VARCHAR(255) NOT NULL,
    cupcake_id   BIGINT NOT NULL,
    count        INTEGER NOT NULL,
    unit_price   NUMERIC(19, 2) NOT NULL
);