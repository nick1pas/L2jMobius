-- Remove Schuttgart teleports.
DELETE FROM `teleport` WHERE `Description` like '%Schuttgart%';

-- Heretics Catacomb.
UPDATE teleport SET loc_x = 43375, loc_y = 143937, loc_z = -5380 WHERE Description = "Cat Heretics Entrance";

-- Apostate Catacomb 
UPDATE teleport SET loc_x = 78055, loc_y = 78405, loc_z = -5128 WHERE Description = "Cat Apostate Entrance";
