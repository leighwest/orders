CREATE TABLE images (
                        id                  LONG NOT NULL AUTO_INCREMENT PRIMARY KEY,
                        productCode         VARCHAR(50) NOT NULL UNIQUE,
                        bucketName          VARCHAR(255) NOT NULL,
                        objectKey           VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE order_items (
                             id             LONG NOT NULL AUTO_INCREMENT,
                             productCode    VARCHAR(50) NOT NULL,
                             cupcakeId      LONG NOT NULL,
                             count          INT NOT NULL
);

CREATE TABLE cupcakes (
    id               LONG NOT NULL AUTO_INCREMENT,
    productCode      VARCHAR(20) NOT NULL,
    flavour          VARCHAR(20) NOT NULL,
    price            DECIMAL(10, 2) NOT NULL,
    image            VARCHAR(20),
    CONSTRAINT fk_image FOREIGN KEY (image) REFERENCES images(productCode)
);

INSERT INTO images (productCode, bucketName, objectKey) VALUES ('CHOC001', 'bucket1', 'choc_key');
INSERT INTO images (productCode, bucketName, objectKey) VALUES ('VAN001', 'bucket1', 'van_key');
INSERT INTO images (productCode, bucketName, objectKey) VALUES ('LEM001', 'bucket1', 'lem_key');

INSERT INTO cupcakes (productCode, flavour, price, image) VALUES ('CHOC001', 'CHOCOLATE', 3.50, CHOC001);
INSERT INTO cupcakes (productCode, flavour, price, image) VALUES ('VAN001', 'VANILLA', 3.50, VAN001);
INSERT INTO cupcakes (productCode, flavour, price, image) VALUES ('LEM001', 'LEMON', 3.50, LEM001);