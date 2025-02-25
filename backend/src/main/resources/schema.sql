-- Create Users Table
CREATE TABLE users (
                       user_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                       username VARCHAR2(50) NOT NULL UNIQUE,
                       password VARCHAR2(100) NOT NULL,  -- Will store hashed passwords
                       email VARCHAR2(100) NOT NULL,
                       first_name VARCHAR2(50) NOT NULL,
                       last_name VARCHAR2(50) NOT NULL,
                       role VARCHAR2(20) NOT NULL CHECK (role IN ('EMPLOYEE', 'IT_SUPPORT'))
);

-- Create Ticket Categories Table
CREATE TABLE categories (
                            category_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                            name VARCHAR2(50) NOT NULL UNIQUE,
                            description VARCHAR2(200)
);

-- Create Priority Levels Table
CREATE TABLE priorities (
                            priority_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                            name VARCHAR2(20) NOT NULL UNIQUE,
                            description VARCHAR2(200)
);

-- Create Ticket Status Table
CREATE TABLE statuses (
                          status_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                          name VARCHAR2(20) NOT NULL UNIQUE,
                          description VARCHAR2(200)
);

-- Create Tickets Table
CREATE TABLE tickets (
                         ticket_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                         title VARCHAR2(100) NOT NULL,
                         description VARCHAR2(4000) NOT NULL,
                         created_by NUMBER NOT NULL,
                         assigned_to NUMBER,
                         category_id NUMBER NOT NULL,
                         priority_id NUMBER NOT NULL,
                         status_id NUMBER NOT NULL,
                         created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                         last_updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                         CONSTRAINT fk_tickets_created_by FOREIGN KEY (created_by) REFERENCES users(user_id),
                         CONSTRAINT fk_tickets_assigned_to FOREIGN KEY (assigned_to) REFERENCES users(user_id),
                         CONSTRAINT fk_tickets_category FOREIGN KEY (category_id) REFERENCES categories(category_id),
                         CONSTRAINT fk_tickets_priority FOREIGN KEY (priority_id) REFERENCES priorities(priority_id),
                         CONSTRAINT fk_tickets_status FOREIGN KEY (status_id) REFERENCES statuses(status_id)
);

-- Create Comments Table
CREATE TABLE comments (
                          comment_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                          ticket_id NUMBER NOT NULL,
                          user_id NUMBER NOT NULL,
                          comment_text VARCHAR2(4000) NOT NULL,
                          created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                          CONSTRAINT fk_comments_ticket FOREIGN KEY (ticket_id) REFERENCES tickets(ticket_id),
                          CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Create Audit Log Table
CREATE TABLE audit_logs (
                            log_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                            ticket_id NUMBER NOT NULL,
                            user_id NUMBER NOT NULL,
                            action_type VARCHAR2(50) NOT NULL,
                            old_value VARCHAR2(4000),
                            new_value VARCHAR2(4000),
                            action_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                            CONSTRAINT fk_audit_logs_ticket FOREIGN KEY (ticket_id) REFERENCES tickets(ticket_id),
                            CONSTRAINT fk_audit_logs_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

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

-- Create indexes for better performance
CREATE INDEX idx_tickets_created_by ON tickets(created_by);
CREATE INDEX idx_tickets_status_id ON tickets(status_id);
CREATE INDEX idx_tickets_category_id ON tickets(category_id);
CREATE INDEX idx_tickets_priority_id ON tickets(priority_id);
CREATE INDEX idx_comments_ticket_id ON comments(ticket_id);
CREATE INDEX idx_audit_logs_ticket_id ON audit_logs(ticket_id);