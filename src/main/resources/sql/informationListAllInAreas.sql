SELECT info.id info_id, info.area_id info_area_id, info.discriminator info_discriminator,
    info.validity_start info_validity_start, info.validity_end info_validity_end,
    area.id area_id, area.discriminator area_discriminator, area.name area_name, area.source area_source,
    area.validity_start area_validity_start, area.validity_end area_validity_end, area.kml area_kml,
    area.police_force area_police_force, area.police_neighborhood area_police_neighborhood
FROM information info INNER JOIN areas area on info.area_id = area.id
WHERE area_id IN (:a0,:a1,:a2,:a3,:a4,:a5,:a6,:a7,:a8,:a9)
