-- Indexes to speed up foreign key joins
CREATE INDEX idx_car_customer ON Car(customer_id);
CREATE INDEX idx_sr_vin ON Service_Request(vin);
CREATE INDEX idx_cr_rid ON Closed_Request(rid);

-- Index for searching customers by last name (used in "Initiate a Service Request")
CREATE INDEX idx_customer_lname ON Customer(lname);

-- Index on the bill amount for the "requests lower than 100" query
CREATE INDEX idx_cr_bill ON Closed_Request(bill);