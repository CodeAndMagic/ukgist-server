package com.codeandmagic.ukgist.dao

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import com.codeandmagic.ukgist.model.{PoliceArea, Area}
import com.codeandmagic.ukgist.MockBrokerComponent

/**
 * User: cvrabie
 * Date: 28/04/2013
 */
class InformationDaoSpec extends Specification{
  import InformationDaoFixture._
  "InformationDao" should{
    val infos = MockRegistry.informationDao.listAllInAreas(INFO_AREA_LIST)

    "list all items that are withing a specific area list" in{
      infos.map(_.id) must beEqualTo(INFO_ID_LIST)
      infos.map(_.area.id) must beEqualTo(INFO_AREA_ID_LIST)
    }
  }
}

object InformationDaoFixture extends Mockito{
  PoliceArea.discriminator //for discriminator assignment

  object MockRegistry extends MockBrokerComponent("test_info_read","db_fixture/crime") with BrokerInformationDaoComponent{
    val informationDao = new BrokerInformationDao
  }

  val INFO_AREA_1_ID = 456
  val INFO_AREA_2_ID = 567
  val INFO_AREA_3_ID = 678
  val INFO_AREA_ID_LIST = INFO_AREA_1_ID :: INFO_AREA_2_ID :: Nil
  val INFO_AREA_LIST = INFO_AREA_ID_LIST.map( id => {
    val area = mock[Area]
    area.id returns(id)
    area
  })
  val INFO_1_ID = 123
  val INFO_2_ID = 234
  val INFO_3_ID = 345
  val INFO_ID_LIST = INFO_1_ID :: INFO_2_ID :: Nil
}
