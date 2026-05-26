CREATE TABLE IF NOT EXISTS blueprints (
    author VARCHAR(50) NOT NULL,
    name VARCHAR(50) NOT NULL,
    PRIMARY KEY (author, name)
);

CREATE TABLE IF NOT EXISTS points (
    id SERIAL PRIMARY KEY,
    author VARCHAR(50) NOT NULL,
    blueprint_name VARCHAR(50) NOT NULL,
    x INT NOT NULL,
    y INT NOT NULL,
    point_order INT NOT NULL,
    FOREIGN KEY (author, blueprint_name) REFERENCES blueprints(author, name) ON DELETE CASCADE
);
