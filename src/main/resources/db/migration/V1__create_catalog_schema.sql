CREATE TABLE product_prices (
  id VARCHAR(64) PRIMARY KEY,
  price_in_cents BIGINT NOT NULL CHECK (price_in_cents >= 0)
);

CREATE TABLE product_availability (
  id VARCHAR(64) PRIMARY KEY,
  available_stock INTEGER NOT NULL CHECK (available_stock >= 0),
  available BOOLEAN NOT NULL
);

CREATE TABLE categories (
  id VARCHAR(64) PRIMARY KEY,
  name VARCHAR(120) NOT NULL UNIQUE
);

CREATE TABLE products (
  id VARCHAR(64) PRIMARY KEY,
  name VARCHAR(200) NOT NULL,
  description TEXT NOT NULL,
  active BOOLEAN NOT NULL,
  current_price_id VARCHAR(64) NOT NULL REFERENCES product_prices(id),
  availability_id VARCHAR(64) NOT NULL REFERENCES product_availability(id)
);

CREATE TABLE product_categories (
  product_id VARCHAR(64) NOT NULL REFERENCES products(id) ON DELETE CASCADE,
  category_id VARCHAR(64) NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
  PRIMARY KEY (product_id, category_id)
);
