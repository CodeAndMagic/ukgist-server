INSERT INTO information (discriminator, area_id, validity_start, validity_end)
VALUES (:info.discriminator, :info.area.id, :info.validity.from, :info.validity.to)