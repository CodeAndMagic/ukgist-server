INSERT INTO areas (discriminator, name, source, validity_start, validity_end, kml, police_force, police_neighborhood)
VALUES (:area.discriminator, :area.name, :area.source, :area.validity.from, :area.validity.to, :area.kml, :area.policeForce, :area.policeNeighborhood)
