CREATE TABLE order_items (
                             id             LONG NOT NULL AUTO_INCREMENT,
                             productCode    VARCHAR(50) NOT NULL,
                             cupcakeId      LONG NOT NULL,
                             count          INT NOT NULL
);

CREATE TABLE cupcakes (
    id               LONG NOT NULL AUTO_INCREMENT,
    productCode      VARCHAR(20) NOT NULL,
    flavour          VARCHAR(20) NOT NULL
);

INSERT INTO cupcakes (productCode, flavour) VALUES ('CHOC001', 'CHOCOLATE');
INSERT INTO cupcakes (productCode, flavour) VALUES ('VAN001', 'VANILLA');
INSERT INTO cupcakes (productCode, flavour) VALUES ('LEM001', 'LEMON');