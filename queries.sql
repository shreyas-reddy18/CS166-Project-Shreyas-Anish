-- List the customers that have paid less than 100 dollars for repairs based on their previous service requests.
SELECT cu.fname, cu.lname, cr.date, cr.comment, cr.bill 
FROM Customer cu 
JOIN Car c ON cu.id = c.customer_id 
JOIN Service_Request sr ON c.vin = sr.vin 
JOIN Closed_Request cr ON sr.rid = cr.rid 
WHERE cr.bill < 100;

-- Find how many cars each customer has counting from the ownership relation and discover who has more than 20 cars.
SELECT cu.fname, cu.lname 
FROM Customer cu 
JOIN Car c ON cu.id = c.customer_id 
GROUP BY cu.id, cu.fname, cu.lname 
HAVING COUNT(c.vin) > 20;

-- Get the odometer from the service_requests and find all cars before 1995 having less than 50000 miles in the odometer.
SELECT DISTINCT c.make, c.model, c.year 
FROM Car c 
JOIN Service_Request sr ON c.vin = sr.vin 
WHERE c.year < 1995 AND sr.odometer < 50000;

-- Find for all cars in the database the number of service requests. Return the make, model and number of service requests for the cars having the k highest number of service requests. The k value should be positive and larger than 0. The user should provide this value. Focus on the open service requests.
SELECT c.make, c.model, COUNT(sr.rid) as request_count 
FROM Car c 
JOIN Service_Request sr ON c.vin = sr.vin 
LEFT JOIN Closed_Request cr ON sr.rid = cr.rid 
WHERE cr.rid IS NULL 
GROUP BY c.vin, c.make, c.model 
ORDER BY request_count DESC 
LIMIT ?; -- The '?' will be replaced by your 'k' value in Java

-- For all service requests find the aggregate cost per customer and order customers according to that cost. List their first, last name and total bill.
SELECT cu.fname, cu.lname, SUM(cr.bill) as total_bill 
FROM Customer cu 
JOIN Car c ON cu.id = c.customer_id 
JOIN Service_Request sr ON c.vin = sr.vin 
JOIN Closed_Request cr ON sr.rid = cr.rid 
GROUP BY cu.id, cu.fname, cu.lname 
ORDER BY total_bill DESC;