ALTER TABLE order_items
    ADD CONSTRAINT fk_order_items_cupcake
    FOREIGN KEY (cupcake_id) REFERENCES cupcakes(id);