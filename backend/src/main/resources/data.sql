
-- Insert initial data for statuses
INSERT INTO statuses (name, description) VALUES ('NEW', 'Ticket has been created but not yet processed');
INSERT INTO statuses (name, description) VALUES ('IN_PROGRESS', 'Ticket is currently being worked on');
INSERT INTO statuses (name, description) VALUES ('RESOLVED', 'Issue has been resolved');

-- Insert initial data for categories
INSERT INTO categories (name, description) VALUES ('NETWORK', 'Network-related issues');
INSERT INTO categories (name, description) VALUES ('HARDWARE', 'Hardware-related issues');
INSERT INTO categories (name, description) VALUES ('SOFTWARE', 'Software-related issues');
INSERT INTO categories (name, description) VALUES ('OTHER', 'Other miscellaneous issues');

-- Insert initial data for priorities
INSERT INTO priorities (name, description) VALUES ('LOW', 'Non-urgent issue');
INSERT INTO priorities (name, description) VALUES ('MEDIUM', 'Important but not critical issue');
INSERT INTO priorities (name, description) VALUES ('HIGH', 'Critical issue requiring immediate attention');

-- Insert sample users (password is 'password' hashed with BCrypt)
INSERT INTO users (username, password, email, first_name, last_name, role)
VALUES ('employee1', '$2a$10$aDipcN7q0vw6OuQvy3NR.eE1.KcXpzDH0ew0XdOi0JZ6CIuKaHJUi', 'employee1@example.com', 'John', 'Doe', 'EMPLOYEE');

INSERT INTO users (username, password, email, first_name, last_name, role)
VALUES ('itsupport1', '$2a$10$aDipcN7q0vw6OuQvy3NR.eE1.KcXpzDH0ew0XdOi0JZ6CIuKaHJUi', 'itsupport1@example.com', 'Jane', 'Smith', 'IT_SUPPORT');
