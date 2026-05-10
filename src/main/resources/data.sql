-- Roles
INSERT INTO roles (name, created_at, updated_at) VALUES 
('ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('AGENT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Users (Şifre: "admin123" BCrypt ile hash'lenmiş)
-- Bu hash'i Postman veya başka bir BCrypt tool'u ile oluşturabilirsin
INSERT INTO users (username, password, email, phone, full_name, created_at, updated_at, created_by) VALUES 
('admin', '$2a$10$w0IpTfvr0m5YRVxpEhvA7eHnN2GBFVNc8IEVdZm1Uh2mlE8sGvzl2', 'admin@example.com', '+90123456789', 'Admin User', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
('agent1', '$2a$10$w0IpTfvr0m5YRVxpEhvA7eHnN2GBFVNc8IEVdZm1Uh2mlE8sGvzl2', 'agent@example.com', '+90987654321', 'Agent User', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
('user1', '$2a$10$w0IpTfvr0m5YRVxpEhvA7eHnN2GBFVNc8IEVdZm1Uh2mlE8sGvzl2', 'user@example.com', '+90555555555', 'Regular User', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);

-- User Roles
INSERT INTO user_roles (user_id, role_id) VALUES 
(1, 1),  -- admin -> ADMIN
(2, 2),  -- agent1 -> AGENT
(3, 3);  -- user1 -> USER

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

-- Sample Districts (Varsayım: İstanbul için 1, Ankara için 2, İzmir için 3)
INSERT INTO districts (name, city_id, created_at, updated_at) VALUES
('Kadıköy', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Çankaya', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Konak', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Sample Neighborhoods (Varsayım: Kadıköy için 1, Çankaya için 2, Konak için 3)
INSERT INTO neighborhood (name, district_id) VALUES
('Moda', 1),
('Bahçelievler', 2),
('Alsancak', 3);

-- Sample Floors
INSERT INTO floor (name) VALUES
('Zemin Kat'),
('1. Kat'),
('2. Kat'),
('3. Kat'),
('Çatı Katı');

-- Note: Run after tables created. Adjust IDs as needed.
