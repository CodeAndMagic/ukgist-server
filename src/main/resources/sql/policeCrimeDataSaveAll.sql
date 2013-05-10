INSERT INTO police_crime_data (information_id, all_crime, anti_social_behavior, burglary, criminal_damage, drugs,
 other_theft, public_disorder, robbery, shoplifting, vehicle_crime, violent_crime, other_crime)
VALUES (:data.information.id, :data.allCrime, :data.antiSocialBehavior, :data.burglary, :data.criminalDamage, :data.drugs,
        :data.otherTheft, :data.publicDisorder, :data.robbery, :data.shoplifting, :data.vehicleCrime, :data.violentCrime,
        :data.otherCrime)