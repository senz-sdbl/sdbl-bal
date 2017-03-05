package protocols

/**
  * Created by senz on 3/3/17.
  */

case class MiniStatInq(agent: String, customer: String)

case class MiniStatInqMsg(msgStream: Array[Byte])

case class MiniStatInqResp(esh: String, resCode: String, authCode: String, statementDetails: String)