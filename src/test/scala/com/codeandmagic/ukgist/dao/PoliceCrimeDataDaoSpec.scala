package com.codeandmagic.ukgist.dao

import org.specs2.mutable.Specification
import com.codeandmagic.ukgist.model.{PoliceArea, Information, PoliceCrimeData}
import org.specs2.mock.Mockito
import com.codeandmagic.ukgist.model.Interval.FOREVER
import com.codeandmagic.ukgist.MockBrokerComponent

/**
 * User: cvrabie
 * Date: 02/05/2013
 */
class PoliceCrimeDataDaoSpec extends Specification{
  import PoliceCrimeDataDaoFixture._

  "PoliceCrimeDataDao" should{
    "correctly fetch PoliceCrimeData with id 123" in{
      val dataMaybe = ReadMockRegistration.policeCrimeDataDao.getByInfoId(CRIME_1_ID)
      dataMaybe must beSome
      val data = dataMaybe.get
      data.allCrime must_==(CRIME_1_ALL)
      data.antiSocialBehavior must_==(CRIME_1_SOCIAL)
    }

    "correctly delete all data" in{
      val dao = DeleteMockRegistry.policeCrimeDataDao
      dao.getByInfoId(CRIME_1_ID) must beSome
      dao.deleteAll() must_== CRIMES_LEN
      dao.getByInfoId(CRIME_1_ID) must beNone
    }

    "correctly save in cascade a batch of Information and PoliceCrimeData" in{
      val dao = WriteMockRegistry.policeCrimeDataDao
      val saved = dao.saveAll(NEW_CRIMES)
      saved.size must_== NEW_CRIMES.length
    }
  }
}

object PoliceCrimeDataDaoFixture extends Mockito{
  object ReadMockRegistration extends MockBrokerComponent("police_crime_read","db_fixture/crime")
  with BrokerPoliceCrimeDataDaoComponent with BrokerInformationDaoComponent{
    val informationDao = new BrokerInformationDao
    val policeCrimeDataDao = new BrokerPoliceCrimeDataDao
  }

  object DeleteMockRegistry extends MockBrokerComponent("police_crime_delete","db_fixture/crime")
  with BrokerPoliceCrimeDataDaoComponent with BrokerInformationDaoComponent{
    val informationDao = new BrokerInformationDao
    val policeCrimeDataDao = new BrokerPoliceCrimeDataDao
  }

  object WriteMockRegistry extends MockBrokerComponent("police_crime_write","db_fixture/crime")
  with BrokerPoliceCrimeDataDaoComponent with BrokerInformationDaoComponent{
    val informationDao = new BrokerInformationDao
    val policeCrimeDataDao = new BrokerPoliceCrimeDataDao
  }

  val CRIMES_LEN = 2
  val CRIME_1_ID = 123
  val CRIME_1_ALL = 999
  val CRIME_1_SOCIAL = 88

  val DISC = PoliceCrimeData.discriminator
  val AREA_3 = mock[PoliceArea]
  AREA_3.id returns(456)
  val CRIME_3 = new PoliceCrimeData(-1, new Information(-1, DISC, AREA_3, FOREVER), 1,2,3,4,5,6,7,8,9,10,11,12)

  /*val AREA_4 = mock[PoliceArea]
  AREA_4.id returns(567)
  val CRIME_4 = new PoliceCrimeData(-1, new Information(-1, DISC, AREA_4, FOREVER), 0,0,0,0,0,0,0,0,0,0,0,0)*/


  //cannot test batch insert IN H2 with more than one item because getGeneratedKeys only returns the last one
  //although this works in MySQL @see https://code.google.com/p/h2database/issues/detail?id=357
  val NEW_CRIMES = CRIME_3 :: /*CRIME_4 ::*/ Nil
}
