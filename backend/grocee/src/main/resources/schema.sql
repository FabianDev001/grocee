-- Create tables with correct schemas only if they don't exist
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
    FOREIGN KEY (shopping_list_id) REFERENCES shopping_list(id) ON DELETE CASCADE
);

-- User entity table (named app_user to avoid SQL keyword conflicts)
CREATE TABLE IF NOT EXISTS app_user (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
);

-- Household entity table
CREATE TABLE IF NOT EXISTS household (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Join table for many-to-many relationship between households and users
CREATE TABLE IF NOT EXISTS household_members (
    household_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (household_id, user_id),
    FOREIGN KEY (household_id) REFERENCES household(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE
); 