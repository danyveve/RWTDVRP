INSERT INTO user_role(name) VALUES
  ('CLIENT'),
  ('ADMIN'),
  ('DEVELOPER');

INSERT INTO user(username, email, password, phone, first_name, last_name, role_id) VALUES
  ('client', 'a1@gmail.com', '$2a$10$9AbAkMsx3HacjGlN4GkgoOQX7h9/kBE/US3/tE/cMbD0q9RhXl29e', '+22222', 'first_name', 'last_name', 1),
  ('admin', 'a2@gmail.com', '$2a$10$jNT5Psj6PtHAIyVTEGisyu1qmpFe4UyBMj.1vze1pOErDXhSaPQBK', '+11111', 'fn', 'ln', 2),
  ('developer', 'a3@gmail.com', '$2a$10$.IvBlp4IfsV5wGJlpqSYOej4/vvEYXlsmwNTjrS0/BpGg6AQgrjJS', '+00000', 'fn', 'ln', 3);

INSERT INTO driver(first_name, last_name, phone, email, car) VALUES
  ('fn1', 'ln1', '+0744444444', 'my-email@gmail.com', 'Dacia 1310, 1980'),
  ('fn2', 'ln2', '+0755555555', 'her-email@gmail.com', 'Skoda Octavia, 2003'),
  ('fn3', 'ln3', '+0766666666', 'his-email@gmail.com', 'Opel Astra, 2007');

--   passwords: client -> client, admin-> admin, danyveve->developer;
