package protocols

case class Agent(account: String, branch: String)

case class BalInq(agent: String, customer: String, amount: Int, timestamp: String, status: String)

case class BalInqMsg(msgStream: Array[Byte])

case class BalInqResp(esh: String, status: String, rst: String)
