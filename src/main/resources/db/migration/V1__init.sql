-- Migration initiale : création de la table products
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    price NUMERIC(10,2) NOT NULL,
    quantity INTEGER NOT NULL
);

CREATE INDEX idx_products_quantity ON products(quantity);
