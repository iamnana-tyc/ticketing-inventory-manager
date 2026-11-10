-- Sample data for local development and API testing.
-- This script inserts dummy customers, venues, and events.
-- Run this after applying the database migrations.

INSERT INTO customers (name, email, address)
VALUES (
           'John Doe',
           'john.doe@example.com',
           '123 Main Street, Berlin'
       ),
       (
           'Jane Smith',
           'jane.smith@example.com',
           '45 Park Avenue, Hamburg'
       ),
       (
           'Michael Brown',
           'michael.brown@example.com',
           '78 River Road, Munich'
       ),
       (
           'Emily Davis',
           'emily.davis@example.com',
           '12 Oak Street, Frankfurt'
       ),
       (
           'David Wilson',
           'david.wilson@example.com',
           '90 Lake Road, Cologne'
       );
INSERT INTO venues (venue_name, total_capacity, address)
VALUES (
           'Berlin Conference Center',
           500,
           '10 Alexanderplatz, Berlin'
       ),
       (
           'Munich Concert Hall',
           1200,
           '25 Ludwigstrasse, Munich'
       ),
       ('Hamburg Arena', 3000, '50 Reeperbahn, Hamburg'),
       (
           'Frankfurt Exhibition Hall',
           2000,
           '15 Messeplatz, Frankfurt'
       ),
       (
           'Cologne Community Theater',
           350,
           '8 Theaterstrasse, Cologne'
       );
INSERT INTO events (
    event_name,
    total_capacity,
    left_capacity,
    "venueId"
)
VALUES (
           'Java Developer Conference',
           500,
           500,
           (
               SELECT venue_id
               FROM venues
               WHERE venue_name = 'Berlin Conference Center'
           )
       ),
       (
           'Rock Music Festival',
           1200,
           1200,
           (
               SELECT venue_id
               FROM venues
               WHERE venue_name = 'Munich Concert Hall'
           )
       ),
       (
           'Summer Music Festival',
           3000,
           3000,
           (
               SELECT venue_id
               FROM venues
               WHERE venue_name = 'Hamburg Arena'
           )
       ),
       (
           'Technology Expo 2026',
           2000,
           2000,
           (
               SELECT venue_id
               FROM venues
               WHERE venue_name = 'Frankfurt Exhibition Hall'
           )
       ),
       (
           'Comedy Night',
           350,
           350,
           (
               SELECT venue_id
               FROM venues
               WHERE venue_name = 'Cologne Community Theater'
           )
       );