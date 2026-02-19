-- Phase 1: Relational Schema Design
DROP TABLE IF EXISTS Closed_Request CASCADE;
DROP TABLE IF EXISTS Service_Request CASCADE;
DROP TABLE IF EXISTS Car CASCADE;
DROP TABLE IF EXISTS Mechanic CASCADE;
DROP TABLE IF EXISTS Customer CASCADE;

-- Table: Customer
CREATE TABLE Customer (
    id SERIAL PRIMARY KEY,
    fname VARCHAR(50) NOT NULL,
    lname VARCHAR(50) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    address TEXT NOT NULL
);

-- Table: Mechanic
CREATE TABLE Mechanic (
    id SERIAL PRIMARY KEY,
    fname VARCHAR(50) NOT NULL,
    lname VARCHAR(50) NOT NULL,
    experience INT NOT NULL
);

-- Table: Car
CREATE TABLE Car (
    vin VARCHAR(17) PRIMARY KEY,
    make VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    year INT NOT NULL,
    customer_id INT NOT NULL,
    
    -- Relationship: Customer 'owns' Car (1:N)
    CONSTRAINT fk_car_customer
        FOREIGN KEY (customer_id)
        REFERENCES Customer (id)
        ON DELETE CASCADE
);

-- Table: Service_Request
CREATE TABLE Service_Request (
    rid SERIAL PRIMARY KEY,
    date DATE NOT NULL,
    odometer INT NOT NULL,
    complain TEXT NOT NULL,
    vin VARCHAR(17) NOT NULL,
    mechanic_id INT NOT NULL,
    
    -- Relationship: Car 'has' Service_Request (1:N)
    CONSTRAINT fk_sr_car
        FOREIGN KEY (vin)
        REFERENCES Car (vin)
        ON DELETE CASCADE,
        
    -- Relationship: Mechanic 'initiates' Service_Request (1:N)
    CONSTRAINT fk_sr_mechanic
        FOREIGN KEY (mechanic_id)
        REFERENCES Mechanic (id)
);

-- Table: Closed Request
CREATE TABLE Closed_Request (
    wid SERIAL,
    rid INT NOT NULL,
    date DATE NOT NULL,
    comment TEXT,
    bill NUMERIC(10, 2) NOT NULL,
    mechanic_id INT NOT NULL,
    
    -- Composite Primary Key for the Weak Entity
    PRIMARY KEY (rid, wid),
    
    -- Identifying Relationship: to Service_Request
    CONSTRAINT fk_cr_sr
        FOREIGN KEY (rid)
        REFERENCES Service_Request (rid)
        ON DELETE CASCADE,
        
    -- Relationship: Mechanic 'closed_by' Closed_Request (1:N)
    CONSTRAINT fk_cr_mechanic
        FOREIGN KEY (mechanic_id)
        REFERENCES Mechanic (id)
);