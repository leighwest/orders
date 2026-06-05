ALTER TABLE cupcakes ADD COLUMN display_name VARCHAR(255) NOT NULL DEFAULT '';

UPDATE cupcakes SET display_name = 'Belgian Chocolate' WHERE product_code = 'CHOC001';
UPDATE cupcakes SET display_name = 'Classic Vanilla' WHERE product_code = 'VAN001';
UPDATE cupcakes SET display_name = 'Lemon Delight' WHERE product_code = 'LEM001';

ALTER TABLE cupcakes ALTER COLUMN display_name DROP DEFAULT;