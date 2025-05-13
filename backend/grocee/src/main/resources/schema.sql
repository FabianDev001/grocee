-- Drop tables if they exist to avoid conflicts
DROP TABLE IF EXISTS shopping_list_item;
DROP TABLE IF EXISTS product_template;
DROP TABLE IF EXISTS shopping_list;

-- Create tables with correct schemas
CREATE TABLE IF NOT EXISTS shopping_list (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255),
    dtype VARCHAR(10)
);

CREATE TABLE IF NOT EXISTS product_template (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(255) NOT NULL,
    brand VARCHAR(255) NOT NULL,
    category VARCHAR(255) NOT NULL,
    UNIQUE(name, brand, category)
);

CREATE TABLE IF NOT EXISTS shopping_list_item (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    product_template_id INTEGER NOT NULL,
    shopping_list_id VARCHAR(36) NOT NULL,
    expiration_date TEXT,
    price REAL DEFAULT 0.0,
    needed_by VARCHAR(255),
    bought_by VARCHAR(255),
    is_paid BOOLEAN DEFAULT 0,
    FOREIGN KEY (product_template_id) REFERENCES product_template(id),
    FOREIGN KEY (shopping_list_id) REFERENCES shopping_list(id)
); 