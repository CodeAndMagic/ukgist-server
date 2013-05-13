SELECT area.id area_id, area.discriminator area_discriminator, area.name area_name, area.source area_source,
    area.validity_start area_validity_start, area.validity_end area_validity_end, area.kml area_kml,
    area.police_force area_police_force, area.police_neighborhood area_police_neighborhood
FROM areas area LIMIT :limit OFFSET :offset