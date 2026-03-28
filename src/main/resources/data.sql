-- Sample Data for PropertyTypes
INSERT INTO property_types (name, created_at, updated_at) VALUES 
('Daire', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Villa', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Arsa', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Dükkan', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Ofis', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Sample Categories
INSERT INTO categories (name, created_at, updated_at) VALUES 
('Satılık', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Kiralık', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Sample Cities
INSERT INTO cities (name, created_at, updated_at) VALUES 
('İstanbul', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Ankara', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('İzmir', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Note: Run after tables created. Adjust IDs as needed.
