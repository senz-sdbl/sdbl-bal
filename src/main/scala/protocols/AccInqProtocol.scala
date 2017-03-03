package protocols

/**
  * Created by senz on 3/3/17.
  */

case class Agent(account: String, branch: String)

case class AccInqMsg(msgStream: Array[Byte])

case class AccInqResp(esh: String, resCode: String, authCode: String, accNumbers: String)

case class AccInq(agent: String, idNumber: String)