-- mechanics_shop_schema.sql
-- Phase 1: Relational Schema Design for Mechanics Shop

-- Drop tables if they exist to ensure a clean slate for re-running the script
DROP TABLE IF EXISTS service_request CASCADE;
DROP TABLE IF EXISTS car CASCADE;
DROP TABLE IF EXISTS mechanic CASCADE;
DROP TABLE IF EXISTS customer CASCADE;

-- Table: Customer
CREATE TABLE customer (
    customer_id SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    address TEXT NOT NULL
);

-- Table: Mechanic
CREATE TABLE mechanic (
    employee_id SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    years_experience INT NOT NULL DEFAULT 0
);

-- Table: Car
CREATE TABLE car (
    vin VARCHAR(17) PRIMARY KEY,
    make VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    year INT NOT NULL,
    customer_id INT NOT NULL,
    
    -- Constraint: A car has exactly one owner (enforced by NOT NULL FK)
    CONSTRAINT fk_car_owner
        FOREIGN KEY (customer_id)
        REFERENCES customer (customer_id)
        ON DELETE CASCADE
);

-- Table: Service_Request
CREATE TABLE service_request (
    request_id SERIAL PRIMARY KEY,
    vin VARCHAR(17) NOT NULL,
    mechanic_id INT NOT NULL,
    date_brought_in DATE NOT NULL,
    odometer INT NOT NULL,
    complaint TEXT NOT NULL,
    
    -- Closing information (Nullable because request starts as open)
    closing_date DATE,
    comment TEXT,
    bill NUMERIC(10, 2), -- Supports amounts up to 99,999,999.99
    
    -- Constraint: Link to Car
    CONSTRAINT fk_request_car
        FOREIGN KEY (vin)
        REFERENCES car (vin)
        ON DELETE CASCADE,
        
    -- Constraint: Link to Mechanic
    CONSTRAINT fk_request_mechanic
        FOREIGN KEY (mechanic_id)
        REFERENCES mechanic (employee_id)
        ON DELETE SET NULL,

    -- Logical Constraint: Closing date must be after or equal to brought in date
    CONSTRAINT check_dates 
        CHECK (closing_date >= date_brought_in)
);

-- -----------------------------------------------------
-- Special Indexes & Constraints
-- -----------------------------------------------------

-- REQUIREMENT: "Mechanics work on a single car at a time."
-- We enforce this using a unique index on the mechanic_id where the request is NOT closed.
-- This prevents a mechanic from being assigned to a second request if they have one that is currently open (closing_date is NULL).
CREATE UNIQUE INDEX idx_single_active_mechanic 
ON service_request (mechanic_id) 
WHERE closing_date IS NULL;