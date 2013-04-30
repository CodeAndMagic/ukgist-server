SELECT info.id info_id, info.area_id info_area_id, info.discriminator info_discriminator,
       info.validity_start info_validity_start, info.validity_end info_validity_end,
       area.id area_id, area.discriminator area_discriminator, area.name area_name, area.source area_source,
       area.validity_start area_validity_start, area.validity_end area_validity_end, area.kml area_kml,
       area.police_force area_police_force, area.police_neighborhood area_police_neighborhood,
       infox.id infox_id, infox.all_crime infox_all_crime, infox.anti_social_behavior infox_anti_social_behavior,
       infox.burglary infox_burglary, infox.criminal_damage infox_criminal_damage, infox.drugs infox_drugs,
       infox.other_theft infox_other_theft, infox.public_disorder infox_public_disorder, infox.robbery infox_robbery,
       infox.shoplifting infox_shoplifting, infox.vehicle_crime infox_vehicle_crime,
       infox.violent_crime infox_violent_crime, infox.other_crime infox_other_crime
FROM police_crime_data infox INNER JOIN information info ON infox.information_id=info.id
    INNER JOIN areas area ON info.area_id = area.id
WHERE info.id = :id