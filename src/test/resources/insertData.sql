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
INSERT INTO cupcakes (productCode, flavour) VALUES ('STRAW001', 'STRAWBERRY');




--
--
--     Cupcake chocolateCupcake = new Cupcake(1L, "CHOC001", Cupcake.Flavour.CHOCOLATE);
-- Cupcake vanillaCupcake = new Cupcake(2L, "VAN001", Cupcake.Flavour.VANILLA);
--         Cupcake strawberryCupcake = new Cupcake(3L, "STRAW001", Cupcake.Flavour.STRAWBERRY);