package protocols

case class BalInq(agent: String, customer: String)

case class BalInqMsg(msgStream: Array[Byte])

case class BalInqResp(esh: String, status: String, authCode:String, rst: String)
